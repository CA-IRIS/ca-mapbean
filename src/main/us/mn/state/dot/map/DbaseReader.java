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
import java.util.*;
import java.net.*;
import java.util.jar.*;
import us.mn.state.dot.shape.*;

/**
  * dBase III PLUS table file reader
  *
  * @author Douglas Lau
  */

public final class DbaseReader {

/**
  *	dBase header (32 bytes without field descriptors)
  *
  *	Position	Field			Value		Type				Size
  *
  *     0       dBase Version	3			byte				1
  *		1		Year (-1900)	YY			byte				1
  *		2		Update Month	MM			byte				1
  *		3		Update Day		DD			byte				1
  *		4		Table records				int (little)		4
  *		8		Header size					short (little)		2
  *		10		Record size					short (little)		2
  *		12		Reserved										20
  */
	public final int records;
	public final short headSize;
	public final short recSize;
	public final short fields;
	public final Field [] field;
	
	/** Constructor */
	public DbaseReader( String name ) throws IOException {
		this( new FileInputStream( name ) );
	}

	public DbaseReader( URL url ) throws IOException {
		this( url.openStream() );
	}

	public DbaseReader( InputStream inputStream ) throws IOException {
		ShapeFileInputStream in = new ShapeFileInputStream( inputStream );
		in.skipBytes( 4 );
		records = in.readLittleInt();
		headSize = in.readLittleShort();
		recSize = in.readLittleShort();
		fields = ( short )( ( ( headSize - 1 ) / 32 ) - 1 );
		field = new Field [ fields ];
		in.skipBytes( 20 );
		for( int i = 0; i < fields; i++ ) {
			field[ i ] = FieldFactory.createField( in, records );
		}
		in.skipBytes( 1 );
		for ( int i = 0; i < records; i++ ) {
			in.skipBytes( 1 );
			for ( int j = 0; j < field.length; j++ ){
				field[ j ].loadData( i, in );
			}
		}
		in.close();
	}

	public Field [] getFields(){
		return field;
	}
	
}