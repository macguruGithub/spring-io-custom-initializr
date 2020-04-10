package io.spring.initializr.generator.spring.code.custom;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.ObjectProvider;

import io.spring.initializr.generator.language.CompilationUnit;
import io.spring.initializr.generator.language.SourceCode;
import io.spring.initializr.generator.language.TypeDeclaration;
import io.spring.initializr.generator.spring.code.CustomSourceCodeCustomizer;
import io.spring.initializr.generator.spring.util.LambdaSafe;

public class BusinessExceptionContributor implements
CustomSourceCodeCustomizer<TypeDeclaration, CompilationUnit<TypeDeclaration>, SourceCode<TypeDeclaration, CompilationUnit<TypeDeclaration>>> {

	private final String packageName;

	private final String extendsName;

	private final ObjectProvider<BusinessExceptionCustomizer<?>> businessExceptionCustomizers;

	public BusinessExceptionContributor(String packageName, String extendsName,
			ObjectProvider<BusinessExceptionCustomizer<?>> businessExceptionCustomizers) {
				this.packageName = packageName;
				this.extendsName = extendsName;
				this.businessExceptionCustomizers = businessExceptionCustomizers;
	}

	@Override
	public void customize(SourceCode<TypeDeclaration, CompilationUnit<TypeDeclaration>> sourceCode) {
		CompilationUnit<TypeDeclaration> compilationUnit = sourceCode.createCompilationUnit(this.packageName,
				"BusinessException");
		TypeDeclaration businessExceptionConfig = compilationUnit.createTypeDeclaration("BusinessException");
		businessExceptionConfig.extend(this.extendsName);
		customizeBusinessException(businessExceptionConfig);
	}

	@SuppressWarnings("unchecked")
	private void customizeBusinessException(TypeDeclaration businessExceptionConfig) {
		List<BusinessExceptionCustomizer<?>> customizers = this.businessExceptionCustomizers.orderedStream()
				.collect(Collectors.toList());
		LambdaSafe.callbacks(BusinessExceptionCustomizer.class, customizers, businessExceptionConfig)
			.invoke((customizer) -> customizer.customize(businessExceptionConfig));
	}
}
