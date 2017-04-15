# unique-validator [![Build Status](https://travis-ci.org/clairton/unique-validator.svg?branch=master)](https://travis-ci.org/clairton/unique-validator)
Bean Validation for unique value in database;

```java
@Entity
@Unique(path = {"protocol"}, type = Model.class, qualifier = Default.class, hints = {
	//to not use cache
	@Hint(key = "eclipselink.read-only", value="true"),//eclipse-link
	@Hint(key = "org.hibernate.readOnly", value="true")//hibernate
})
public class Model {
	....
	private String protocol;
	...
}
```
For unit test:
```java
ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);
....
Unique annotation = ....;
UniqueValidator.mockCountToReturn(2l);
final ConstraintValidator<Unique, Object> validator = new UniqueValidator();
validator.init(annotation);
assertFalse(validator.isValid(someOject, context));
UniqueValidator.mockRollback();
```

Para usar será necessário adicionar as depêndencias:

```xml
<dependency>
    <groupId>br.eti.clairton</groupId>
	<artifactId>unique-validator</artifactId>
	<version>{latest}</version>
</dependency>
```
