/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2000-2005  Minnesota Department of Transportation
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
package us.mn.state.dot.map.shapefile;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 * A file filter for filtering only ESRI shape files.
 *
 * @author <a href="mailto:erik.engstrom@dot.state.mn.us">Erik Engstrom</a>
 * @author Douglas Lau
 */
public class ShapeFileFilter extends FileFilter {

	/** Description of the file filter */
	static protected final String DESCRIPTION = "ESRI ShapeFiles";

	/** Get the extension of the given filename */
	static protected String getFileExtension(File file) {
		String fileName = file.getName();
		int i = fileName.lastIndexOf('.');
		if(i > 0 && i < fileName.length() - 1)
			return fileName.substring(i + 1).toLowerCase();
		return null;
	}

	/** Create a new file filter that accepts only ESRI shape files */
	public ShapeFileFilter() {
	}

	/** Test if the filter accepts the specified file */
	public boolean accept(File f) {
		if(f.isDirectory())
			return true;
		String ext = getFileExtension(f);
		if(ext != null && ext.equals("shp"))
			return true;
		return false;
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
