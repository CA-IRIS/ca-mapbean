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

//Title:        IntegerField
//Version:      1.0
//Copyright:    Copyright (c) 1999
//Author:       Erik Engstrom
//Company:      MnDOT
//Description:  Integer field for dbase files.

package us.mn.state.dot.shape;

import java.io.*;

public final class IntegerField extends NumericField {

    /** Field properties */
	private final int [] data;

    /** Create a new dBase field */
	public IntegerField( String name, int size, int offset, int length ){
		super( Field.INTEGER_FIELD, name, offset, length );
		data = new int[ size ];
	}

	public int getLength(){
		return data.length;
	}

	/** Get the field's value from a given record */
	public int getValue( int record ) {
		return data[ record ];
	}

	public void setValue( int record, int value ){
		data[ record ] = value;
	}

	void setValue( int index, String value ){
		data[ index ] = Integer.parseInt( value );
	}

	public String getStringValue( int record ){
		if ( data.length - 1 < record ) {
			System.out.println( "record = " + record );
		}
		return new Integer( data[ record ] ).toString();
	}

	/*public void setData( int[] data ){
		this.data = data;
	} */

	public final int[] getData(){
		return this.data;
	}

	public final int getRenderingClass( int index, double[] classBreaks ){
		int result = -1;

		for ( int i = 0; i < classBreaks.length ; i++ ) {
			if ( data[ index ] <= classBreaks[ i ] ) {
				result = i;
				break;
			}
		}
		if ( data[ index ] > classBreaks[ classBreaks.length - 1 ] ){
			result = classBreaks.length;
		}
		return result;
	}
}