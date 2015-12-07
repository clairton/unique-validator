package br.eti.clairton.uniquevalidator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Unique(path = "protocol", type = Model.class)
public class Model extends SuperModel{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Deprecated
	public Model() {
		this(null);
	}
	
	public Model(final String protocol) {
		super(protocol);
	}
}
