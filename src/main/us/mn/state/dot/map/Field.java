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

import java.io.*;

/**
  * The Field class is the parent class for all field information for the dbase
  * table part of a shape file.
  *
  * @author <a href="mailto:erik.engstrom@dot.state.mn.us">Erik Engstrom</a>
  * @version $Revision: 1.6 $ $Date: 2001/05/12 00:10:40 $
  */
public abstract class Field {
	private final int type;
	private final String name;
	protected final int length;

	/** Constants for field type */
	public static final int INTEGER_FIELD = 3;
	public static final int DOUBLE_FIELD = 5;
	public static final int STRING_FIELD = 8;
	public static final int BOOLEAN_FIELD = 11;
	public static final int POINT_FIELD = 21;
	public static final int LINE_FIELD = 22;
	public static final int POLYGON_FIELD = 23;

	public Field( int type, String name, int length ){
		this.type = type;
		this.name = name;
		this.length = length;
	}

	public void loadData( int index, ShapeFileInputStream in ) 
			throws IOException {
		setValue( index, in.readString( length ) );
	}
	
	abstract void setValue( int index, String value );

	public abstract String getStringValue( int index );

	public final String getName(){
		return name;
	}

	public final int getType(){
    	return type;
	}

	public abstract int getLength();
}