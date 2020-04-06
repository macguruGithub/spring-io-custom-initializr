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

package io.spring.initializr.generator.spring.code.custom;

import java.util.List;
import java.util.stream.Collectors;

import io.spring.initializr.generator.language.CompilationUnit;
import io.spring.initializr.generator.language.SourceCode;
import io.spring.initializr.generator.language.TypeDeclaration;
import io.spring.initializr.generator.spring.code.CustomSourceCodeCustomizer;
import io.spring.initializr.generator.spring.util.LambdaSafe;

import org.springframework.beans.factory.ObjectProvider;

/**
 * {@link CustomSourceCodeCustomizer} that contributes the servlet initializer to
 * applications using war packaging.
 *
 * @author Andy Wilkinson
 */
public class SwaggerContributor implements
		CustomSourceCodeCustomizer<TypeDeclaration, CompilationUnit<TypeDeclaration>, SourceCode<TypeDeclaration, CompilationUnit<TypeDeclaration>>> {

	private final String packageName;

	private final String interfaceName;

	private final ObjectProvider<SwaggerCustomizer<?>> swaggerCustomizers;

	public SwaggerContributor(String packageName, String interfaceName,
			ObjectProvider<SwaggerCustomizer<?>> swaggerCustomizers) {
		this.packageName = packageName;
		this.interfaceName = interfaceName;
		this.swaggerCustomizers = swaggerCustomizers;
	}

	@Override
	public void customize(SourceCode<TypeDeclaration, CompilationUnit<TypeDeclaration>> sourceCode) {
		CompilationUnit<TypeDeclaration> compilationUnit = sourceCode.createCompilationUnit(this.packageName,
				"SwaggerConfig");
		TypeDeclaration swaggerConfig = compilationUnit.createTypeDeclaration("SwaggerConfig");
		swaggerConfig.implement(this.interfaceName);
		customizeSwagger(swaggerConfig);
	}

	@SuppressWarnings("unchecked")
	private void customizeSwagger(TypeDeclaration swaggerConfig) {
		List<SwaggerCustomizer<?>> customizers = this.swaggerCustomizers.orderedStream()
				.collect(Collectors.toList());
		LambdaSafe.callbacks(SwaggerCustomizer.class, customizers, swaggerConfig)
				.invoke((customizer) -> customizer.customize(swaggerConfig));
	}

}
