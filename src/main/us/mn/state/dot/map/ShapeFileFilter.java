/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2000  Minnesota Department of Transportation
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package us.mn.state.dot.shape;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import javax.swing.filechooser.*;

/**
 * A file filter for filtering for ESRI shape files.
 * @author <a href="mailto:erik.engstrom@dot.state.mn.us">Erik Engstrom</a>
 * @version $Revision: 1.2 $ $Date: 2001/05/07 22:16:24 $ 
 */
public class ShapeFileFilter extends FileFilter {	
	
	/** Description used if set */
	private static final String DESCRIPTION = "ESRI ShapeFiles";
	
	/** Creates a new ExtensionFileFilter that accepts all files */
    public ShapeFileFilter() {
    }
	
	public boolean accept( File f ){
		if ( f.isDirectory() ) {
			return true;
		}
		String extension = getFileExtension( f );
		if ( extension == null ) {
			return false;
		}
		if ( extension.equals( "shp" ) ){
			//FIXME check to make sure the .dbf file is also present
			return true;
		}
		return false;
	}
	
	/**
	 * Returns the extension of the filename.
	 * 
	 * @param file 
	 */
	private String getFileExtension( File file ) {
		String fileName = file.getName();
		int index = fileName.lastIndexOf( '.' );
		if ( index > 0 && index < fileName.length() - 1 ) {
			return fileName.substring( index + 1 ).toLowerCase();
		}	
		return null;
	}
	
	/**
     * Returns the human readable description of this filter.
     *
     * @see FileFilter#getDescription
     */
    public String getDescription() {
		return DESCRIPTION;
    }
}