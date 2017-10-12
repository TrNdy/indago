/**
 *
 */
package com.indago.io;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import weka.gui.ExtensionFileFilter;

/**
 * @author jug
 */
public class ProjectFolder {

	private final File folder;
	private final ProjectFolder parent;

	protected final Map< String, ProjectFile > mapFiles = new HashMap< String, ProjectFile >();
	protected final Map< String, ProjectFolder > mapFolders = new HashMap< String, ProjectFolder >();

	/**
	 * Creates a <code>ProjectFolder</code> with a given id.
	 *
	 * @param id
	 * @param baseFolder
	 * @throws IOException
	 */
	public ProjectFolder( final String id, final File baseFolder ) throws IOException {
		this.folder = baseFolder;
		this.parent = null;
		if ( !folder.exists() ) {
			if ( !folder.mkdirs() ) throw new IOException( "Given baseFolder did not exist but could not be created." );
		}
	}

	/**
	 * Creates a <code>ProjectFolder</code> with a given id within a given
	 * parent.
	 *
	 * @param id
	 * @param folderName
	 * @param parentFolder
	 * @throws IOException
	 */
	public ProjectFolder( final String id, final String folderName, final ProjectFolder parentFolder ) throws IOException {
		this.parent = parentFolder;
		this.folder = new File( parentFolder.folder, folderName );
		if ( !folder.exists() ) {
			if ( !folder.mkdirs() )
				throw new IOException( String.format( "Folder '%s' in given parentFolder did not exist but could not be created.", folderName ) );
		}
	}

	/**
	 * Deletes all contents of this <code>ProjectFolder</code> (by recursively
	 * deleting and recreating the empty base folder).
	 */
	public void deleteContent() throws IOException {
		FileUtils.deleteDirectory( getFolder() );
		if ( !getFolder().mkdirs() ) throw new IOException( "Given baseFolder (%s) could not be created!" );
	}

	/**
	 * Deletes all contents and the <code>ProjectFolder</code> itself.
	 */
	public void delete() throws IOException {
		FileUtils.deleteDirectory( getFolder() );
	}

	/**
	 * Returns all <code>ProjectFile</code>s stored in this
	 * <code>ProjectFolder</code>.
	 *
	 * @return all <code>ProjectFile</code>s from within this project folder.
	 */
	public Collection< ProjectFile > getFiles() {
		return mapFiles.values();
	}

	/**
	 * Returns all <code>ProjectFile</code>s stored in this
	 * <code>ProjectFolder</code> that comply with the given
	 * <code>ExtensionFileFilter</code>.
	 *
	 * @param extensionFileFilter
	 * @return
	 */
	public Collection< ProjectFile > getFiles( final ExtensionFileFilter extensionFileFilter ) {
		final ArrayList< ProjectFile > ret = new ArrayList< >();
		for ( final ProjectFile pf : getFiles() ) {
			if ( extensionFileFilter.accept( pf.getFile() ) ) {
				ret.add( pf );
			}
			ret.sort( new Comparator< ProjectFile >() {

				@Override
				public int compare( final ProjectFile o1, final ProjectFile o2 ) {
					return o1.getAbsolutePath().compareToIgnoreCase( o2.getAbsolutePath() );
				}
			} );
		}
		return ret;
	}

	/**
	 * Returns the <code>ProjectFile</code> stored by the given id.
	 *
	 * @param id
	 * @return <code>ProjectFile</code> or <code>null</code> if no file with
	 *         given id exists.
	 */
	public ProjectFile getFile( final String id ) {
		return mapFiles.get( id );
	}

	/**
	 * Returns all <code>ProjectFolder</code>s stored in this
	 * <code>ProjectFolder</code>.
	 *
	 * @return all <code>ProjectFolder</code>s from within the current project
	 *         folder.
	 */
	public Collection< ProjectFolder > getFolders() {
		return mapFolders.values();
	}

	/**
	 * Returns the <code>ProjectFolder</code> stored by the given id.
	 *
	 * @param id
	 * @return <code>ProjectFolder</code> or <code>null</code> if no project
	 *         folder with
	 *         given id exists.
	 */
	public ProjectFolder getFolder( final String id ) {
		return mapFolders.get( id );
	}

	/**
	 * @return the File pointing at this folder.
	 */
	public File getFolder() {
		return folder;
	}

	/**
	 * Adds a file to the ProjectFolder.
	 * The filename will also be used as its id.
	 *
	 * @param filename
	 */
	public ProjectFile addFile( final String filename ) {
		return addFile( filename, filename );
	}

	/**
	 * @param id
	 * @param filename
	 */
	public ProjectFile addFile( final String id, final String filename ) {
		final ProjectFile pf = new ProjectFile( id, this, filename );
		mapFiles.put( id, pf );
		return pf;
	}

	/**
	 * Adds a folder to the given ProjectFolder.
	 * The foldername will also be used as its id.
	 *
	 * @param foldername
	 * @throws IOException
	 */
	public ProjectFolder addFolder( final String foldername ) throws IOException {
		return addFolder( foldername, foldername );
	}

	/**
	 * @param id
	 * @param foldername
	 * @return
	 * @throws IOException
	 */
	public ProjectFolder addFolder( final String id, final String foldername ) throws IOException {
		final ProjectFolder pf = new ProjectFolder( id, foldername, this );
		mapFolders.put( id, pf );
		return pf;
	}

	/**
	 * @return the absolute path to this <code>ProjectFolder</code>.
	 */
	public String getAbsolutePath() {
		return folder.getAbsolutePath();
	}

	/**
	 * @return <code>true</code> if and only if the file or directory denoted
	 *         by this abstract pathname exists; <code>false</code> otherwise
	 */
	public boolean exists() {
		return folder.exists();
	}

	/**
	 * Creates this folder if it does not exist already.
	 * @return <code>true</code> if folder was created, <code>false</code> otherwise (including the case that folder already existed).
	 */
	public boolean mkdirs() {
		if (!exists()) {
			return folder.mkdirs();
		}
		return false;
	}

	/**
	 * Loads all files that are located in this <code>ProjectFolder</code>.
	 * The filename is also used as the id.
	 */
	public void loadFiles() {
		final File[] listOfFiles = getFolder().listFiles();
		for ( final File file : listOfFiles ) {
			this.addFile( file.getName() );
		}
	}

	/**
	 * Removes the given ProjectFile in case it is registered in this
	 * ProjectFolder.
	 *
	 * @param id
	 */
	public void removeFile( final String id ) {
		mapFiles.remove( id );
	}

	/**
	 * Removes the given ProjectFile in case it is registered in this
	 * ProjectFolder.
	 *
	 * @param pf
	 * @return true, if the given ProjectFile was found and removed.
	 */
	public boolean removeFile( final ProjectFile pf ) {
		for ( final String key : mapFiles.keySet() ) {
			if ( pf.getFilename().equals( mapFiles.get( key ).getFilename() ) ) {
				mapFiles.remove( key );
				return true;
			}
		}
		return false;
	}
}
