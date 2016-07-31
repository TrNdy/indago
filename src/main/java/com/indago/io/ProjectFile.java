/**
 *
 */
package com.indago.io;

import java.io.File;

/**
 * @author jug
 */
public class ProjectFile {

	private final String id;
	private final String filename;
	private final ProjectFolder parent;

	/**
	 * Creates a <code>ProjectFile</code> with identifier <code>id</code> that
	 * is known to be located in the given <code>parentFolder</code>.
	 *
	 * @param id
	 * @param parentFolder
	 * @param filename
	 */
	public ProjectFile( final String id, final ProjectFolder parentFolder, final String filename ) {
		this.id = id;
		this.parent = parentFolder;
		this.filename = filename;
	}

	public String getId() {
		return id;
	}

	public String getFilename() {
		return filename;
	}

	public ProjectFolder getParent() {
		return parent;
	}

	public String getAbsolutePath() {
		return parent.getAbsolutePath() + File.separator + getFilename();
	}

	public File getFile() {
		return new File( getAbsolutePath() );
	}

	public boolean exists() {
		return getFile().exists();
	}

	public boolean canRead() {
		return getFile().canRead();
	}

	public boolean canWrite() {
		return getFile().canWrite();
	}

	@Override
	public String toString() {
		return filename;
	}
}
