package io.spring.initializr.generator.spring.code.custom;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.ObjectProvider;

import io.spring.initializr.generator.language.CompilationUnit;
import io.spring.initializr.generator.language.SourceCode;
import io.spring.initializr.generator.language.TypeDeclaration;
import io.spring.initializr.generator.spring.code.CustomSourceCodeCustomizer;
import io.spring.initializr.generator.spring.util.LambdaSafe;

public class GlobalExceptionHandlerContributor implements
CustomSourceCodeCustomizer<TypeDeclaration, CompilationUnit<TypeDeclaration>, SourceCode<TypeDeclaration, CompilationUnit<TypeDeclaration>>> {

	private final String packageName;
	
	private final String extendsName;

	private final ObjectProvider<GlobalExceptionHandlerCustomizer<?>> globalExceptionHandlerCustomizers;

	public GlobalExceptionHandlerContributor(String packageName, String extendsName,
			ObjectProvider<GlobalExceptionHandlerCustomizer<?>> globalExceptionHandlerCustomizers) {
				this.packageName = packageName;
				this.extendsName = extendsName;
				this.globalExceptionHandlerCustomizers = globalExceptionHandlerCustomizers;
	}

	@Override
	public void customize(SourceCode<TypeDeclaration, CompilationUnit<TypeDeclaration>> sourceCode) {
		CompilationUnit<TypeDeclaration> compilationUnit = sourceCode.createCompilationUnit(this.packageName,
				"GlobalExceptionHandler");
		TypeDeclaration globalExceptionHandlerConfig = compilationUnit.createTypeDeclaration("GlobalExceptionHandler");
		globalExceptionHandlerConfig.extend(this.extendsName);
		customizeGlobalExceptionHandler(globalExceptionHandlerConfig);
	}

	@SuppressWarnings("unchecked")
	private void customizeGlobalExceptionHandler(TypeDeclaration globalExceptionHandlerConfig) {
		List<GlobalExceptionHandlerCustomizer<?>> customizers = this.globalExceptionHandlerCustomizers.orderedStream()
				.collect(Collectors.toList());
		LambdaSafe.callbacks(GlobalExceptionHandlerCustomizer.class, customizers, globalExceptionHandlerConfig)
			.invoke((customizer) -> customizer.customize(globalExceptionHandlerConfig));
	}
}
