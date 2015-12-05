package br.eti.clairton.uniquevalidator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Unique(path = "protocol", type = Model.class)
public class Model {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String protocol;

	@Deprecated
	public Model() {
		this(null);
	}
	
	public Model(final String protocol) {
		super();
		this.protocol = protocol;
	}

	public String getProtocol() {
		return protocol;
	}
	
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
}
