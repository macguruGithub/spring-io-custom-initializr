/*
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.spring.initializr.generator.spring.code.java;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.spring.initializr.generator.condition.ConditionalOnPackaging;
import io.spring.initializr.generator.condition.ConditionalOnPlatformVersion;
import io.spring.initializr.generator.condition.ConditionalOnRequestedDependency;
import io.spring.initializr.generator.language.Annotation;
import io.spring.initializr.generator.language.Parameter;
import io.spring.initializr.generator.language.java.JavaExpressionStatement;
import io.spring.initializr.generator.language.java.JavaFieldDeclaration;
import io.spring.initializr.generator.language.java.JavaGetterCustomizer;
import io.spring.initializr.generator.language.java.JavaHardCodeExpression;
import io.spring.initializr.generator.language.java.JavaMethodDeclaration;
import io.spring.initializr.generator.language.java.JavaMethodInvocation;
import io.spring.initializr.generator.language.java.JavaObjectCreation;
import io.spring.initializr.generator.language.java.JavaReturnStatement;
import io.spring.initializr.generator.language.java.JavaSetterCustomizer;
import io.spring.initializr.generator.language.java.JavaStaticClassDeclaration;
import io.spring.initializr.generator.language.java.JavaTypeDeclaration;
import io.spring.initializr.generator.packaging.war.WarPackaging;
import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.spring.code.CustomApplicationTypeCustomizer;
import io.spring.initializr.generator.spring.code.MainApplicationTypeCustomizer;
import io.spring.initializr.generator.spring.code.ServletInitializerCustomizer;
import io.spring.initializr.generator.spring.code.TestApplicationTypeCustomizer;
import io.spring.initializr.generator.spring.code.custom.ApiErrorCustomizer;
import io.spring.initializr.generator.spring.code.custom.ApplicationExceptionCustomizer;
import io.spring.initializr.generator.spring.code.custom.BaseExceptionCustomizer;
import io.spring.initializr.generator.spring.code.custom.BusinessExceptionCustomizer;
import io.spring.initializr.generator.spring.code.custom.ExceptionConstantsCustomizer;
import io.spring.initializr.generator.spring.code.custom.GlobalExceptionHandlerCustomizer;
import io.spring.initializr.generator.spring.code.custom.MessageSourceUtilCustomizer;
import io.spring.initializr.generator.spring.code.custom.RedisCustomizer;
import io.spring.initializr.generator.spring.code.custom.SwaggerCustomizer;

/**
 * Default Java language contributors.
 *
 * @author Andy Wilkinson
 * @author Stephane Nicoll
 */
@Configuration
class JavaProjectGenerationDefaultContributorsConfiguration {
	
	

	@Bean
	MainApplicationTypeCustomizer<JavaTypeDeclaration> mainMethodContributor() {
		return (typeDeclaration) -> {
			typeDeclaration.modifiers(Modifier.PUBLIC);
			typeDeclaration.addMethodDeclaration(
					JavaMethodDeclaration.method("main").modifiers(Modifier.PUBLIC | Modifier.STATIC).returning("void")
							.parameters(new Parameter("java.lang.String[]", "args"))
							.body(new JavaExpressionStatement(
									new JavaMethodInvocation("org.springframework.boot.SpringApplication", "run",
											typeDeclaration.getName() + ".class", "args"))));
		};
	}

	// Place to add Custom Bean in the main application file
	@Bean
	@ConditionalOnRequestedDependency("veracode-scanner-id")
	CustomApplicationTypeCustomizer<JavaTypeDeclaration> jedisConnectionFactoryMethodContributor() {
		return (typeDeclaration) -> {
			typeDeclaration.modifiers(Modifier.PUBLIC);
			typeDeclaration.addMethodDeclaration(JavaMethodDeclaration.method("jedisConnectionFactory")
					.returning("org.springframework.data.redis.connection.jedis.JedisConnectionFactory").parameters()
					.body(new JavaReturnStatement(new JavaObjectCreation(
							"org.springframework.data.redis.connection.jedis.JedisConnectionFactory"))));
		};
	}

	@Bean
	@ConditionalOnPlatformVersion("[1.5.0.RELEASE,2.2.0.M3)")
	TestApplicationTypeCustomizer<JavaTypeDeclaration> junit4TestMethodContributor() {
		return (typeDeclaration) -> {
			typeDeclaration.modifiers(Modifier.PUBLIC);
			JavaMethodDeclaration method = JavaMethodDeclaration.method("contextLoads").modifiers(Modifier.PUBLIC)
					.returning("void").body();
			method.annotate(Annotation.name("org.junit.Test"));
			typeDeclaration.addMethodDeclaration(method);
		};
	}

	@Bean
	@ConditionalOnPlatformVersion("2.2.0.M3")
	TestApplicationTypeCustomizer<JavaTypeDeclaration> junitJupiterTestMethodContributor() {
		return (typeDeclaration) -> {
			JavaMethodDeclaration method = JavaMethodDeclaration.method("contextLoads").returning("void").body();
			method.annotate(Annotation.name("org.junit.jupiter.api.Test"));
			typeDeclaration.addMethodDeclaration(method);
		};
	}

	/**
	 * Java source code contributions for projects using war packaging.
	 */
	@Configuration
	@ConditionalOnPackaging(WarPackaging.ID)
	static class WarPackagingConfiguration {

		@Bean
		ServletInitializerCustomizer<JavaTypeDeclaration> javaServletInitializerCustomizer(
				ProjectDescription description) {
			return (typeDeclaration) -> {
				typeDeclaration.modifiers(Modifier.PUBLIC);
				JavaMethodDeclaration configure = JavaMethodDeclaration.method("configure")
						.modifiers(Modifier.PROTECTED)
						.returning("org.springframework.boot.builder.SpringApplicationBuilder")
						.parameters(new Parameter("org.springframework.boot.builder.SpringApplicationBuilder",
								"application"))
						.body(new JavaReturnStatement(new JavaMethodInvocation("application", "sources",
								description.getApplicationName() + ".class")));
				configure.annotate(Annotation.name("java.lang.Override"));
				typeDeclaration.addMethodDeclaration(configure);
			};
		}

	}
	
	@Configuration
	@ConditionalOnRequestedDependency("swagger-id")
	static class SwaggerConfiguration{
		
		@Bean
		SwaggerCustomizer<JavaTypeDeclaration> addResourceHandlersCustomizer(
				ProjectDescription description) {
			return (typeDeclaration) -> {
				typeDeclaration.modifiers(Modifier.PUBLIC);
				JavaMethodDeclaration configure = JavaMethodDeclaration.method("addResourceHandlers")
						.modifiers(Modifier.PUBLIC)
						.returning("void")
						.parameters(new Parameter("org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry",
								"registry"))
						.body(new JavaHardCodeExpression());
				configure.annotate(Annotation.name("java.lang.Override"));
				configure.setFeatureName(JavaProjectConstants.SWAGGER_FEATURE);
				typeDeclaration.addMethodDeclaration(configure);
				typeDeclaration.annotate(Annotation.name("org.springframework.context.annotation.Configuration"));
				typeDeclaration.annotate(Annotation.name("springfox.documentation.swagger2.annotations.EnableSwagger2"));
				typeDeclaration.annotate(Annotation.name("java.lang.SuppressWarnings", (annotation) -> annotation
						.attribute("value",String.class,"rawtypes")));
				addField(typeDeclaration,"SWAGGER_UI","\"swagger-ui.html\"");
				addField(typeDeclaration,"WEB_JARS","\"/webjars/**\"");
				addField(typeDeclaration,"RESOURCE_LOCATION","\"classpath:/META-INF/resources/\"");
				addField(typeDeclaration,"WEB_JARS_LOCATION","\"classpath:/META-INF/resources/webjars/\"");
				addField(typeDeclaration,"AUTHORIZATION","\"Authorization\"");
				addField(typeDeclaration,"TOKEN_MSG","\"Enter Bearer token here\"");
				addField(typeDeclaration,"TYPE_STR","\"string\"");
				addField(typeDeclaration,"HEADER","\"header\"");
				addField(typeDeclaration,"CONTENT_TYPE","\"Content-Type\"");
				addField(typeDeclaration,"API_NAME","\"Event Management API\"");
				addField(typeDeclaration,"API_DESCRIPTION","\"Event Management API on Oracle Cloud\"");
				addField(typeDeclaration,"VERSION","\"1.0\"");
				addField(typeDeclaration,"EMPTY_STRING","\"\"");
				addField(typeDeclaration,"NET_INSIGHT","\"NetInsight\"");
				addField(typeDeclaration,"NET_INSIGHT_URL","\"https://netinsight.com/\"");
				addField(typeDeclaration,"NET_INSIGHT_EMAIL","\"info@netinsight.com\"");
				addField(typeDeclaration,"ERROR_PATH","\"/error.*\"");
				addField(typeDeclaration,"ACTUATOR_PATH","\"/actuator.*\"");
				addField(typeDeclaration,"BACK_SLASH","\"/\"");
			};
		}
		
		private void addField(JavaTypeDeclaration typeDeclaration,String fieldName, String value) {
			typeDeclaration.addFieldDeclaration(
					JavaFieldDeclaration.field(fieldName).modifiers(Modifier.PRIVATE).value(value).returning("java.lang.String"));
		}
		
		@Bean
		SwaggerCustomizer<JavaTypeDeclaration> apiCustomizer(
				ProjectDescription description) {
			return (typeDeclaration) -> {
				typeDeclaration.modifiers(Modifier.PUBLIC);
				JavaMethodDeclaration configure = JavaMethodDeclaration.method("api")
						.modifiers(Modifier.PUBLIC)
						.returning("springfox.documentation.spring.web.plugins.Docket")
						.body(new JavaHardCodeExpression());
				configure.annotate(Annotation.name("org.springframework.context.annotation.Bean"));
				configure.setFeatureName(JavaProjectConstants.SWAGGER_FEATURE);
				typeDeclaration.addMethodDeclaration(configure);
			};
		}
		
		@Bean
		SwaggerCustomizer<JavaTypeDeclaration> metadataCustomizer(
				ProjectDescription description) {
			return (typeDeclaration) -> {
				typeDeclaration.modifiers(Modifier.PUBLIC);
				JavaMethodDeclaration configure = JavaMethodDeclaration.method("metadata")
						.modifiers(Modifier.PUBLIC)
						.returning("springfox.documentation.service.ApiInfo")
						.body(new JavaHardCodeExpression());
				configure.annotate(Annotation.name("org.springframework.context.annotation.Bean"));
				configure.setFeatureName(JavaProjectConstants.SWAGGER_FEATURE);
				typeDeclaration.addMethodDeclaration(configure);
			};
		}
		
		@Bean
		 SwaggerCustomizer<JavaTypeDeclaration> loadimports() {
			return (t) ->{
				Set<String> imports = new HashSet<>();
				imports.add("springfox.documentation.service.Parameter");
				imports.add("springfox.documentation.builders.ParameterBuilder");
				imports.add("springfox.documentation.schema.ModelRef");
				imports.add("org.springframework.http.MediaType");
				imports.add("springfox.documentation.spring.web.plugins.Docket");
				imports.add("springfox.documentation.spi.DocumentationType");
				imports.add("springfox.documentation.builders.RequestHandlerSelectors");
				imports.add("springfox.documentation.builders.PathSelectors");
				imports.add("com.google.common.base.Predicates");
				imports.add("springfox.documentation.builders.PathSelectors");
				imports.add("java.util.Arrays");
				imports.add("java.util.ArrayList");
				imports.add("springfox.documentation.service.ApiInfo");
				imports.add("springfox.documentation.service.Contact");
				imports.add("springfox.documentation.service.VendorExtension");
				t.setImports(imports);
			};
		}
	}

	@Configuration
	@ConditionalOnRequestedDependency("redis-id")
	static class RedisConfiguration{
		
		@Bean
		RedisCustomizer<JavaTypeDeclaration> jedisConnectionFactoryCustomizer(
				ProjectDescription description) {
			return (typeDeclaration) -> {
				typeDeclaration.modifiers(Modifier.PUBLIC);
				JavaMethodDeclaration configure = JavaMethodDeclaration.method("jedisConnectionFactory")
						.modifiers(Modifier.PROTECTED)
						.returning("org.springframework.data.redis.connection.jedis.JedisConnectionFactory")
						.body(new JavaHardCodeExpression());
				configure.annotate(Annotation.name("org.springframework.context.annotation.Bean"));
				configure.setFeatureName(JavaProjectConstants.REDIS_FEATURE);
				typeDeclaration.addMethodDeclaration(configure);
				typeDeclaration.annotate(Annotation.name("org.springframework.context.annotation.Configuration"));
				addField(typeDeclaration,"REDIS_HOSTNAME","java.lang.String","${spring.redis.host}");
				addField(typeDeclaration,"REDIS_PASSWORD","java.lang.String","${spring.redis.password}");
				addField(typeDeclaration,"REDIS_PORT","java.lang.int","{spring.redis.port}");
				addField(typeDeclaration,"expirationTimeout","java.lang.Integer","{spring.cache.redis.time-to-live}");
				
				JavaStaticClassDeclaration staticRedisCacheErrorHandlerClassDeclaration = new JavaStaticClassDeclaration("RedisCacheErrorHandler");
				staticRedisCacheErrorHandlerClassDeclaration.implement("org.springframework.cache.interceptor.CacheErrorHandler");
				JavaMethodDeclaration handleCacheGetErrorConfig = JavaMethodDeclaration.method("handleCacheGetError")
				.modifiers(Modifier.PUBLIC)
				.returning("void")
				.parameters(new Parameter("java.lang.RuntimeException",
						"exception"),new Parameter(" org.springframework.cache.Cache",
								"cache"),new Parameter("java.lang.Object",
										"key"))
				.body(new JavaHardCodeExpression());
				handleCacheGetErrorConfig.setFeatureName(JavaProjectConstants.REDIS_FEATURE);
				staticRedisCacheErrorHandlerClassDeclaration.addMethodDeclaration(handleCacheGetErrorConfig);
				JavaMethodDeclaration handleCachePutErrorConfig = JavaMethodDeclaration.method("handleCachePutError")
						.modifiers(Modifier.PUBLIC)
						.returning("void")
						.parameters(new Parameter("java.lang.RuntimeException",
								"exception"),new Parameter(" org.springframework.cache.Cache",
										"cache"),new Parameter("java.lang.Object",
												"key"),new Parameter("java.lang.Object",
														"value"))
						.body(new JavaHardCodeExpression());
				handleCachePutErrorConfig.setFeatureName(JavaProjectConstants.REDIS_FEATURE);
				staticRedisCacheErrorHandlerClassDeclaration.addMethodDeclaration(handleCachePutErrorConfig);
				JavaMethodDeclaration handleCacheEvictErrorConfig = JavaMethodDeclaration.method("handleCacheEvictError")
						.modifiers(Modifier.PUBLIC)
						.returning("void")
						.parameters(new Parameter("java.lang.RuntimeException",
								"exception"),new Parameter(" org.springframework.cache.Cache",
										"cache"),new Parameter("java.lang.Object",
												"key"))
						.body(new JavaHardCodeExpression());
				handleCacheEvictErrorConfig.setFeatureName(JavaProjectConstants.REDIS_FEATURE);
				staticRedisCacheErrorHandlerClassDeclaration.addMethodDeclaration(handleCacheEvictErrorConfig);
				JavaMethodDeclaration handleCacheClearErrorConfig = JavaMethodDeclaration.method("handleCacheClearError")
						.modifiers(Modifier.PUBLIC)
						.returning("void")
						.parameters(new Parameter("java.lang.RuntimeException",
								"exception"))
						.body(new JavaHardCodeExpression());
				handleCacheClearErrorConfig.setFeatureName(JavaProjectConstants.REDIS_FEATURE);
				staticRedisCacheErrorHandlerClassDeclaration.addMethodDeclaration(handleCacheClearErrorConfig);
				List<JavaStaticClassDeclaration> staticClassList = new ArrayList<JavaStaticClassDeclaration>();
				staticClassList.add(staticRedisCacheErrorHandlerClassDeclaration);
				typeDeclaration.setStaticClassDeclaration(staticClassList);
			};
		}
		
		private void addField(JavaTypeDeclaration typeDeclaration, String fieldName, String returnType, String annotationValue) {
			JavaFieldDeclaration javaFieldDeclaration = JavaFieldDeclaration.field(fieldName)
					.modifiers(Modifier.PRIVATE).returning(returnType);
			javaFieldDeclaration.annotate(Annotation.name("org.springframework.beans.factory.annotation.Value",annotation -> {
				annotation.attribute("value", String.class, annotationValue);
			}));
			typeDeclaration.addFieldDeclaration(javaFieldDeclaration);
		}
		
		@Bean
		RedisCustomizer<JavaTypeDeclaration> redisTemplateCustomizer(
				ProjectDescription description) {
			return (typeDeclaration) -> {
				typeDeclaration.modifiers(Modifier.PUBLIC);
				JavaMethodDeclaration configure = JavaMethodDeclaration.method("redisTemplate")
						.modifiers(Modifier.PUBLIC)
						.returning("org.springframework.data.redis.core.RedisTemplate")
						.parameters(new Parameter("org.springframework.data.redis.connection.jedis.JedisConnectionFactory",
								"jedisConnectionFactory"))
						.body(new JavaHardCodeExpression());
				configure.annotate(Annotation.name("org.springframework.context.annotation.Bean"));
				configure.setFeatureName(JavaProjectConstants.REDIS_FEATURE);
				typeDeclaration.addMethodDeclaration(configure);
			};
		}
		
		@Bean
		RedisCustomizer<JavaTypeDeclaration> redisCacheManagerCustomizer(
				ProjectDescription description) {
			return (typeDeclaration) -> {
				typeDeclaration.modifiers(Modifier.PUBLIC);
				JavaMethodDeclaration configure = JavaMethodDeclaration.method("redisCacheManager")
						.modifiers(Modifier.PUBLIC)
						.returning("org.springframework.data.redis.cache.RedisCacheManager")
						.parameters(new Parameter("org.springframework.data.redis.connection.jedis.JedisConnectionFactory",
								"jedisConnectionFactory"))
						.body(new JavaHardCodeExpression());
				configure.annotate(Annotation.name("org.springframework.context.annotation.Bean"));
				configure.setFeatureName(JavaProjectConstants.REDIS_FEATURE);
				typeDeclaration.addMethodDeclaration(configure);
			};
		}
		
		@Bean
		RedisCustomizer<JavaTypeDeclaration> errorHandlerCustomizer(
				ProjectDescription description) {
			return (typeDeclaration) -> {
				typeDeclaration.modifiers(Modifier.PUBLIC);
				JavaMethodDeclaration configure = JavaMethodDeclaration.method("errorHandler")
						.modifiers(Modifier.PUBLIC)
						.returning("org.springframework.cache.interceptor.CacheErrorHandler")
						.body(new JavaHardCodeExpression());
				configure.annotate(Annotation.name("java.lang.Override"));
				configure.setFeatureName(JavaProjectConstants.REDIS_FEATURE);
				typeDeclaration.addMethodDeclaration(configure);
			};
		}
		
		@Bean
		 RedisCustomizer<JavaTypeDeclaration> loadimports() {
			return (t) ->{
				Set<String> imports = new HashSet<>();
				imports.add("java.time.Duration");
				imports.add("org.slf4j.Logger");
				imports.add("org.slf4j.LoggerFactory");
				imports.add("org.springframework.beans.factory.annotation.Autowired");
				imports.add("org.springframework.beans.factory.annotation.Value");
				imports.add("org.springframework.cache.Cache");
				imports.add("org.springframework.cache.annotation.CachingConfigurer");
				imports.add("org.springframework.cache.annotation.CachingConfigurerSupport");
				imports.add("org.springframework.cache.interceptor.CacheErrorHandler");
				imports.add("org.springframework.context.annotation.Bean");
				imports.add("org.springframework.context.annotation.Configuration");
				imports.add("org.springframework.data.redis.cache.RedisCacheConfiguration");
				imports.add("org.springframework.data.redis.cache.RedisCacheManager");
				imports.add("org.springframework.data.redis.connection.RedisStandaloneConfiguration");
				imports.add("org.springframework.data.redis.connection.jedis.JedisClientConfiguration");
				imports.add("org.springframework.data.redis.connection.jedis.JedisConnectionFactory");
				imports.add("org.springframework.data.redis.core.RedisTemplate");
				imports.add("org.springframework.data.redis.serializer.GenericToStringSerializer");
				t.setImports(imports);
			};
		}
	}
	
	@Configuration
	@ConditionalOnRequestedDependency("exception-id")
	static class ExceptionConfiguration{
		
		// MessageSourceUtil
		@Bean
		MessageSourceUtilCustomizer<JavaTypeDeclaration> setMessageSourceCustomizer(
				ProjectDescription description) {
			return (typeDeclaration) -> {
				typeDeclaration.modifiers(Modifier.PRIVATE);
				JavaMethodDeclaration configure = JavaMethodDeclaration.method("setMessageSource")
						.modifiers(Modifier.PUBLIC)
						.returning("void")
						.parameters(new Parameter("org.springframework.context.MessageSource",
								"messageSource"))
						.body(new JavaHardCodeExpression());
				configure.annotate(Annotation.name("java.lang.Override"));
				configure.setFeatureName(JavaProjectConstants.MESSAGE_SOURCE_UTIL);
				typeDeclaration.addMethodDeclaration(configure);
				typeDeclaration.annotate(Annotation.name("org.springframework.stereotype.Component"));
				addFieldWithoutValue(typeDeclaration,"messageSource","org.springframework.context.MessageSource");
				addPrivateField(typeDeclaration,"errorBaseKey","\"ERROR_CONFIG\"","java.lang.String");
				addPrivateField(typeDeclaration,"errorSeperator","\".\"","java.lang.String");
				addPrivateField(typeDeclaration,"errorMessage","\"message\"","java.lang.String");
				addPrivateField(typeDeclaration,"baseKey","errorBaseKey + errorSeperator","java.lang.String");
				
			};
		}
	
		private void addFieldWithoutValue(JavaTypeDeclaration typeDeclaration,String fieldName, String returnType) {
			typeDeclaration.addFieldDeclaration(
					JavaFieldDeclaration.field(fieldName).modifiers(Modifier.PRIVATE).returning(returnType));
		}
		
		private void addPrivateField(JavaTypeDeclaration typeDeclaration,String fieldName, String value, String returnType) {
			typeDeclaration.addFieldDeclaration(
					JavaFieldDeclaration.field(fieldName).modifiers(Modifier.PRIVATE).value(value).returning(returnType));
		}
		
		private void addPublicField(JavaTypeDeclaration typeDeclaration,String fieldName, String value, String returnType) {
			typeDeclaration.addFieldDeclaration(
					JavaFieldDeclaration.field(fieldName).modifiers(Modifier.PUBLIC).value(value).returning(returnType));
		}
		
		public void customizeGettersAndSetters(JavaTypeDeclaration typeDeclaration, String type, String name) {
			String caseChange = name.substring(0, 1).toUpperCase() + name.substring(1);
			String getterName = "get" + caseChange;
			JavaMethodDeclaration getter = JavaMethodDeclaration.method(getterName)
					.modifiers(Modifier.PUBLIC)
					.returning(type)
					.body(new JavaGetterCustomizer(name));
			typeDeclaration.addMethodDeclaration(getter);
			
			String setterName = "set" + caseChange;
			JavaMethodDeclaration setter = JavaMethodDeclaration.method(setterName)
					.modifiers(Modifier.PUBLIC)
					.returning("void")
					.parameters(new Parameter(type, name))
					.body(new JavaSetterCustomizer(name));
			typeDeclaration.addMethodDeclaration(setter);
		}
		
		@Bean
		MessageSourceUtilCustomizer<JavaTypeDeclaration> getLocalisedTextCustomizer(
				ProjectDescription description) {
			return (typeDeclaration) -> {
				typeDeclaration.modifiers(Modifier.PUBLIC);
				JavaMethodDeclaration configure = JavaMethodDeclaration.method("getLocalisedText")
						.modifiers(Modifier.PUBLIC)
						.returning("java.lang.String")
						.parameters(new Parameter("java.lang.String",
								"errorCode"), new Parameter("java.lang.String", "module"))
						.body(new JavaHardCodeExpression());
				configure.setFeatureName(JavaProjectConstants.MESSAGE_SOURCE_UTIL);
				typeDeclaration.addMethodDeclaration(configure);
			};
		}		
		
		@Bean
		MessageSourceUtilCustomizer<JavaTypeDeclaration> loadimports() {
			return (t) ->{
				Set<String> imports = new HashSet<>();
				imports.add("org.springframework.context.MessageSource");
				imports.add("org.springframework.context.MessageSourceAware");
				imports.add("org.springframework.context.i18n.LocaleContextHolder");
				imports.add("org.springframework.stereotype.Component");
				t.setImports(imports);
			};
		}
		
		// ExceptionConstants
		@Bean
		ExceptionConstantsCustomizer<JavaTypeDeclaration> exceptionConstantsCustomizer(
				ProjectDescription description) {
			return (typeDeclaration) -> {
				typeDeclaration.modifiers(Modifier.PUBLIC);
				addPublicField(typeDeclaration,"GENERAL_MODULE","\"GENERAL\"","java.lang.String");
				addPublicField(typeDeclaration,"API_MANAGEMENT","\"API_MANAGEMENT\"","java.lang.String");
				addPublicField(typeDeclaration,"GENERAL_ERROR_CODE","\"ER_001\"","java.lang.String");
				addPublicField(typeDeclaration,"INVALID_RESPONSE","\"ER_002\"","java.lang.String");
				addPublicField(typeDeclaration,"JSESSIONID_NOTFOUND","\"ER_003\"","java.lang.String");
				
			};
		}
		
		// GlobalExceptionHandler
		@Bean
		GlobalExceptionHandlerCustomizer<JavaTypeDeclaration> handleApplicationException(
				ProjectDescription description) {
			return (typeDeclaration) -> {
				typeDeclaration.modifiers(Modifier.PUBLIC);
				JavaMethodDeclaration configure = JavaMethodDeclaration.method("handleApplicationException")
						.modifiers(Modifier.PUBLIC)
						.returning("org.springframework.http.ResponseEntity<Object>")
						.parameters(new Parameter("org.springframework.web.context.request.WebRequest",
								"request"), new Parameter("java.lang.Exception", "ex"))
						.body(new JavaHardCodeExpression());
				configure.annotate(Annotation.name("org.springframework.web.bind.annotation.ExceptionHandler", (annotation) -> annotation
						.attribute("value",Exception.class,"ApplicationException.class")));
				configure.setFeatureName(JavaProjectConstants.GLOBAL_EXCEPTION_HANDLER);
				typeDeclaration.addMethodDeclaration(configure);
				typeDeclaration.annotate(Annotation.name("org.springframework.web.bind.annotation.RestControllerAdvice"));
				
				//addFieldWithoutValue(typeDeclaration,"messageSourceUtil","org.springframework.context.MessageSource");
//				addPrivateField(typeDeclaration,"errorBaseKey","\"ERROR_CONFIG\"","java.lang.String");
				
			};
		}

		
		@Bean
		GlobalExceptionHandlerCustomizer<JavaTypeDeclaration> handleBusinessException(
				ProjectDescription description) {
			return (typeDeclaration) -> {
				typeDeclaration.modifiers(Modifier.PUBLIC);
				JavaMethodDeclaration configure = JavaMethodDeclaration.method("handleBusinessException")
						.modifiers(Modifier.PUBLIC)
						.returning("org.springframework.http.ResponseEntity<Object>")
						.parameters(new Parameter("org.springframework.web.context.request.WebRequest",
								"request"), new Parameter("java.lang.Exception", "ex"))
						.body(new JavaHardCodeExpression());
				configure.annotate(Annotation.name("org.springframework.web.bind.annotation.ExceptionHandler", (annotation) -> annotation
						.attribute("value",Exception.class,"BusinessException.class")));
				configure.setFeatureName(JavaProjectConstants.GLOBAL_EXCEPTION_HANDLER);
				typeDeclaration.addMethodDeclaration(configure);
			};
		}	
		
		@Bean
		GlobalExceptionHandlerCustomizer<JavaTypeDeclaration> handleException(
				ProjectDescription description) {
			return (typeDeclaration) -> {
				typeDeclaration.modifiers(Modifier.PUBLIC);
				JavaMethodDeclaration configure = JavaMethodDeclaration.method("handleException")
						.modifiers(Modifier.PUBLIC)
						.returning("org.springframework.http.ResponseEntity<Object>")
						.parameters(new Parameter("org.springframework.web.context.request.WebRequest",
								"request"), new Parameter("java.lang.Exception", "ex"))
						.body(new JavaHardCodeExpression());
				configure.annotate(Annotation.name("org.springframework.web.bind.annotation.ExceptionHandler", (annotation) -> annotation
						.attribute("value",Exception.class,"RuntimeException.class")));
				configure.setFeatureName(JavaProjectConstants.GLOBAL_EXCEPTION_HANDLER);
				typeDeclaration.addMethodDeclaration(configure);
			};
		}
		
		@Bean
		GlobalExceptionHandlerCustomizer<JavaTypeDeclaration> getCustomExceptionResponse(
				ProjectDescription description) {
			return (typeDeclaration) -> {
				typeDeclaration.modifiers(Modifier.PUBLIC);
				JavaMethodDeclaration configure = JavaMethodDeclaration.method("getCustomExceptionResponse")
						.modifiers(Modifier.PRIVATE)
						.returning("org.springframework.http.ResponseEntity<Object>")
						.parameters(new Parameter("org.springframework.web.context.request.WebRequest",
								"request"), new Parameter("BaseException", "ex"))
						.body(new JavaHardCodeExpression());
				configure.setFeatureName(JavaProjectConstants.GLOBAL_EXCEPTION_HANDLER);
				typeDeclaration.addMethodDeclaration(configure);
			};
		}	
		
		@Bean
		GlobalExceptionHandlerCustomizer<JavaTypeDeclaration> loadimportsforGlobalExceptionHandler() {
			return (t) ->{
				Set<String> imports = new HashSet<>();
				imports.add("org.apache.commons.lang3.RandomUtils");
				imports.add("org.slf4j.Logger");
				imports.add("org.slf4j.LoggerFactory");
				imports.add("org.springframework.beans.factory.annotation.Autowired");
				imports.add("org.springframework.http.HttpStatus");
				imports.add("org.springframework.http.ResponseEntity");
				imports.add("org.springframework.web.bind.annotation.ExceptionHandler");
				imports.add("org.springframework.web.bind.annotation.RestControllerAdvice");
				imports.add("org.springframework.web.context.request.WebRequest");
				imports.add("org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler");
				t.setImports(imports);
			};
		}
		
		// ApiError
		@Bean
		ApiErrorCustomizer<JavaTypeDeclaration> apiErrorCustomizer(
				ProjectDescription description) {
			return (typeDeclaration) -> {
				typeDeclaration.modifiers(Modifier.PUBLIC);
				addFieldWithoutValue(typeDeclaration,"id", "java.lang.int");
				addFieldWithoutValue(typeDeclaration,"errorMessage", "java.lang.String");
				addFieldWithoutValue(typeDeclaration,"errorCode", "java.lang.String");
				addFieldWithoutValue(typeDeclaration,"errors", "java.util.List<String>");
				addFieldWithoutValue(typeDeclaration,"timeStamp", "java.lang.String");
				addFieldWithoutValue(typeDeclaration,"exceptionMessage", "java.lang.String");
				
			};
		}
		
		@Bean
		ApiErrorCustomizer<JavaTypeDeclaration> gettersAndSettersForApiError(
				ProjectDescription description) {
			return (typeDeclaration) -> {
				customizeGettersAndSetters(typeDeclaration, "java.lang.int", "id");
				customizeGettersAndSetters(typeDeclaration, "java.lang.String", "errorMessage");
				customizeGettersAndSetters(typeDeclaration, "java.lang.String", "errorCode");
				customizeGettersAndSetters(typeDeclaration, "java.util.List<String>", "errors");
				customizeGettersAndSetters(typeDeclaration, "java.lang.String", "timeStamp");
				customizeGettersAndSetters(typeDeclaration, "java.lang.String", "exceptionMessage");

			};
		}	
		

		@Bean
		ApiErrorCustomizer<JavaTypeDeclaration> loadimportsforApiError() {
			return (t) ->{
				Set<String> imports = new HashSet<>();
				imports.add("java.util.List");
				imports.add("com.fasterxml.jackson.annotation.JsonInclude");
				imports.add("com.fasterxml.jackson.annotation.JsonInclude.Include");
				imports.remove("java.util.List<String>");
				t.setImports(imports);
			};
		}
		
		// BaseException
		@Bean
		BaseExceptionCustomizer<JavaTypeDeclaration> baseExceptionCustomizer(
				ProjectDescription description) {
			return (typeDeclaration) -> {
				typeDeclaration.modifiers(Modifier.PUBLIC);
				addPrivateField(typeDeclaration,"id", "RandomUtils.nextInt(5000, 10000)", "java.lang.int");
				addFieldWithoutValue(typeDeclaration,"httpStatus", "org.springframework.http.HttpStatus");
				addFieldWithoutValue(typeDeclaration,"errorCode", "java.lang.String");
				addPrivateField(typeDeclaration,"errorModule","ExceptionConstants.GENERAL_MODULE", "java.lang.String");
				addFieldWithoutValue(typeDeclaration,"exceptionMessage", "java.lang.String");
				addPrivateField(typeDeclaration,"timeStamp", "new SimpleDateFormat(\"yyyy.MM.dd.HH.mm.ss\").format(new Timestamp(System.currentTimeMillis()))", "java.lang.String");
				
			};
		}
		
		@Bean
		BaseExceptionCustomizer<JavaTypeDeclaration> gettersAndSettersForBaseException(
				ProjectDescription description) {
			return (typeDeclaration) -> {
				customizeGettersAndSetters(typeDeclaration, "java.lang.int", "id");
				customizeGettersAndSetters(typeDeclaration, "org.springframework.http.HttpStatus", "httpStatus");
				customizeGettersAndSetters(typeDeclaration, "java.lang.String", "errorCode");
				customizeGettersAndSetters(typeDeclaration, "java.lang.String", "errorModule");
				customizeGettersAndSetters(typeDeclaration, "java.lang.String", "timeStamp");
				customizeGettersAndSetters(typeDeclaration, "java.lang.String", "exceptionMessage");

			};
		}	
		
		@Bean
		BaseExceptionCustomizer<JavaTypeDeclaration> loadimportsforBaseException() {
			return (t) ->{
				Set<String> imports = new HashSet<>();
				imports.add("java.sql.Timestamp");
				imports.add("java.text.SimpleDateFormat");
				imports.add("org.apache.commons.lang3.RandomUtils");
				t.setImports(imports);
			};
		}
		
		// BusinessException
		@Bean
		BusinessExceptionCustomizer<JavaTypeDeclaration> businessExceptionCustomizer(
				ProjectDescription description) {
			return (typeDeclaration) -> {
				typeDeclaration.modifiers(Modifier.PUBLIC);
				addFieldWithoutValue(typeDeclaration,"id", "java.lang.int");
				addPrivateField(typeDeclaration,"httpStatus", "HttpStatus.UNPROCESSABLE_ENTITY", "org.springframework.http.HttpStatus");
				addFieldWithoutValue(typeDeclaration,"errorCode", "java.lang.String");
				addFieldWithoutValue(typeDeclaration,"errorModule", "java.lang.String");
				addFieldWithoutValue(typeDeclaration,"exceptionMessage", "java.lang.String");
				addPrivateField(typeDeclaration,"timeStamp", "new SimpleDateFormat(\"yyyy.MM.dd.HH.mm.ss\").format(new Timestamp(System.currentTimeMillis()))", "java.lang.String");
				
			};
		}
		
		@Bean
		BusinessExceptionCustomizer<JavaTypeDeclaration> gettersAndSettersForBusinessException(
				ProjectDescription description) {
			return (typeDeclaration) -> {
				customizeGettersAndSetters(typeDeclaration, "java.lang.int", "id");
				customizeGettersAndSetters(typeDeclaration, "org.springframework.http.HttpStatus", "httpStatus");
				customizeGettersAndSetters(typeDeclaration, "java.lang.String", "errorCode");
				customizeGettersAndSetters(typeDeclaration, "java.lang.String", "errorModule");
				customizeGettersAndSetters(typeDeclaration, "java.lang.String", "timeStamp");
				customizeGettersAndSetters(typeDeclaration, "java.lang.String", "exceptionMessage");

			};
		}	
		
		@Bean
		BusinessExceptionCustomizer<JavaTypeDeclaration> loadimportsforBusinessException() {
			return (t) ->{
				Set<String> imports = new HashSet<>();
				imports.add("java.sql.Timestamp");
				imports.add("java.text.SimpleDateFormat");
				t.setImports(imports);
			};
		}
		
		// ApplicationException
		@Bean
		ApplicationExceptionCustomizer<JavaTypeDeclaration> applicationExceptionCustomizer(
				ProjectDescription description) {
			return (typeDeclaration) -> {
				typeDeclaration.modifiers(Modifier.PUBLIC);
				addFieldWithoutValue(typeDeclaration,"id", "java.lang.int");
				addPrivateField(typeDeclaration,"httpStatus", "HttpStatus.INTERNAL_SERVER_ERROR", "org.springframework.http.HttpStatus");
				addFieldWithoutValue(typeDeclaration,"errorCode", "java.lang.String");
				addPrivateField(typeDeclaration,"errorModule","ExceptionConstants.GENERAL_MODULE", "java.lang.String");
				addFieldWithoutValue(typeDeclaration,"exceptionMessage", "java.lang.String");
				addPrivateField(typeDeclaration,"timeStamp", "new SimpleDateFormat(\"yyyy.MM.dd.HH.mm.ss\").format(new Timestamp(System.currentTimeMillis()))", "java.lang.String");
				
			};
		}
		
		@Bean
		ApplicationExceptionCustomizer<JavaTypeDeclaration> gettersAndSettersForApplicationException(
				ProjectDescription description) {
			return (typeDeclaration) -> {
				customizeGettersAndSetters(typeDeclaration, "java.lang.int", "id");
				customizeGettersAndSetters(typeDeclaration, "org.springframework.http.HttpStatus", "httpStatus");
				customizeGettersAndSetters(typeDeclaration, "java.lang.String", "errorCode");
				customizeGettersAndSetters(typeDeclaration, "java.lang.String", "errorModule");
				customizeGettersAndSetters(typeDeclaration, "java.lang.String", "timeStamp");
				customizeGettersAndSetters(typeDeclaration, "java.lang.String", "exceptionMessage");

			};
		}	
		
		@Bean
		ApplicationExceptionCustomizer<JavaTypeDeclaration> loadimportsforApplicationException() {
			return (t) ->{
				Set<String> imports = new HashSet<>();
				imports.add("java.sql.Timestamp");
				imports.add("java.text.SimpleDateFormat");
				t.setImports(imports);
			};
		}
		
	}
	
}
