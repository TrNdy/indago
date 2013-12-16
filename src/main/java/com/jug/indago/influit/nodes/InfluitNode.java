/**
 *
 */
package com.jug.indago.influit.nodes;

import java.util.List;

import org.scijava.plugin.SciJavaPlugin;

import com.jug.indago.influit.data.InfluitDatum;
import com.jug.indago.influit.exception.InfluitFormatException;

/**
 * @author jug
 */
public interface InfluitNode extends SciJavaPlugin {

	@Override
	public String toString();

	public List< String > getSupportedInputFormats();

	public List< String > getSupportedOutputFormats();

	public boolean canEvaluate();

	public void evaluate();

	public void getOutput( InfluitDatum data ) throws InfluitFormatException;
}
