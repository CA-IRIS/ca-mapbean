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
  * Used by the ShapFile to create new Fields.
  *
  * @author <a href="mailto:erik.engstrom@dot.state.mn.us">Erik Engstrom</a>
  * @version $Revision: 1.6 $ $Date: 2001/07/09 21:10:19 $
  */
public final class FieldFactory {

/**
  *	dBase field descriptor 32 bytes
  *
  *	Position	Field			Value		Type				Size
  *
  *     0       name						byte				11
  *		11		type		   C,N,L,D 		byte				1
  *		12		adress						byte				4
  *		16		length						byte				1
  *		17		decimal						byte				1
  *		18		Reserved										14
  */
	static Field createField( ShapeFileInputStream in, int records ) 
			throws IOException {
		String name = in.readString( 11 ).trim();
		char type = in.readString( 1 ).charAt( 0 );
		in.skipBytes( 4 );
		int length = in.readByte();
		int decimal = in.readByte();
		in.skipBytes( 14 );
		Field result = null;
		switch ( type ) {
		case 'C': case 'D':
			result = new StringField( name, records, length );
			break;
		case 'N':
			if ( decimal == 0 ) {
				result = new IntegerField( name, records, length );
			} else {
				result = new DoubleField( name, records, length );
			}
			break;
		case 'L':
			result = new BooleanField( name, records, length );
			break;
		}
		return result;
	}
}