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

//Title:        FieldFactory
//Version:      1.0
//Copyright:    Copyright (c) 1999
//Author:       Erik Engstrom
//Company:      MnDOT
//Description:  Creates fields from inputstream of dbasereader.

package us.mn.state.dot.shape;

import java.io.*;

public final class FieldFactory {

	static Field createField( InputStream i, int o, int records ) throws
			IOException {
		ByteBuffer buffer = new ByteBuffer( i, 32 );
		String name = (buffer.getString( 0, 11 )).trim();
		char type = buffer.getString( 11, 1 ).charAt( 0 );
		int offset = o;
		int length = buffer.getByte( 16 );
		int decimal = buffer.getByte( 17 );
		Field result = null;
		switch ( type ) {
		case 'C': case 'D':
			result = new StringField( name, records, offset, length );
			break;
		case 'N':
			if ( decimal == 0 ) {
				result = new IntegerField( name, records, offset, length );
			} else {
				result = new DoubleField( name, records, offset, length );
			}
			break;
		case 'L':
			result = new BooleanField( name, records, offset, length );
			break;
		}
		return result;
	}
}