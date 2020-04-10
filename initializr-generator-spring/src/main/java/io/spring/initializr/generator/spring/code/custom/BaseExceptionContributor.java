package io.spring.initializr.generator.spring.code.custom;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.ObjectProvider;

import io.spring.initializr.generator.language.CompilationUnit;
import io.spring.initializr.generator.language.SourceCode;
import io.spring.initializr.generator.language.TypeDeclaration;
import io.spring.initializr.generator.spring.code.CustomSourceCodeCustomizer;
import io.spring.initializr.generator.spring.util.LambdaSafe;

public class BaseExceptionContributor implements
CustomSourceCodeCustomizer<TypeDeclaration, CompilationUnit<TypeDeclaration>, SourceCode<TypeDeclaration, CompilationUnit<TypeDeclaration>>> {

	private final String packageName;

	private final String extendsName;

	private final ObjectProvider<BaseExceptionCustomizer<?>> baseExceptionCustomizers;

	public BaseExceptionContributor(String packageName, String extendsName,
			ObjectProvider<BaseExceptionCustomizer<?>> baseExceptionCustomizers) {
				this.packageName = packageName;
				this.extendsName = extendsName;
				this.baseExceptionCustomizers = baseExceptionCustomizers;
	}

	@Override
	public void customize(SourceCode<TypeDeclaration, CompilationUnit<TypeDeclaration>> sourceCode) {
		CompilationUnit<TypeDeclaration> compilationUnit = sourceCode.createCompilationUnit(this.packageName,
				"BaseException");
		TypeDeclaration baseExceptionConfig = compilationUnit.createTypeDeclaration("BaseException");
		baseExceptionConfig.extend(this.extendsName);
		customizeBaseException(baseExceptionConfig);
	}

	@SuppressWarnings("unchecked")
	private void customizeBaseException(TypeDeclaration baseExceptionConfig) {
		List<BaseExceptionCustomizer<?>> customizers = this.baseExceptionCustomizers.orderedStream()
				.collect(Collectors.toList());
		LambdaSafe.callbacks(BaseExceptionCustomizer.class, customizers, baseExceptionConfig)
			.invoke((customizer) -> customizer.customize(baseExceptionConfig));
	}
}
