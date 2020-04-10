package io.spring.initializr.generator.language.java;

public class JavaSetterCustomizer extends JavaStatement {

	private final String string;

	public JavaSetterCustomizer(String string) {
		this.string = string;
	}
	
	public String getString() {
		return this.string;
	}

}
