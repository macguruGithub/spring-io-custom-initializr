package io.spring.initializr.generator.language.java;

public class JavaGetterCustomizer extends JavaStatement {

	private final String string;

	public JavaGetterCustomizer(String string) {
		this.string = string;
	}
	
	public String getString() {
		return this.string;
	}

}
