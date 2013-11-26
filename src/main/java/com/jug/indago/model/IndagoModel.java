/**
 *
 */
package com.jug.indago.model;

import ij.ImagePlus;

import javax.swing.JScrollPane;

import com.jug.indago.Indago;



/**
 * @author jug
 */
public class IndagoModel {

	private final Indago main;
	private ImagePlus imgPlus;

	/**
	 * Creates and Indago Model (MVC pattern).
	 * This keeps a link to the Indago main class.
	 *
	 * @param main
	 *            indago main class
	 */
	public IndagoModel( final Indago main ) {
		this.main = main;
		this.setImgPlus( main.loadCurrentImage() );
	}

	/**
	 * @return the imgPlus
	 */
	public ImagePlus getImgPlus() {
		return imgPlus;
	}

	/**
	 * @param imgPlus
	 *            the imgPlus to set
	 */
	public void setImgPlus( final ImagePlus imgPlus ) {
		this.imgPlus = imgPlus;
	}

	/**
	 * @return the console built in Indago main.
	 */
	public JScrollPane getConsole() {
		return main.getConsole();
	}

}
