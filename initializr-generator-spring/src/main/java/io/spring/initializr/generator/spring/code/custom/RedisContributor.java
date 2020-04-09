package io.spring.initializr.generator.spring.code.custom;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.ObjectProvider;

import io.spring.initializr.generator.language.CompilationUnit;
import io.spring.initializr.generator.language.SourceCode;
import io.spring.initializr.generator.language.TypeDeclaration;
import io.spring.initializr.generator.spring.code.CustomSourceCodeCustomizer;
import io.spring.initializr.generator.spring.util.LambdaSafe;

	/**
	 * {@link CustomSourceCodeCustomizer} that contributes the servlet initializer to
	 * applications using war packaging.
	 *
	 * @author Andy Wilkinson
	 */
	public class RedisContributor implements
			CustomSourceCodeCustomizer<TypeDeclaration, CompilationUnit<TypeDeclaration>, SourceCode<TypeDeclaration, CompilationUnit<TypeDeclaration>>> {

		private final String packageName;

		private final String interfaceName;
		
		private final String extendsName;

		private final ObjectProvider<RedisCustomizer<?>> redisCustomizers;

		public RedisContributor(String packageName, String interfaceName, String extendsName,
				ObjectProvider<RedisCustomizer<?>> redisCustomizers) {
			this.packageName = packageName;
			this.interfaceName = interfaceName;
			this.extendsName = extendsName;
			this.redisCustomizers = redisCustomizers;
		}

		@Override
		public void customize(SourceCode<TypeDeclaration, CompilationUnit<TypeDeclaration>> sourceCode) {
			CompilationUnit<TypeDeclaration> compilationUnit = sourceCode.createCompilationUnit(this.packageName,
					"RedisConfig");
			TypeDeclaration redisConfig = compilationUnit.createTypeDeclaration("RedisConfig");
			redisConfig.implement(this.interfaceName);
			redisConfig.extend(this.extendsName);
			customizeRedis(redisConfig);
		}

		@SuppressWarnings("unchecked")
		private void customizeRedis(TypeDeclaration redisConfig) {
			List<RedisCustomizer<?>> customizers = this.redisCustomizers.orderedStream()
					.collect(Collectors.toList());
			LambdaSafe.callbacks(RedisCustomizer.class, customizers, redisConfig)
					.invoke((customizer) -> customizer.customize(redisConfig));
		}

	}