package io.spring.initializr.generator.spring.code.custom;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.ObjectProvider;

import io.spring.initializr.generator.language.CompilationUnit;
import io.spring.initializr.generator.language.SourceCode;
import io.spring.initializr.generator.language.TypeDeclaration;
import io.spring.initializr.generator.spring.code.CustomSourceCodeCustomizer;
import io.spring.initializr.generator.spring.util.LambdaSafe;

public class ApiErrorContributor implements
CustomSourceCodeCustomizer<TypeDeclaration, CompilationUnit<TypeDeclaration>, SourceCode<TypeDeclaration, CompilationUnit<TypeDeclaration>>> {

	private final String packageName;

	private final ObjectProvider<ApiErrorCustomizer<?>> apiErrorCustomizers;

	public ApiErrorContributor(String packageName, 
			ObjectProvider<ApiErrorCustomizer<?>> apiErrorCustomizers) {
				this.packageName = packageName;
				this.apiErrorCustomizers = apiErrorCustomizers;
	}

	@Override
	public void customize(SourceCode<TypeDeclaration, CompilationUnit<TypeDeclaration>> sourceCode) {
		CompilationUnit<TypeDeclaration> compilationUnit = sourceCode.createCompilationUnit(this.packageName,
				"ApiError");
		TypeDeclaration apiErrorConfig = compilationUnit.createTypeDeclaration("ApiError");
		customizeApiError(apiErrorConfig);
	}

	@SuppressWarnings("unchecked")
	private void customizeApiError(TypeDeclaration apiErrorConfig) {
		List<ApiErrorCustomizer<?>> customizers = this.apiErrorCustomizers.orderedStream()
				.collect(Collectors.toList());
		LambdaSafe.callbacks(ApiErrorCustomizer.class, customizers, apiErrorConfig)
			.invoke((customizer) -> customizer.customize(apiErrorConfig));
	}
}
