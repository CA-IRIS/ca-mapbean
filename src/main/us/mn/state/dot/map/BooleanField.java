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

//Title:        BooleanField
//Version:      1.0
//Copyright:    Copyright (c) 1999
//Author:       Erik Engstrom
//Company:      MnDOT
//Description:  Boolean field for dBase files.

package us.mn.state.dot.shape;

import java.io.*;

public final class BooleanField extends Field {

    /** Field properties */
	private final boolean [] data;

	/** Create a new dBase field */
	public BooleanField( String name, int size, int offset, int length ){
		super( Field.BOOLEAN_FIELD, name, offset, length );
		data = new boolean[ size ];
	}

	/** Get the field's value from a given record */
	public boolean getValue( int record ) {
		return data[ record ];
	}

	public int getLength(){
		return data.length;
	}

	public void setValue( int record, boolean value ){
		data[ record ] = value;
	}

	void setValue( int index, String record ){
		char tempChar = record.charAt( 0 );
		switch( tempChar ){
		case 'Y': case 'y': case 'T': case 't':
			data[ index ] = true;
			break;
		case 'N': case 'n': case 'F': case 'f':
			data[ index ] = false;
			break;
		}
	}

	public String getStringValue( int record ){
		return new Boolean( data[ record ] ).toString();
	}
}