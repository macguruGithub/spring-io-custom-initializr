package io.spring.initializr.generator.spring.code.custom;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.ObjectProvider;

import io.spring.initializr.generator.language.CompilationUnit;
import io.spring.initializr.generator.language.SourceCode;
import io.spring.initializr.generator.language.TypeDeclaration;
import io.spring.initializr.generator.spring.code.CustomSourceCodeCustomizer;
import io.spring.initializr.generator.spring.util.LambdaSafe;

public class ExceptionConstantsContributor implements
CustomSourceCodeCustomizer<TypeDeclaration, CompilationUnit<TypeDeclaration>, SourceCode<TypeDeclaration, CompilationUnit<TypeDeclaration>>> {

	private final String packageName;

	private final ObjectProvider<ExceptionConstantsCustomizer<?>> exceptionConstantsCustomizers;

	public ExceptionConstantsContributor(String packageName, 
			ObjectProvider<ExceptionConstantsCustomizer<?>> exceptionConstantsCustomizers) {
				this.packageName = packageName;
				this.exceptionConstantsCustomizers = exceptionConstantsCustomizers;
	}

	@Override
	public void customize(SourceCode<TypeDeclaration, CompilationUnit<TypeDeclaration>> sourceCode) {
		CompilationUnit<TypeDeclaration> compilationUnit = sourceCode.createCompilationUnit(this.packageName,
				"ExceptionConstants");
		TypeDeclaration exceptionConstantsConfig = compilationUnit.createTypeDeclaration("ExceptionConstants");
		customizeExceptionConstants(exceptionConstantsConfig);
	}

	@SuppressWarnings("unchecked")
	private void customizeExceptionConstants(TypeDeclaration exceptionConstantsConfig) {
		List<ExceptionConstantsCustomizer<?>> customizers = this.exceptionConstantsCustomizers.orderedStream()
				.collect(Collectors.toList());
		LambdaSafe.callbacks(ExceptionConstantsCustomizer.class, customizers, exceptionConstantsConfig)
			.invoke((customizer) -> customizer.customize(exceptionConstantsConfig));
	}
}
