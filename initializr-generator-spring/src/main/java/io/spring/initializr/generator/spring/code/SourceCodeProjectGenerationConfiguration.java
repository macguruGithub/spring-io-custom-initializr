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

package io.spring.initializr.generator.spring.code;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.spring.initializr.generator.condition.ConditionalOnPackaging;
import io.spring.initializr.generator.condition.ConditionalOnPlatformVersion;
import io.spring.initializr.generator.condition.ConditionalOnRequestedDependency;
import io.spring.initializr.generator.language.Annotation;
import io.spring.initializr.generator.language.TypeDeclaration;
import io.spring.initializr.generator.packaging.war.WarPackaging;
import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.project.ProjectGenerationConfiguration;
import io.spring.initializr.generator.spring.code.custom.ApiErrorContributor;
import io.spring.initializr.generator.spring.code.custom.ApiErrorCustomizer;
import io.spring.initializr.generator.spring.code.custom.ApplicationExceptionContributor;
import io.spring.initializr.generator.spring.code.custom.ApplicationExceptionCustomizer;
import io.spring.initializr.generator.spring.code.custom.BaseExceptionContributor;
import io.spring.initializr.generator.spring.code.custom.BaseExceptionCustomizer;
import io.spring.initializr.generator.spring.code.custom.BusinessExceptionContributor;
import io.spring.initializr.generator.spring.code.custom.BusinessExceptionCustomizer;
import io.spring.initializr.generator.spring.code.custom.ExceptionConstantsContributor;
import io.spring.initializr.generator.spring.code.custom.ExceptionConstantsCustomizer;
import io.spring.initializr.generator.spring.code.custom.GlobalExceptionHandlerContributor;
import io.spring.initializr.generator.spring.code.custom.GlobalExceptionHandlerCustomizer;
import io.spring.initializr.generator.spring.code.custom.MessageSourceUtilContributor;
import io.spring.initializr.generator.spring.code.custom.MessageSourceUtilCustomizer;
import io.spring.initializr.generator.spring.code.custom.RedisContributor;
import io.spring.initializr.generator.spring.code.custom.RedisCustomizer;
import io.spring.initializr.generator.spring.code.custom.SwaggerContributor;
import io.spring.initializr.generator.spring.code.custom.SwaggerCustomizer;

/**
 * Project generation configuration for projects written in any language.
 *
 * @author Andy Wilkinson
 */
@ProjectGenerationConfiguration
public class SourceCodeProjectGenerationConfiguration {

	@Bean
	public MainApplicationTypeCustomizer<TypeDeclaration> springBootApplicationAnnotator() {
		return (typeDeclaration) -> typeDeclaration
				.annotate(Annotation.name("org.springframework.boot.autoconfigure.SpringBootApplication"));
	}

	@Bean
	@ConditionalOnPlatformVersion("[1.5.0.RELEASE,2.2.0.M3)")
	public TestApplicationTypeCustomizer<TypeDeclaration> junit4SpringBootTestTypeCustomizer() {
		return (typeDeclaration) -> {
			typeDeclaration.annotate(Annotation.name("org.junit.runner.RunWith", (annotation) -> annotation
					.attribute("value", Class.class, "org.springframework.test.context.junit4.SpringRunner")));
			typeDeclaration.annotate(Annotation.name("org.springframework.boot.test.context.SpringBootTest"));
		};
	}

	@Bean
	@ConditionalOnPlatformVersion("2.2.0.M3")
	public TestApplicationTypeCustomizer<TypeDeclaration> junitJupiterSpringBootTestTypeCustomizer() {
		return (typeDeclaration) -> typeDeclaration
				.annotate(Annotation.name("org.springframework.boot.test.context.SpringBootTest"));
	}

	/**
	 * Language-agnostic source code contributions for projects using war packaging.
	 */
	@Configuration
	@ConditionalOnPackaging(WarPackaging.ID)
	static class WarPackagingConfiguration {

		private final ProjectDescription description;

		WarPackagingConfiguration(ProjectDescription description) {
			this.description = description;
		}

		@Bean
		@ConditionalOnPlatformVersion("[1.5.0.M1, 2.0.0.M1)")
		ServletInitializerContributor boot15ServletInitializerContributor(
				ObjectProvider<ServletInitializerCustomizer<?>> servletInitializerCustomizers) {
			return new ServletInitializerContributor(this.description.getPackageName(),
					"org.springframework.boot.web.support.SpringBootServletInitializer", servletInitializerCustomizers);
		}

		@Bean
		@ConditionalOnPlatformVersion("2.0.0.M1")
		ServletInitializerContributor boot20ServletInitializerContributor(
				ObjectProvider<ServletInitializerCustomizer<?>> servletInitializerCustomizers) {
			return new ServletInitializerContributor(this.description.getPackageName(),
					"org.springframework.boot.web.servlet.support.SpringBootServletInitializer",
					servletInitializerCustomizers);
		}

	}
	
	@Configuration
	@ConditionalOnRequestedDependency("swagger-id")
	static class SwaggerConfiguration {

		private final ProjectDescription description;

		SwaggerConfiguration(ProjectDescription description) {
			this.description = description;
		}

		@Bean
		SwaggerContributor swaggerContributor(
				ObjectProvider<SwaggerCustomizer<?>> swaggerCustomizers) {
			return new SwaggerContributor(this.description.getPackageName() + ".swagger",
					"org.springframework.web.servlet.config.annotation.WebMvcConfigurer", swaggerCustomizers);
		}

	}
	
	@Configuration
	@ConditionalOnRequestedDependency("redis-id")
	static class RedisConfiguration {

		private final ProjectDescription description;

		RedisConfiguration(ProjectDescription description) {
			this.description = description;
		}

		@Bean
		RedisContributor redisContributor(
				ObjectProvider<RedisCustomizer<?>> redisCustomizers) {
			return new RedisContributor(this.description.getPackageName() + ".redis",
					"org.springframework.cache.annotation.CachingConfigurer", "org.springframework.cache.annotation.CachingConfigurerSupport", redisCustomizers);
		}

	}	
	
	@Configuration
	@ConditionalOnRequestedDependency("exception-id")
	static class ExceptionConfiguration {

		private final ProjectDescription description;

		ExceptionConfiguration(ProjectDescription description) {
			this.description = description;
		}

		@Bean
		MessageSourceUtilContributor messageSourceUtilContributor(
				ObjectProvider<MessageSourceUtilCustomizer<?>> messageSourceUtilCustomizers) {
			return new MessageSourceUtilContributor(this.description.getPackageName() + ".exception",
					"org.springframework.context.MessageSourceAware", messageSourceUtilCustomizers);
		}

		// ExceptionConstants
		@Bean
		ExceptionConstantsContributor exceptionConstantsContributor(
				ObjectProvider<ExceptionConstantsCustomizer<?>> exceptionConstantsCustomizers) {
			return new ExceptionConstantsContributor(this.description.getPackageName() + ".exception", exceptionConstantsCustomizers);
		}
		
		// GlobalExceptionHandler
		@Bean
		GlobalExceptionHandlerContributor globalExceptionHandlerContributor(
				ObjectProvider<GlobalExceptionHandlerCustomizer<?>> globalExceptionHandlerCustomizers) {
			return new GlobalExceptionHandlerContributor(this.description.getPackageName() + ".exception",
					"org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler", globalExceptionHandlerCustomizers);
		}

		// ApiError
		@Bean
		ApiErrorContributor apiErrorContributor(
				ObjectProvider<ApiErrorCustomizer<?>> apiErrorCustomizers) {
			return new ApiErrorContributor(this.description.getPackageName() + ".exception", apiErrorCustomizers);
		}
		
		// BaseException
		@Bean
		BaseExceptionContributor baseExceptionContributor(
				ObjectProvider<BaseExceptionCustomizer<?>> baseExceptionCustomizers) {
			return new BaseExceptionContributor(this.description.getPackageName() + ".exception",
					"java.lang.RuntimeException", baseExceptionCustomizers);
		}
		
		// BusinessException
		@Bean
		BusinessExceptionContributor businessExceptionContributor(
				ObjectProvider<BusinessExceptionCustomizer<?>> businessExceptionCustomizers) {
			return new BusinessExceptionContributor(this.description.getPackageName() + ".exception",
					"BaseException", businessExceptionCustomizers);
		}
		
		// ApplicationException
		@Bean
		ApplicationExceptionContributor applicationExceptionContributor(
				ObjectProvider<ApplicationExceptionCustomizer<?>> applicationExceptionCustomizers) {
			return new ApplicationExceptionContributor(this.description.getPackageName() + ".exception",
					"BaseException", applicationExceptionCustomizers);
		}
		
	}

}
