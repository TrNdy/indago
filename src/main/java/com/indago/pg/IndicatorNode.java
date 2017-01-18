package com.indago.pg;

public class IndicatorNode {

	protected double cost;

	public IndicatorNode( final double cost ) {
		this.cost = cost;
	}

	public double getCost() {
		return cost;
	}

	public void setCost( final double cost ) {
		this.cost = cost;
	}
}