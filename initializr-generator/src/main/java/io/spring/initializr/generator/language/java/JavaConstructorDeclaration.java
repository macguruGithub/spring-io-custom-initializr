package io.spring.initializr.generator.language.java;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.spring.initializr.generator.language.Parameter;

public class JavaConstructorDeclaration {

	private final String name;

	private final int modifiers;

	private final List<Parameter> parameters;
	
	private final List<JavaStatement> statements;
	
	private String featureName;
	
	public String getFeatureName() {
		return featureName;
	}

	public void setFeatureName(String featureName) {
		this.featureName = featureName;
	}
	
	private JavaConstructorDeclaration(String name, int modifiers, List<Parameter> parameters,
			List<JavaStatement> statements) {
		this.name = name;
		this.modifiers = modifiers;
		this.parameters = parameters;
		this.statements = statements;
	}

	public static Builder constructor(String name) {
		return new Builder(name);
	}

	String getName() {
		return this.name;
	}

	List<Parameter> getParameters() {
		return this.parameters;
	}

	int getModifiers() {
		return this.modifiers;
	}

	public List<JavaStatement> getStatements() {
		return this.statements;
	}
	
	/**
	 * Builder for creating a {@link JavaConstructorDeclaration}.
	 */
	public static final class Builder {

		private final String name;

		private List<Parameter> parameters = new ArrayList<>();

		private int modifiers;

		private Builder(String name) {
			this.name = name;
		}

		public Builder modifiers(int modifiers) {
			this.modifiers = modifiers;
			return this;
		}

		public Builder parameters(Parameter... parameters) {
			this.parameters = Arrays.asList(parameters);
			return this;
		}

		public JavaConstructorDeclaration body(JavaStatement... statements) {
			return new JavaConstructorDeclaration(this.name, this.modifiers, this.parameters,
					Arrays.asList(statements));
		}

	}
}
