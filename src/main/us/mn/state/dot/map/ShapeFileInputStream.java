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
  * Adds little endian double and integer reading to DataInputStream.
  *
  * @author <a href="mailto:erik.engstrom@dot.state.mn.us">Erik Engstrom</a>
  * @version $Revision: 1.1 $ $Date: 2001/05/12 00:10:40 $ 
  */
public class ShapeFileInputStream extends DataInputStream {

	/** Creates new ShapeFileInputStream */
    public ShapeFileInputStream( InputStream in ) {
		super( in );
    }
	
	/**
	 * Read a little endian short.
	 */
	public short readLittleShort() throws EOFException, IOException {
		short result = 0;
		result += ( readByte() & 0xff ); 
		result += ( readByte() & 0xff ) << 8;
		return result;
	}
	
	/**
	 * Read a little endian integer.
	 */
	public int readLittleInt() throws EOFException, IOException {
		int result = 0;
		for ( int i = 0; i < 4; i++ ) {
			result += ( ( readByte() & 0xff ) << ( i * 8 ) );
		}
		return result;
	}
	
	/** 
	 * Read a little endian double.
	 */
	public double readLittleDouble() throws EOFException, IOException {
		int first = readLittleInt();
		int second = readLittleInt();
		long temp = ( ( second & 0xFFFFFFFFL ) << 32 ) +
			( first & 0xFFFFFFFFL );
		return Double.longBitsToDouble( temp );
	}
	
	/**
	 * Read a String.
	 */
	public String readString( int length ) throws EOFException, IOException {
		byte[] buffer = new byte[ length ];
		for ( int i = 0; i < length; i++ ) {
			buffer[ i ] = readByte();
		}
		return new String( buffer ).trim();
	}
}