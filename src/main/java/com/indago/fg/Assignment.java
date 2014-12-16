package com.indago.fg;

import java.util.HashMap;
import java.util.List;

import com.indago.fg.domain.Domain;
import com.indago.fg.value.Value;
import com.indago.fg.variable.Variable;


public class Assignment {
	private final HashMap< Variable< ? >, Value< ?, ? > > variableToValue;

	public Assignment( final List< ? extends Variable< ?> > variables )
	{
		variableToValue = new HashMap<>();
	}

	public boolean isAssigned( final Variable< ? > variable )
	{
		return variableToValue.containsKey( variable );
	}

	@SuppressWarnings( "unchecked" )
	public < T, D extends Domain< T > > Value< T, D > getAssignment( final Variable< D > variable )
	{
		return ( Value< T, D > ) variableToValue.get( variable );
	}

	public < T, D extends Domain< T > > void assign( final Variable< D > variable, final Value< T, D > value )
	{
		if ( value == null )
			variableToValue.remove( variable );
		else
			variableToValue.put( variable, value );
	}
}
