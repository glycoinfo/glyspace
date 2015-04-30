package org.glyspace.registry.view.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.ElementType;

import javax.validation.Constraint;
import javax.validation.Payload;

@Documented
@Constraint(validatedBy = GlycanValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Structure {
	String message() default "{org.glyspace.registry.validation.structure}";
	
	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}