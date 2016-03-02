package br.eti.clairton.uniquevalidator;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Unique(path = {"protocol", "active"}, type = Model2.class, hints = {
		@Hint(key = "eclipselink.read-only", value = "true")
})
public class Model2 extends SuperModel{

	@Id
	@GeneratedValue(strategy = IDENTITY)
	private Long id;
	
	private Boolean active = TRUE;

	@Deprecated
	public Model2() {
		this(null);
	}
	
	public Model2(final String protocol) {
		super(protocol);
	}
	
	public Boolean getActive() {
		return active;
	}
	
	public void deactive() {
		active = FALSE;
	}
}
