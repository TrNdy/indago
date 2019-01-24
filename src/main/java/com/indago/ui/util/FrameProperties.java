/**
 *
 */
package com.indago.ui.util;

import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import javax.swing.JFrame;

/**
 * @author jug
 */
public class FrameProperties {

	public static void save( final File file, final Frame f ) throws Exception {
		final Properties p = new Properties();
		// restore the frame from 'full screen' first!
		f.setExtendedState( JFrame.NORMAL );

		final Rectangle r = f.getBounds();
		final int x = ( int ) r.getX();
		final int y = ( int ) r.getY();
		final int w = ( int ) r.getWidth();
		final int h = ( int ) r.getHeight();

		p.setProperty( "x", "" + x );
		p.setProperty( "y", "" + y );
		p.setProperty( "w", "" + w );
		p.setProperty( "h", "" + h );

		final BufferedWriter br = new BufferedWriter( new FileWriter( file ) );
		p.store( br, "Properties of the user frame" );
	}

	public static void load( final File file, final Frame f ) throws IOException {
		final Properties p = new Properties();
		final BufferedReader br = new BufferedReader( new FileReader( file ) );
		p.load( br );

		final int x = Integer.parseInt( p.getProperty( "x" ) );
		final int y = Integer.parseInt( p.getProperty( "y" ) );
		final int w = Integer.parseInt( p.getProperty( "w" ) );
		final int h = Integer.parseInt( p.getProperty( "h" ) );

		f.setBounds( x, y, w, h );
	}

	public static Rectangle getCenteredRectangle( int w, int h ) {
		final GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		final int maxwidth = gd.getDisplayMode().getWidth();
		final int maxheight = gd.getDisplayMode().getHeight();
		w = Math.min( w, maxwidth );
		h = Math.min( h, maxheight );
		final int x = ( maxwidth - w ) / 2;
		final int y = ( maxheight - h ) / 2;
		return new Rectangle( x, y, w, h );
	}

}
