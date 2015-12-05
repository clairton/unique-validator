# unique-validator[![Build Status](https://drone.io/github.com/clairton/unique-validator/status.png)](https://drone.io/github.com/clairton/unique-validator/latest)

Bean Validation for unique value in database;

```java
@Entity
@Unique(path = "protocol", type = Model.class, qualifier = Default.class)
public class Model {
	....
	private String protocol;
	...
}
```

Para usar será necessário adicionar os repositórios maven:

```xml
<unique-validator>
	<id>mvn-repo-releases</id>
	<url>https://raw.github.com/clairton/mvn-repo/releases</url>
</unique-validator>
<unique-validator>
	<id>mvn-repo-snapshot</id>
	<url>https://raw.github.com/clairton/mvn-repo/snapshots</url>
</unique-validator>
```
 Também adicionar as depêndencias:
```xml
<dependency>
    <groupId>br.eti.clairton</groupId>
	<artifactId>unique-validator</artifactId>
	<version>0.1.0</version>
</dependency>
```
