package br.eti.clairton.uniquevalidator;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.enterprise.inject.Default;
import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * Anotação para verificar se o valor é unico no banco de dados.
 * 
 * @author Clairton Rodrigo Heinzen<clairton.rodrigo@gmail.com>
 */
@Target(FIELD)
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = { UniqueValidator.class })
public @interface Unique {

	String message() default "{br.eti.clairton.uniquevalidator.Unique.message}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
	
	Class<? extends Annotation> qualifier() default Default.class;

	Class<?> type();
	
	String path();
}
