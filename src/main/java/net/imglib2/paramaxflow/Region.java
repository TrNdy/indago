package net.imglib2.paramaxflow;

public class Region
{
	final long ptr_this;

	Region( final long ptr_this )
	{
		this.ptr_this = ptr_this;
	}

	@Override
	public boolean equals( final Object obj )
	{
		return Region.class.isInstance( obj ) && ( ( Region ) obj ).ptr_this == ptr_this;
	}

	@Override
	public int hashCode()
	{
		return ( int ) ptr_this;
	}
}
