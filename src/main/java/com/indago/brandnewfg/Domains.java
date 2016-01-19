package com.indago.brandnewfg;

import java.util.ArrayList;
import java.util.List;

// TODO: not currently thread-safe
public class Domains {

	private static final Domain binaryDom = new Domain( "Binary", 2 );

	private static final ArrayList< ArrayList< Domain > > binaryArgDoms = new ArrayList< >();

	public static Domain getDefaultBinaryDomain() {
		return binaryDom;
	}

	public static List< Domain > getDefaultBinaryArgumentDomains( final int arity ) {
		while ( arity > binaryArgDoms.size() - 1 ) {
			final ArrayList< Domain > doms = new ArrayList< >();
			for ( int i = 0; i < binaryArgDoms.size(); ++i )
				doms.add( binaryDom );
			binaryArgDoms.add( doms );
		}
		return binaryArgDoms.get( arity );
	}
}
