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
package us.mn.state.dot.shape.shapefile;

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

public final class DbaseInputStream {

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
	
	/** Number of records in file. */
	private final int records;
	
	/** Number of fields per record. */
	private final short fieldCount;
	
	/** List of field names. */
	private final List fields = new ArrayList();
	
	/** Index of the last record read from file. */
	private int lastRecord = 0;
	
	private ShapeDataInputStream in;
	
	/**
	 * Create a new DbaseInputStream from the supplied file name. */
	public DbaseInputStream( String name ) throws IOException {
		this( new FileInputStream( name ) );
	}

	public DbaseInputStream( URL url ) throws IOException {
		this( url.openStream() );
	}

	/**
	 * Create a DbaseInputStream from the supplied InputStream.
	 */
	public DbaseInputStream( InputStream inputStream ) throws IOException {
		in = new ShapeDataInputStream( inputStream );
		in.skipBytes( 4 );
		records = in.readLittleInt();
		short headSize = in.readLittleShort();
		short recSize = in.readLittleShort();
		fieldCount = ( short )( ( ( headSize - 1 ) / 32 ) - 1 );
		in.skipBytes( 20 );
		for ( int i = 0; i < fieldCount; i++ ) {
			fields.add( new ShapeField( in ) );
		}
		in.skipBytes( 1 );
		/*for ( int i = 0; i < records; i++ ) {
			in.skipBytes( 1 );
			for ( int j = 0; j < field.length; j++ ){
				field[ j ].loadData( i, in );
			}
		}*/
		//in.close();
	}
	
	/**
	 * Are there more records to read?
	 */
	public boolean hasNext() {
		return lastRecord < records;
	}
	
	/**
	 * Read the nextRecord in the database file.
	 */
	public Map nextRecord( ) throws IOException {
		HashMap map = new HashMap();
		Iterator it = fields.iterator();
		while ( it.hasNext() ) {
			ShapeField field = ( ShapeField ) it.next();
			map.put( field.getName(), in.readString( field.getLength() ) );
		}
		lastRecord++;
		return map;
	}
	
	/**
	 * Close the DbaseInputStream.
	 */
	public void close() throws IOException {
		in.close();
	}
	
	
	
	
	/**
	 * Class used for reading fields from database files.
	 */
	private class ShapeField {
		
		private String name = "";
		
		private int length = 0;
		
		public ShapeField( ShapeDataInputStream in ) throws IOException {
			String name = in.readString( 11 ).trim();
			char type = in.readString( 1 ).charAt( 0 );
			in.skipBytes( 4 );
			int length = in.readByte();
			int decimal = in.readByte();
			in.skipBytes( 14 );
		}
		
		public String getName() {
			return name;
		}
		
		public int getLength() {
			return length;
		}
	}
}