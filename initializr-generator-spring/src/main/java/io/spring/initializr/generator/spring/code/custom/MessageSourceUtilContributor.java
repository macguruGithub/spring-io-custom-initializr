package io.spring.initializr.generator.spring.code.custom;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.ObjectProvider;

import io.spring.initializr.generator.language.CompilationUnit;
import io.spring.initializr.generator.language.SourceCode;
import io.spring.initializr.generator.language.TypeDeclaration;
import io.spring.initializr.generator.spring.code.CustomSourceCodeCustomizer;
import io.spring.initializr.generator.spring.util.LambdaSafe;

public class MessageSourceUtilContributor implements
CustomSourceCodeCustomizer<TypeDeclaration, CompilationUnit<TypeDeclaration>, SourceCode<TypeDeclaration, CompilationUnit<TypeDeclaration>>> {

	private final String packageName;

	private final String interfaceName;

	private final ObjectProvider<MessageSourceUtilCustomizer<?>> messageSourceUtilCustomizers;

	public MessageSourceUtilContributor(String packageName, String interfaceName,
			ObjectProvider<MessageSourceUtilCustomizer<?>> messageSourceUtilCustomizers) {
				this.packageName = packageName;
				this.interfaceName = interfaceName;
				this.messageSourceUtilCustomizers = messageSourceUtilCustomizers;
	}

	@Override
	public void customize(SourceCode<TypeDeclaration, CompilationUnit<TypeDeclaration>> sourceCode) {
		CompilationUnit<TypeDeclaration> compilationUnit = sourceCode.createCompilationUnit(this.packageName,
				"MessageSourceUtil");
		TypeDeclaration messageSourceUtilConfig = compilationUnit.createTypeDeclaration("MessageSourceUtil");
		messageSourceUtilConfig.implement(this.interfaceName);
		customizeMessageSourceUtil(messageSourceUtilConfig);
	}

	@SuppressWarnings("unchecked")
	private void customizeMessageSourceUtil(TypeDeclaration messageSourceUtilConfig) {
		List<MessageSourceUtilCustomizer<?>> customizers = this.messageSourceUtilCustomizers.orderedStream()
				.collect(Collectors.toList());
		LambdaSafe.callbacks(MessageSourceUtilCustomizer.class, customizers, messageSourceUtilConfig)
			.invoke((customizer) -> customizer.customize(messageSourceUtilConfig));
	}
}
