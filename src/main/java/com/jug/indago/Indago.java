/**
 *
 */
package com.jug.indago;

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.WindowManager;

import java.awt.BorderLayout;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import com.jug.indago.gui.IndagoGui;
import com.jug.indago.model.IndagoModel;

/**
 * This is the main class for the Indago project.
 * This class creates and holds the GUI-frame and all other Indago classes.
 *
 * @author jug
 */
public class Indago {

	/**
	 * The singleton instance of ImageJ.
	 */
	public static ImageJ ij;

	/**
	 * The singleton instance of the Indago main class.
	 */
	public static Indago singleton;


	/**
	 * The <code>JFrame</code> containing the main GUI.
	 */
	private static JFrame guiFrame;
	/**
	 * The IndagoGui.
	 */
	private static IndagoGui gui;
	/**
	 * The Pane that is used as console for this app.
	 */
	private JScrollPane consolePane;
	/**
	 * The JTextArea that hosts the console text.
	 */
	private JTextArea txtConsole;

	/**
	 * Properties to configure app (loaded and saved to properties file!).
	 */
	private static Properties props;
	/**
	 * Default x-position of the main GUI-window.
	 * This value will be used if the values in the properties file are not
	 * fitting on any of the currently attached screens.
	 */
	private static int DEFAULT_GUI_POS_X = 100;
	/**
	 * X-position of the main GUI-window. This value will be loaded from and
	 * stored in the properties file!
	 */
	private static int GUI_POS_X;
	/**
	 * Default y-position of the main GUI-window.
	 * This value will be used if the values in the properties file are not
	 * fitting on any of the currently attached screens.
	 */
	private static int DEFAULT_GUI_POS_Y = 100;
	/**
	 * Y-position of the main GUI-window. This value will be loaded from and
	 * stored in the properties file!
	 */
	private static int GUI_POS_Y;
	/**
	 * Width (in pixels) of the main GUI-window. This value will be loaded from
	 * and stored in the properties file!
	 */
	private static int GUI_WIDTH = 800;
	/**
	 * Width (in pixels) of the main GUI-window. This value will be loaded from
	 * and stored in the properties file!
	 */
	private static int GUI_HEIGHT = 630;
	/**
	 * Divider location of the horizontal SplitPane.
	 */
	public static int HORIZONTAL_DIVIDER_LOCATION = 400;
	/**
	 * Divider location of the horizontal SplitPane.
	 */
	public static int VERTICAL_DIVIDER_LOCATION = 300;
	/**
	 * The path to usually open JFileChoosers at (except for initial load
	 * dialog).
	 */
	public static String DEFAULT_PATH = System.getProperty( "user.home" );

	/**
	 * PROJECT MAIN
	 * ============
	 *
	 * @param args
	 *            currently not used for anything.
	 */
	public static void main( final String[] args ) {

		ij = IJ.getInstance(); // this works only if started as plugin
		if ( ij == null ) {
			ij = new ImageJ(); // this is needed when started from e.g. Eclipse
			IJ.open( "/Users/jug/MPI/ProjectMansfeld/sample_small.tif" );
		}

		singleton = new Indago();
		guiFrame = new JFrame( "Indago (lat.) - track, trace, nose, dog" );
		singleton.initMainWindow( guiFrame );

		props = singleton.loadParams();
		GUI_POS_X = Integer.parseInt( props.getProperty( "GUI_POS_X", Integer.toString( DEFAULT_GUI_POS_X ) ) );
		GUI_POS_Y = Integer.parseInt( props.getProperty( "GUI_POS_Y", Integer.toString( DEFAULT_GUI_POS_X ) ) );
		GUI_WIDTH = Integer.parseInt( props.getProperty( "GUI_WIDTH", Integer.toString( GUI_WIDTH ) ) );
		GUI_HEIGHT = Integer.parseInt( props.getProperty( "GUI_HEIGHT", Integer.toString( GUI_HEIGHT ) ) );
		HORIZONTAL_DIVIDER_LOCATION = Integer.parseInt( props.getProperty( "HORIZONTAL_DIVIDER_LOCATION", Integer.toString( HORIZONTAL_DIVIDER_LOCATION ) ) );
		VERTICAL_DIVIDER_LOCATION = Integer.parseInt( props.getProperty( "VERTICAL_DIVIDER_LOCATION", Integer.toString( VERTICAL_DIVIDER_LOCATION ) ) );
		// Iterate over all currently attached monitors and check if screen position is actually possible,
		// otherwise fall back to the DEFAULT values and ignore the ones coming from the properties-file.
		boolean pos_ok = false;
		final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		final GraphicsDevice[] gs = ge.getScreenDevices();
		for ( int i = 0; i < gs.length; i++ ) {
			if ( gs[ i ].getDefaultConfiguration().getBounds().contains( new java.awt.Point( GUI_POS_X, GUI_POS_Y ) ) ) {
				pos_ok = true;
			}
		}
		// None of the screens contained the top-left window coordinates --> fall back onto default values...
		if ( !pos_ok ) {
			GUI_POS_X = DEFAULT_GUI_POS_X;
			GUI_POS_Y = DEFAULT_GUI_POS_Y;
		}

		gui = new IndagoGui( guiFrame, new IndagoModel( singleton ) );

		guiFrame.setSize( GUI_WIDTH, GUI_HEIGHT );
		guiFrame.setLocation( GUI_POS_X, GUI_POS_Y );
		guiFrame.getContentPane().add( gui, BorderLayout.CENTER );
		guiFrame.setVisible( true );

//		try {
//
//		} catch ( final UnsatisfiedLinkError ulr ) {
//			JOptionPane.showMessageDialog( Indago.guiFrame, "Could initialize Gurobi.\n" + "You might not have installed Gurobi properly or you miss a valid license.\n" + "Please visit 'www.gurobi.com' for further information.\n\n" + ulr.getMessage(), "Gurobi Error?", JOptionPane.ERROR_MESSAGE );
//			guiFrame.dispose();
//		}
	}

	// ===============================================================================================================

	/**
	 * Initializes the MotherMachine main app. This method contains platform
	 * specific code like setting icons, etc.
	 *
	 * @param guiFrame
	 *            the JFrame containing the MotherMachine.
	 */
	private void initMainWindow( final JFrame guiFrame ) {
		initConsolePane();

		// Set window-closing action...
		guiFrame.addWindowListener( new WindowAdapter() {

			@Override
			public void windowClosing( final WindowEvent we ) {
				saveParams();
				System.exit( 0 );
			}
		} );
//		final java.net.URL url = Indago.class.getResource( "gui/media/IconIndago128.png" );
//		final Toolkit kit = Toolkit.getDefaultToolkit();
//		final Image img = kit.createImage( url );
//		if ( !OSValidator.isMac() ) {
//			guiFrame.setIconImage( img );
//		}
//		if ( OSValidator.isMac() ) {
//			Application.getApplication().setDockIconImage( img );
//		}
	}

	/**
	 * Initializes the console and bends stdio and stderr to it...
	 */
	public void initConsolePane() {
		this.txtConsole = new JTextArea();

		this.txtConsole.append( Indago.getAboutString() );

		this.consolePane = new JScrollPane( txtConsole );
		consolePane.setBorder( BorderFactory.createEmptyBorder( 0, 15, 0, 0 ) );

		final OutputStream out = new OutputStream() {

			private final PrintStream original = new PrintStream( System.out );

			@Override
			public void write( final int b ) throws IOException {
				updateConsoleTextArea( String.valueOf( ( char ) b ) );
				original.print( String.valueOf( ( char ) b ) );
			}

			@Override
			public void write( final byte[] b, final int off, final int len ) throws IOException {
				updateConsoleTextArea( new String( b, off, len ) );
				original.print( new String( b, off, len ) );
			}

			@Override
			public void write( final byte[] b ) throws IOException {
				write( b, 0, b.length );
			}
		};

		final OutputStream err = new OutputStream() {

			private final PrintStream original = new PrintStream( System.out );

			@Override
			public void write( final int b ) throws IOException {
				updateConsoleTextArea( String.valueOf( ( char ) b ) );
				original.print( String.valueOf( ( char ) b ) );
			}

			@Override
			public void write( final byte[] b, final int off, final int len ) throws IOException {
				updateConsoleTextArea( new String( b, off, len ) );
				original.print( new String( b, off, len ) );
			}

			@Override
			public void write( final byte[] b ) throws IOException {
				write( b, 0, b.length );
			}
		};

		// Bend stdout and stderr to console...
		System.setOut( new PrintStream( out, true ) );
		System.setErr( new PrintStream( err, true ) );
	}

	/**
	 * @return A String containing basic Information about Indago.
	 */
	private static String getAboutString() {
		return "- - - - - - - - - - - - - - - - - - -\nIndago V1.0 using Influit V1.0\nAll systems up and running... enjoy!\n- - - - - - - - - - - - - - - - - - -\n\n";
	}

	private void updateConsoleTextArea( final String text ) {
		SwingUtilities.invokeLater( new Runnable() {

			@Override
			public void run() {
				txtConsole.append( text );
			}
		} );
	}

	/**
	 * @return
	 */
	public JScrollPane getConsole() {
		return consolePane;
	}

	/**
	 * Loads the file 'indago.properties' and returns an instance of
	 * {@link Properties} containing the key-value pairs found in that file.
	 *
	 * @return instance of {@link Properties} containing the key-value pairs
	 *         found in that file.
	 */
	@SuppressWarnings( "resource" )
	private Properties loadParams() {
		InputStream is = null;
		final Properties props = new Properties();

		// First try loading from the current directory
		try {
			final File f = new File( "indago.properties" );
			is = new FileInputStream( f );
		} catch ( final Exception e ) {
			is = null;
		}

		try {
			if ( is == null ) {
				// Try loading from classpath
				is = getClass().getResourceAsStream( "indago.properties" );
			}

			// Try loading properties from the file (if found)
			props.load( is );
		} catch ( final Exception e ) {
			System.out.println( "No properties file 'indago.properties' found in current path or classpath... default file is created!" );
		}

		return props;
	}

	/**
	 * Saves a file 'indago.properties' in the current folder.
	 * This file contains all Indago-specific properties as key-value
	 * pairs.
	 *
	 * @param props
	 *            an instance of {@link Properties} containing all key-value
	 *            pairs used by Indago.
	 */
	public void saveParams() {
		try {
			final File f = new File( "indago.properties" );
			final OutputStream out = new FileOutputStream( f );

			final java.awt.Point loc = guiFrame.getLocation();
			GUI_POS_X = loc.x;
			GUI_POS_Y = loc.y;
			GUI_WIDTH = guiFrame.getWidth();
			GUI_HEIGHT = guiFrame.getHeight();
			HORIZONTAL_DIVIDER_LOCATION = gui.getHorizontalDividerLocation();
			VERTICAL_DIVIDER_LOCATION = gui.getVerticalDividerLocation();

			props.setProperty( "GUI_POS_X", Integer.toString( GUI_POS_X ) );
			props.setProperty( "GUI_POS_Y", Integer.toString( GUI_POS_Y ) );
			props.setProperty( "GUI_WIDTH", Integer.toString( GUI_WIDTH ) );
			props.setProperty( "GUI_HEIGHT", Integer.toString( GUI_HEIGHT ) );
			props.setProperty( "HORIZONTAL_DIVIDER_LOCATION", Integer.toString( HORIZONTAL_DIVIDER_LOCATION ) );
			props.setProperty( "VERTICAL_DIVIDER_LOCATION", Integer.toString( VERTICAL_DIVIDER_LOCATION ) );

			props.store( out, "Indago properties" );
		} catch ( final Exception e ) {
			e.printStackTrace();
		}
	}

	/**
	 * Loads current IJ image into the OrthoSlicer
	 */
	public ImagePlus loadCurrentImage() {
		final ImagePlus imgPlus = WindowManager.getCurrentImage();
		if ( imgPlus == null ) {
			IJ.error( "There must be an active, open window!" );
			return null;
		}
//		final int[] dims = imgPlus.getDimensions(); // width, height, nChannels, nSlizes, nFrames
//		if ( dims[ 3 ] > 1 || dims[ 4 ] < 1 ) {
//			IJ.error( "The active, open window must contain an image with multiple frames, but no slizes!" );
//			return;
//		}
		return imgPlus;
	}

}
