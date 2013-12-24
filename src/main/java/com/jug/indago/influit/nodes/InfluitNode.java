/**
 *
 */
package com.jug.indago.influit.nodes;

import java.util.List;

import javax.swing.JPanel;

import org.scijava.plugin.SciJavaPlugin;

import com.jug.indago.influit.data.InfluitDatum;
import com.jug.indago.influit.data.InfluitFormatIdentifyer;
import com.jug.indago.influit.exception.InfluitFormatException;

/**
 * @author jug
 */
public interface InfluitNode extends SciJavaPlugin {

	@Override
	public String toString();

	public List< InfluitFormatIdentifyer > getSupportedInputFormats();

	public List< InfluitFormatIdentifyer > getSupportedOutputFormats();

	public boolean canEvaluate();

	public void evaluate();

	public void getOutput( InfluitDatum data ) throws InfluitFormatException;

	public JPanel getPropertiesPanel();
}
