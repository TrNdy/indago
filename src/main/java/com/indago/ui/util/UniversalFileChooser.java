/**
 *
 */
package com.indago.ui.util;

import java.awt.Component;
import java.awt.FileDialog;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;

import com.indago.util.OSValidator;

import weka.gui.ExtensionFileFilter;

/**
 * @author jug
 */
public class UniversalFileChooser {

	public static boolean showOptionPaneWithTitleOnMac = false;

	public static File showLoadFolderChooser(
			final Component parent,
			final String path,
			final String title ) {

		JFrame frame = null;
		try {
			if ( parent instanceof JFrame ) {
				frame = ( JFrame ) parent;
			} else {
				frame = ( JFrame ) SwingUtilities.getWindowAncestor( parent );
			}
		} catch ( final ClassCastException e ) {
			frame = null;
		}

		if ( OSValidator.isMac() && frame != null ) {

			if ( showOptionPaneWithTitleOnMac )
				JOptionPane.showMessageDialog( parent, "Next: " + title, "Select a folder...", JOptionPane.INFORMATION_MESSAGE );

			System.setProperty( "apple.awt.fileDialogForDirectories", "true" );
			final FileDialog fd = new FileDialog( frame, title, FileDialog.LOAD );
			fd.setDirectory( path );
			fd.setVisible( true );
			final File selectedFile = new File( fd.getDirectory() + "/" + fd.getFile() );
			if ( fd.getFile() == null ) { return null; }
			System.setProperty( "apple.awt.fileDialogForDirectories", "false" );
			return selectedFile;
		} else {
			return showSwingLoadFolderChooser( parent, path, title );
		}
	}

	private static File showSwingLoadFolderChooser(
			final Component parent,
			final String path,
			final String title ) {
		final JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory( new java.io.File( path ) );
		chooser.setDialogTitle( title );
		chooser.setFileFilter( new FileFilter() {

			@Override
			public final boolean accept( final File file ) {
				return file.isDirectory();
			}

			@Override
			public String getDescription() {
				return "We only take directories";
			}
		} );
		chooser.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
		chooser.setAcceptAllFileFilterUsed( false );

		if ( chooser.showOpenDialog( parent ) == JFileChooser.APPROVE_OPTION ) {
			return chooser.getSelectedFile();
		} else {
			return null;
		}
	}

	public static File showSaveFolderChooser( final Component parent, final String path, final String title ) {

		JFrame frame = null;
		try {
			if ( parent instanceof JFrame ) {
				frame = ( JFrame ) parent;
			} else {
				frame = ( JFrame ) SwingUtilities.getWindowAncestor( parent );
			}
		} catch ( final ClassCastException e ) {
			frame = null;
		}

		if ( OSValidator.isMac() && frame != null ) {

			if ( showOptionPaneWithTitleOnMac )
				JOptionPane.showMessageDialog( parent, "Next: " + title, "Select a folder...", JOptionPane.INFORMATION_MESSAGE );

			System.setProperty( "apple.awt.fileDialogForDirectories", "true" );
			final FileDialog fd = new FileDialog( frame, title );
			fd.setDirectory( path );
			fd.setVisible( true );
			final File selectedFile = new File( fd.getDirectory() + "/" + fd.getFile() );
			if ( fd.getFile() == null ) { return null; }
			System.setProperty( "apple.awt.fileDialogForDirectories", "false" );
			return selectedFile;

		} else {
			return showSwingSaveFolderChooser( parent, path, title );
		}
	}

	private static File showSwingSaveFolderChooser( final Component parent, final String path, final String title ) {
		final JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory( new java.io.File( path ) );
		chooser.setDialogTitle( title );
		chooser.setFileFilter( new FileFilter() {

			@Override
			public final boolean accept( final File file ) {
				return file.isDirectory();
			}

			@Override
			public String getDescription() {
				return "We only take directories";
			}
		} );
		chooser.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
		chooser.setAcceptAllFileFilterUsed( false );

		if ( chooser.showSaveDialog( parent ) == JFileChooser.APPROVE_OPTION ) {
			return chooser.getSelectedFile();
		} else {
			return null;
		}
	}

	public static File showSaveFileChooser(
			final Component parent,
			final String path,
			final String title,
			final ExtensionFileFilter fileFilter ) {

		JFrame frame = null;
		try {
			if ( parent instanceof JFrame ) {
				frame = ( JFrame ) parent;
			} else {
				frame = ( JFrame ) SwingUtilities.getWindowAncestor( parent );
			}
		} catch ( final ClassCastException e ) {
			frame = null;
		}

		if ( ( OSValidator.isMac() || OSValidator.isWindows() ) && frame != null ) {

			if ( showOptionPaneWithTitleOnMac )
				JOptionPane.showMessageDialog( parent, "Next: " + title, "Select a file...", JOptionPane.INFORMATION_MESSAGE );

			final FileDialog fd = new FileDialog( frame, title, FileDialog.SAVE );
			fd.setDirectory( path );
			if ( OSValidator.isMac() ) fd.setLocation( frame.getBounds().x + 50, frame.getBounds().y + 50 );
			fd.setFilenameFilter( fileFilter );
			fd.setVisible( true );

			String extensionToAdd = "";
			if ( fileFilter != null && fileFilter.getExtensions() != null && fileFilter.getExtensions().length != 0 ) {
				boolean correct = false;
				for ( final String extension : fileFilter.getExtensions() ) {
					if ( fd.getFile().endsWith( extension ) ) {
						correct = true;
					}
				}
				if ( !correct ) {
					extensionToAdd = "." + fileFilter.getExtensions()[ 0 ];
				}
			}
			final File selectedFile =
					new File( fd.getDirectory() + "/" + fd.getFile() + extensionToAdd );
			if ( fd.getFile() == null ) { return null; }
			return selectedFile;

		} else {
			final File file = showSwingSaveFileChooser( parent, path, title, fileFilter );
			if ( !fileFilter.accept( file ) ) {
				return new File( file.getAbsolutePath().concat( fileFilter.getExtensions()[ 0 ] ) );
			} else {
				return file;
			}
		}
	}

	private static File showSwingSaveFileChooser(
			final Component parent,
			final String path,
			final String title,
			final FileFilter fileFilter ) {
		final JFileChooser chooser = new JFileChooser();
		if ( path != null ) {
			chooser.setCurrentDirectory( new java.io.File( path ) );
		}
		chooser.setDialogTitle( title );
		if ( fileFilter != null ) {
			chooser.setFileFilter( fileFilter );
			chooser.setAcceptAllFileFilterUsed( false );
		} else {
			chooser.setAcceptAllFileFilterUsed( true );
		}
		chooser.setFileSelectionMode( JFileChooser.FILES_ONLY );

		if ( chooser.showSaveDialog( parent ) == JFileChooser.APPROVE_OPTION ) {
			return chooser.getSelectedFile();
		} else {
			return null;
		}
	}

	public static File showLoadFileChooser(
			final Component parent,
			final String path,
			final String title,
			final ExtensionFileFilter fileFilter ) {

		JFrame frame = null;
		try {
			if ( parent instanceof JFrame ) {
				frame = ( JFrame ) parent;
			} else {
				frame = ( JFrame ) SwingUtilities.getWindowAncestor( parent );
			}
		} catch ( final ClassCastException e ) {
			frame = null;
		}

		if ( ( OSValidator.isMac() || OSValidator.isWindows() ) && frame != null ) {

			if ( showOptionPaneWithTitleOnMac )
				JOptionPane.showMessageDialog( parent, "Next: " + title, "Select a file...", JOptionPane.INFORMATION_MESSAGE );

			final FileDialog fd = new FileDialog( frame, title, FileDialog.LOAD );
			fd.setDirectory( path );
			fd.setFilenameFilter( fileFilter );
			if ( OSValidator.isMac() ) fd.setLocation( frame.getBounds().x + 50, frame.getBounds().y + 50 );

			fd.setVisible( true );
			final File selectedFile = new File( fd.getDirectory() + "/" + fd.getFile() );
			if ( fd.getFile() == null ) { return null; }
			return selectedFile;

		} else {
			return showSwingLoadFileChooser( parent, path, title, fileFilter );
		}
	}

	public static List< File > showLoadMultipleFilesChooser(
			final Component parent,
			final String path,
			final String title,
			final ExtensionFileFilter fileFilter ) {

		JFrame frame = null;
		try {
			if ( parent instanceof JFrame ) {
				frame = ( JFrame ) parent;
			} else {
				frame = ( JFrame ) SwingUtilities.getWindowAncestor( parent );
			}
		} catch ( final ClassCastException e ) {
			frame = null;
		}

		if ( ( OSValidator.isMac() || OSValidator.isWindows() ) && frame != null ) {

			if ( showOptionPaneWithTitleOnMac )
				JOptionPane.showMessageDialog( parent, "Next: " + title, "Select a file...", JOptionPane.INFORMATION_MESSAGE );

			final FileDialog fd = new FileDialog( frame, title, FileDialog.LOAD );
			fd.setDirectory( path );
			fd.setFilenameFilter( fileFilter );
			fd.setMultipleMode( true );
			if ( OSValidator.isMac() ) fd.setLocation( frame.getBounds().x + 50, frame.getBounds().y + 50 );

			fd.setVisible( true );
			return new ArrayList<>( Arrays.asList( fd.getFiles() ) );

		} else {
			return showSwingLoadMultipleFilesChooser( parent, path, title, fileFilter );
		}
	}

	private static File showSwingLoadFileChooser(
			final Component parent,
			final String path,
			final String title,
			final FileFilter fileFilter ) {
		final JFileChooser chooser = new JFileChooser();
		if ( path != null ) {
			chooser.setCurrentDirectory( new java.io.File( path ) );
		}
		chooser.setDialogTitle( title );
		if ( fileFilter != null ) {
			chooser.setFileFilter( fileFilter );
			chooser.setAcceptAllFileFilterUsed( false );
		} else {
			chooser.setAcceptAllFileFilterUsed( true );
		}
		chooser.setFileSelectionMode( JFileChooser.FILES_ONLY );

		if ( chooser.showOpenDialog( parent ) == JFileChooser.APPROVE_OPTION ) {
			return chooser.getSelectedFile();
		} else {
			return null;
		}
	}

	private static List< File > showSwingLoadMultipleFilesChooser(
			final Component parent,
			final String path,
			final String title,
			final FileFilter fileFilter ) {
		final JFileChooser chooser = new JFileChooser();
		if ( path != null ) {
			chooser.setCurrentDirectory( new java.io.File( path ) );
		}
		chooser.setDialogTitle( title );
		if ( fileFilter != null ) {
			chooser.setFileFilter( fileFilter );
			chooser.setAcceptAllFileFilterUsed( false );
		} else {
			chooser.setAcceptAllFileFilterUsed( true );
		}
		chooser.setFileSelectionMode( JFileChooser.FILES_ONLY );
		chooser.setMultiSelectionEnabled( true );

		if ( chooser.showOpenDialog( parent ) == JFileChooser.APPROVE_OPTION ) {
			return new ArrayList<>( Arrays.asList( chooser.getSelectedFiles() ) );
		} else {
			return null;
		}
	}
}
