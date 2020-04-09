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
import java.util.HashSet;
import java.util.Set;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.spring.initializr.generator.condition.ConditionalOnPackaging;
import io.spring.initializr.generator.condition.ConditionalOnPlatformVersion;
import io.spring.initializr.generator.condition.ConditionalOnRequestedDependency;
import io.spring.initializr.generator.language.Annotation;
import io.spring.initializr.generator.language.Parameter;
import io.spring.initializr.generator.language.java.JavaExpressionStatement;
import io.spring.initializr.generator.language.java.JavaFieldDeclaration;
import io.spring.initializr.generator.language.java.JavaHardCodeExpression;
import io.spring.initializr.generator.language.java.JavaMethodDeclaration;
import io.spring.initializr.generator.language.java.JavaMethodInvocation;
import io.spring.initializr.generator.language.java.JavaObjectCreation;
import io.spring.initializr.generator.language.java.JavaReturnStatement;
import io.spring.initializr.generator.language.java.JavaTypeDeclaration;
import io.spring.initializr.generator.packaging.war.WarPackaging;
import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.spring.code.CustomApplicationTypeCustomizer;
import io.spring.initializr.generator.spring.code.MainApplicationTypeCustomizer;
import io.spring.initializr.generator.spring.code.ServletInitializerCustomizer;
import io.spring.initializr.generator.spring.code.TestApplicationTypeCustomizer;
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
				addField(typeDeclaration,"REDIS_HOSTNAME","java.lang.String");
				addField(typeDeclaration,"REDIS_PASSWORD","java.lang.String");
				addField(typeDeclaration,"REDIS_PORT","java.lang.Integer");
				addField(typeDeclaration,"expirationTimeout","java.lang.Integer");
			};
		}
		
		private void addField(JavaTypeDeclaration typeDeclaration,String fieldName, String returnType) {
			typeDeclaration.addFieldDeclaration(
					JavaFieldDeclaration.field(fieldName).modifiers(Modifier.PRIVATE).returning(returnType));
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
	
}
