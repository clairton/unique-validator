package br.eti.clairton.uniquevalidator;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class SuperModel {

	private String protocol;

	@Deprecated
	public SuperModel() {
		this(null);
	}

	public SuperModel(final String protocol) {
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
