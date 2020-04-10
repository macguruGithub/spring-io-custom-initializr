package io.spring.initializr.generator.spring.code.custom;

import org.springframework.core.Ordered;

import io.spring.initializr.generator.language.TypeDeclaration;

	/**
	 * Callback for configuring the generated project's servlet initializer. Invoked with an
	 * {@link Ordered order} of {@code 0} by default, considering overriding
	 * {@link #getOrder()} to customize this behaviour.
	 *
	 * @param <T> type declaration that this customizer can handle
	 * @author Andy Wilkinson
	 */
	@FunctionalInterface
	public interface BusinessExceptionCustomizer<T extends TypeDeclaration> extends Ordered {

		void customize(T typeDeclaration);

		@Override
		default int getOrder() {
			return 0;
		}

}