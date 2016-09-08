package com.indago.ilp;

import org.slf4j.Logger;

import gurobi.GRB;
import gurobi.GRBCallback;
import gurobi.GRBException;

public class DefaultLoggingGurobiCallback extends GRBCallback {

	private double lastiter;
	private double lastnode;

	private double latestGap;

	private final String msgPrefix = "[GRB] ";
	private final Logger log;
	private final double maxTime;
	private final double maxOptimalityGap;
	private final double maxNodesToExplore;

	public DefaultLoggingGurobiCallback( final Logger log ) {
		this.log = log;

		lastiter = -GRB.INFINITY;
		lastnode = -GRB.INFINITY;
		latestGap = Double.POSITIVE_INFINITY;
		maxTime = Double.MAX_VALUE;
		maxOptimalityGap = Double.MAX_VALUE;
		maxNodesToExplore = 10000;
	}

	@Override
	protected void callback() {
		try {
			if ( where == GRB.CB_POLLING ) {
				// Ignore polling callback
			} else if ( where == GRB.CB_PRESOLVE ) {
				// Presolve callback
				final int cdels = getIntInfo( GRB.CB_PRE_COLDEL );
				final int rdels = getIntInfo( GRB.CB_PRE_ROWDEL );
				if ( cdels != 0 || rdels != 0 ) {
					log_info( cdels + " columns and " + rdels + " rows are removed" );
				}

			} else if ( where == GRB.CB_SIMPLEX ) {
				// Simplex callback
				final double itcnt = getDoubleInfo( GRB.CB_SPX_ITRCNT );
				if ( itcnt - lastiter >= 100 ) {
					lastiter = itcnt;
					final double obj = getDoubleInfo( GRB.CB_SPX_OBJVAL );
					final int ispert = getIntInfo( GRB.CB_SPX_ISPERT );
					final double pinf = getDoubleInfo( GRB.CB_SPX_PRIMINF );
					final double dinf = getDoubleInfo( GRB.CB_SPX_DUALINF );
					char ch;
					if ( ispert == 0 )
						ch = ' ';
					else if ( ispert == 1 )
						ch = 'S';
					else
						ch = 'P';
					log_info( itcnt + " " + obj + ch + " " + pinf + " " + dinf );
				}

			} else if ( where == GRB.CB_MIP ) {
				// General MIP callback
				final double nodecnt = getDoubleInfo( GRB.CB_MIP_NODCNT );
				final double objbst = getDoubleInfo( GRB.CB_MIP_OBJBST );
				final double objbnd = getDoubleInfo( GRB.CB_MIP_OBJBND );
				final int solcnt = getIntInfo( GRB.CB_MIP_SOLCNT );
				final double runtime = getDoubleInfo( GRB.CB_RUNTIME );

				this.latestGap = Math.abs( objbst - objbnd ) / ( 1.0 + Math.abs( objbst ) );

				if ( nodecnt - lastnode >= 100 ) {
					lastnode = nodecnt;
					final int actnodes = ( int ) getDoubleInfo( GRB.CB_MIP_NODLFT );
					final int itcnt = ( int ) getDoubleInfo( GRB.CB_MIP_ITRCNT );
					final int cutcnt = getIntInfo( GRB.CB_MIP_CUTCNT );
					log_info( nodecnt + " " + actnodes + " " + itcnt + " " + objbst + " " + objbnd + " " + solcnt + " " + cutcnt );
				}
				if ( runtime > maxTime ) {
					if ( latestGap < maxOptimalityGap ) {
						log_warn( "Stop early - solve time > " + maxTime + " && gap < " + maxOptimalityGap );
						abort();
					}
				}
				if ( nodecnt >= maxNodesToExplore && solcnt > 0 ) {
					log_warn( "Stop early - " + maxNodesToExplore + " nodes explored" );
					abort();
				}

			} else if ( where == GRB.CB_MIPSOL ) {
				// MIP solution callback
				final int nodecnt = ( int ) getDoubleInfo( GRB.CB_MIPSOL_NODCNT );
				final double obj = getDoubleInfo( GRB.CB_MIPSOL_OBJ );
				final int solcnt = getIntInfo( GRB.CB_MIPSOL_SOLCNT );
				log_trace( "**** New solution at node " + nodecnt + ", obj " + obj + ", sol " + solcnt + " ****" );

			} else if ( where == GRB.CB_MIPNODE ) {
				// MIP node callback
				log_trace( "**** New node ****" );
				if ( getIntInfo( GRB.CB_MIPNODE_STATUS ) == GRB.OPTIMAL ) {}

			} else if ( where == GRB.CB_BARRIER ) {
				// Barrier callback
				final int itcnt = getIntInfo( GRB.CB_BARRIER_ITRCNT );
				final double primobj = getDoubleInfo( GRB.CB_BARRIER_PRIMOBJ );
				final double dualobj = getDoubleInfo( GRB.CB_BARRIER_DUALOBJ );
				final double priminf = getDoubleInfo( GRB.CB_BARRIER_PRIMINF );
				final double dualinf = getDoubleInfo( GRB.CB_BARRIER_DUALINF );
				final double cmpl = getDoubleInfo( GRB.CB_BARRIER_COMPL );
				log_info( itcnt + " " + primobj + " " + dualobj + " " + priminf + " " + dualinf + " " + cmpl );

			} else if ( where == GRB.CB_MESSAGE ) {
				// Message callback
				String msg = getStringInfo( GRB.CB_MSG_STRING );
				final double runtime = getDoubleInfo( GRB.CB_RUNTIME );

				if ( msg != null ) {
					while ( msg.startsWith( "\n" ) ) {
						msg = msg.substring( 1 );
					}
					while ( msg.endsWith( "\n" ) ) {
						msg = msg.substring( 0, msg.length() - 1 );
					}
					log_info( msg );
				}
			}
		} catch ( final GRBException e ) {
			log_error( "Error code: " + e.getErrorCode() );
			log_error( e.getMessage() );
			e.printStackTrace();
		} catch ( final Exception e ) {
			log_error( "Error during callback" );
			e.printStackTrace();
		}
	}

	private void log_trace( final String message ) {
		log.trace( this.msgPrefix + message );
	}

	private void log_info( final String message ) {
		log.info( this.msgPrefix + message );
	}

	private void log_warn( final String message ) {
		log.warn( this.msgPrefix + message );
	}

	private void log_error( final String message ) {
		log.error( this.msgPrefix + message );
	}
}