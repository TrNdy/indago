/**
 *
 */
package com.indago.plugins.seg;

import java.util.List;

import javax.swing.JPanel;

import org.scijava.log.Logger;

import com.indago.io.ProjectFolder;

import net.imagej.ImageJPlugin;
import net.imagej.ImgPlus;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.real.DoubleType;

/**
 * @author jug
 */
public interface IndagoSegmentationPlugin extends ImageJPlugin, AutoCloseable {

	JPanel getInteractionPanel();

	List< RandomAccessibleInterval< IntType > > getOutputs();

	void setProjectFolderAndData( ProjectFolder projectFolder, ImgPlus< DoubleType > rawData );

	String getUiName();

	public void setLogger( Logger logger );

	default boolean isUsable() { return true; };

	@Override default void close() throws Exception {
		// default implementation is intentionally empty
	}
}
