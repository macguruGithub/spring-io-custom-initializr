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

}
