package io.spring.initializr.generator.spring.code.custom;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.ObjectProvider;

import io.spring.initializr.generator.language.CompilationUnit;
import io.spring.initializr.generator.language.SourceCode;
import io.spring.initializr.generator.language.TypeDeclaration;
import io.spring.initializr.generator.spring.code.CustomSourceCodeCustomizer;
import io.spring.initializr.generator.spring.util.LambdaSafe;

public class ApplicationExceptionContributor implements
CustomSourceCodeCustomizer<TypeDeclaration, CompilationUnit<TypeDeclaration>, SourceCode<TypeDeclaration, CompilationUnit<TypeDeclaration>>> {

	private final String packageName;

	private final String extendsName;

	private final ObjectProvider<ApplicationExceptionCustomizer<?>> applicationExceptionCustomizers;

	public ApplicationExceptionContributor(String packageName, String extendsName,
			ObjectProvider<ApplicationExceptionCustomizer<?>> applicationExceptionCustomizers) {
				this.packageName = packageName;
				this.extendsName = extendsName;
				this.applicationExceptionCustomizers = applicationExceptionCustomizers;
	}

	@Override
	public void customize(SourceCode<TypeDeclaration, CompilationUnit<TypeDeclaration>> sourceCode) {
		CompilationUnit<TypeDeclaration> compilationUnit = sourceCode.createCompilationUnit(this.packageName,
				"ApplicationException");
		TypeDeclaration applicationExceptionConfig = compilationUnit.createTypeDeclaration("ApplicationException");
		applicationExceptionConfig.extend(this.extendsName);
		customizeApplicationException(applicationExceptionConfig);
	}

	@SuppressWarnings("unchecked")
	private void customizeApplicationException(TypeDeclaration applicationExceptionConfig) {
		List<ApplicationExceptionCustomizer<?>> customizers = this.applicationExceptionCustomizers.orderedStream()
				.collect(Collectors.toList());
		LambdaSafe.callbacks(ApplicationExceptionCustomizer.class, customizers, applicationExceptionConfig)
			.invoke((customizer) -> customizer.customize(applicationExceptionConfig));
	}
}
