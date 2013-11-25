import ij.plugin.PlugIn;

import com.jug.indago.Indago;

/**
 *
 */

/**
 * @author jug
 */
public class Indago_ implements PlugIn {

	/**
	 * @see ij.plugin.PlugIn#run(java.lang.String)
	 */
	@Override
	public void run( final String arg ) {
		Indago.main( null );
	}

}
