package io.spring.initializr.generator.language.java;

import java.util.HashMap;
import java.util.Map;

public class JavaHardCodeExpression extends JavaStatement {
	
		
	static class SwaggerData{
		public static Map<String,String> data;
		
		static {
			data = new HashMap<>();
			//Swagger dependencies
			data.put("metadata", "final ApiInfo apiInfo = new ApiInfo(API_DESCRIPTION, API_NAME, VERSION, EMPTY_STRING,\r\n" + 
					"				new Contact(NET_INSIGHT, NET_INSIGHT_URL, NET_INSIGHT_EMAIL),\r\n" + 
					"				NET_INSIGHT, EMPTY_STRING, new ArrayList<VendorExtension>());\r\n" + 
					"		return apiInfo");
			data.put("api", "final Parameter autherizationParam = new ParameterBuilder().name(AUTHORIZATION)\r\n" + 
					"				.description(TOKEN_MSG).modelRef(new ModelRef(TYPE_STR)).parameterType(HEADER)\r\n" + 
					"				.required(false).build();\r\n" + 
					"		Parameter headerParam = new ParameterBuilder().name(CONTENT_TYPE).modelRef(new ModelRef(TYPE_STR)).parameterType(HEADER).required(true).defaultValue(MediaType.APPLICATION_JSON_VALUE).build();\r\n" + 
					"        \r\n" + 
					"		return new Docket(DocumentationType.SWAGGER_2).select().apis(RequestHandlerSelectors.any())\r\n" + 
					"				.paths(PathSelectors.any()).paths(Predicates.not(PathSelectors.regex(ERROR_PATH))).paths(Predicates.not(PathSelectors.regex(ACTUATOR_PATH)))\r\n" + 
					"				.build().apiInfo(metadata())\r\n" + 
					"				.pathMapping(BACK_SLASH).globalOperationParameters(Arrays.asList(headerParam,autherizationParam))\r\n" + 
					"	");
			data.put("addResourceHandlers", "\r\n" + 
					"\r\n" + 
					"		WebMvcConfigurer.super.addResourceHandlers(registry);\r\n" + 
					"		registry.addResourceHandler(SWAGGER_UI).addResourceLocations(RESOURCE_LOCATION);\r\n" + 
					"		registry.addResourceHandler(WEB_JARS).addResourceLocations(WEB_JARS_LOCATION)\r\n" + 
					"	");
		}
	}
	
	static class RedisData{
		public static Map<String,String> data;
		
		static {
			data = new HashMap<>();
			
			data.put("jedisConnectionFactory", "RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration(REDIS_HOSTNAME, REDIS_PORT);\r\n" + 
					"				configuration.setPassword(REDIS_PASSWORD);\r\n" +
					"				JedisClientConfiguration jedisClientConfiguration = JedisClientConfiguration.builder().usePooling().build();\r\n" + 
					"				JedisConnectionFactory factory = new JedisConnectionFactory(configuration, jedisClientConfiguration);\r\n" + 
					"				factory.afterPropertiesSet();\r\n" + 
					"		return factory");
			data.put("redisTemplate", "final RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();\r\n" + 
					"		template.setConnectionFactory(jedisConnectionFactory);\r\n" +
					"		template.setValueSerializer(new GenericToStringSerializer<Object>(Object.class));\r\n" + 
					"		return template");
			data.put("redisCacheManager", "RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()\r\n" + 
					"		.disableCachingNullValues()\r\n" +
					"		.entryTtl(Duration.ofSeconds(expirationTimeout));\r\n" +
					"		redisCacheConfiguration.usePrefix();\r\n" +
					"		return RedisCacheManager.RedisCacheManagerBuilder.fromConnectionFactory(jedisConnectionFactory)\r\n" + 
					"		.cacheDefaults(redisCacheConfiguration).build()");
			data.put("errorHandler", "return new RedisCacheErrorHandler()");
			data.put("handleCacheGetError", "log.info(\"Unable to get from cache \" + cache.getName() + \" : \" + exception.getMessage())");
			data.put("handleCachePutError", "log.info(\"Unable to put into cache \" + cache.getName() + \" : \" + exception.getMessage())");
			data.put("handleCacheEvictError", "log.info(\"Unable to evict from cache \" + cache.getName() + \" : \" + exception.getMessage())");
			data.put("handleCacheClearError", "log.info(\"Unable to clean cache \" + cache.getName() + \" : \" + exception.getMessage())");
		}
	}
	
	static class MessageSourceUtilData{
		public static Map<String,String> data;
		
		static {
			data = new HashMap<>();
			
			data.put("setMessageSource", "this.messageSource = messageSource");
			data.put("getLocalisedText", "return messageSource.getMessage(\r\n" + 
					"		baseKey + module + errorSeperator + errorCode + errorSeperator + errorMessage,\r\n" +
					"		new Object[0], LocaleContextHolder.getLocale())");
			
		}
	}

	static class GlobalExceptionHandler{
		public static Map<String,String> data;
		
		static {
			data = new HashMap<>();
						
			data.put("handleApplicationException", "BaseException e = (BaseException) ex;\r\n" + 
					"		logger.error(\"ApplicationException Occured:: URL=\" + request.getDescription(true));\r\n" +
					"		logger.error(\"ApplicationException Occured::\" + ex);\r\n" +
					"		return getCustomExceptionResponse(request, e)");
			data.put("handleBusinessException", "BaseException e = (BaseException) ex;\r\n" + 
					"		logger.error(\"BusinessException Occured:: URL=\" + request.getDescription(true));\r\n" +
					"		logger.error(\"BusinessException Occured::\" + ex);\r\n" +
					"		return getCustomExceptionResponse(request, e)");
			data.put("handleException", "BaseException e = new BaseException(HttpStatus.INTERNAL_SERVER_ERROR, ExceptionConstants.GENERAL_ERROR_CODE,\r\n" + 
					"		ExceptionConstants.GENERAL_MODULE, \"RuntimeException :: \" + ex.getMessage());\r\n" +
					"		logger.error(\"Exception Occured:: URL=\" + request.getDescription(true));\r\n" +
					"		logger.error(\"Exception Occured::\" + ex);\r\n" +
					"		return getCustomExceptionResponse(request, e)");
			data.put("getCustomExceptionResponse", "String errorCode = ex.getErrorCode();\r\n" + 
					"		String exceptionMessage = ex.getExceptionMessage();\r\n" +
					"		String timeStamp = ex.getTimeStamp();\r\n" +
					"		String errorModule = ex.getErrorModule();\r\n" +
					"		String errorMessage = \"\";\r\n" +
					"		Integer id = RandomUtils.nextInt(10000, 50000);\r\n" +
					"		try {\r\n" +
					"		errorMessage = messageSourceUtil.getLocalisedText(errorCode, errorModule);\r\n" +
					"		} catch (Exception e) {\r\n" +
					"		errorMessage = messageSourceUtil.getLocalisedText(ExceptionConstants.GENERAL_ERROR_CODE,\r\n" +
					"		ExceptionConstants.GENERAL_MODULE);\r\n" +
					"		exceptionMessage = \"The message for errorCode:\" + errorCode + \" module:\" + errorModule\r\n" +
					"		+ \" is not found in prop file\";\r\n" +
					"		}\r\n" +
					"		HttpStatus status = ex.getHttpStatus();\r\n" +					
					"		return new ResponseEntity<>(new ApiError(id, errorMessage, errorCode, timeStamp, exceptionMessage), status)");
			
		}
	}
	
	static class BaseExceptionData{
		public static Map<String,String> data;
		
		static {
			data = new HashMap<>();
			data.put("BaseException", "this.id = RandomUtils.nextInt(1001, 5000);");
		}
	}
	
	static class ApplicationExceptionData{
		public static Map<String,String> data;
		
		static {
			data = new HashMap<>();
			
			data.put("ApplicationException", "super(errorCode, errorModule, exceptionMessage);\r\n" + 
					"		this.errorCode = errorCode;\r\n" + 
					"		this.errorModule = errorModule;\r\n" + 
					"		this.exceptionMessage = exceptionMessage;\r\n" + 
					"	}\r\n" + 
					"	public ApplicationException(HttpStatus httpStatus, String errorCode, String errorModule, String exceptionMessage,\r\n" + 
					"			String timeStamp) {\r\n" + 
					"		super(httpStatus, errorCode, errorModule, exceptionMessage,timeStamp);\r\n" + 
					"		this.httpStatus = httpStatus;\r\n" + 
					"		this.errorCode = errorCode;\r\n" + 
					"		this.errorModule = errorModule;\r\n" + 
					"		this.exceptionMessage = exceptionMessage;\r\n" + 
					"		this.timeStamp = timeStamp;\r\n" + 
					"	}\r\n" + 
					"	public ApplicationException(HttpStatus httpStatus, String errorCode, String exceptionMessage) {\r\n" + 
					"		super(httpStatus, errorCode, exceptionMessage);\r\n" + 
					"		this.httpStatus = httpStatus;\r\n" + 
					"		this.errorCode = errorCode;\r\n" + 
					"		this.exceptionMessage = exceptionMessage;\r\n" + 
					"	}\r\n" + 
					"	public ApplicationException(HttpStatus httpStatus, String errorCode,String errorModule, String exceptionMessage) {\r\n" + 
					"		super(httpStatus, errorCode, exceptionMessage);\r\n" + 
					"		this.httpStatus = httpStatus;\r\n" + 
					"		this.errorCode = errorCode;\r\n" + 
					"		this.errorModule = errorModule;\r\n" + 
					"		this.exceptionMessage = exceptionMessage;\r\n" + 
					"	}\r\n" + 
					"	public ApplicationException(HttpStatus httpStatus, String errorCode) {\r\n" + 
					"		super(httpStatus, errorCode);\r\n" + 
					"		this.httpStatus = httpStatus;\r\n" + 
					"		this.errorCode = errorCode;\r\n" + 
					"	}\r\n" + 
					"	public ApplicationException(String errorCode, String exceptionMessage) {\r\n" + 
					"		super(errorCode, exceptionMessage);\r\n" + 
					"		this.errorCode = errorCode;\r\n" + 
					"		this.exceptionMessage = exceptionMessage");
			
		}
	}
}
