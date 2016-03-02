package br.eti.clairton.uniquevalidator;

import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintValidatorContext.ConstraintViolationBuilder;
import javax.validation.ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext;

import org.junit.Test;

public class UniqueValidatorTest {

	@Test
	public void test() {
		final ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);
		final ConstraintViolationBuilder builder = mock(ConstraintViolationBuilder.class);
		final NodeBuilderCustomizableContext node = mock(NodeBuilderCustomizableContext.class);
		when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
		when(builder.addPropertyNode(anyString())).thenReturn(node);
		final ConstraintValidator<Unique, Object> validator = new UniqueValidator(){
			{
				mockCountToReturn(2l);
			}
		};
		final Unique annotation = Model2.class.getAnnotation(Unique.class);
		validator.initialize(annotation);
		assertFalse(validator.isValid("somevalue", context));
	}

}
