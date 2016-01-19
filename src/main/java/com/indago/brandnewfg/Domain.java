package com.indago.brandnewfg;

public class Domain {

	private final String name;

	private final int cardinality;

	private final int hashcode;

	public Domain( final String name, final int cardinality ) {
		this.name = name;
		this.cardinality = cardinality;
		hashcode = name.hashCode() ^ cardinality;
	}

	public int getCardinality() {
		return cardinality;
	}

	@Override
	public int hashCode() {
		return hashcode;
	}

	@Override
	public boolean equals( final Object obj ) {
		if ( obj == this ) {
			return true;
		} else if ( obj != null && obj instanceof Domain ) {
			final Domain d = ( Domain ) obj;
			return d.cardinality == cardinality && d.name.equals( name );
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		return name + "(" + cardinality + ")";
	}
}
