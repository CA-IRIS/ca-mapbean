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
  * ByteBuffer is a utility class for reading data from an InputStream and
  * converting integer or floating-point values from little-endian to big-
  * endian (or vice versa).
  *
  * @author Douglas Lau
  * @author <a href="mailto:erik.engstrom@dot.state.mn.us">Erik Engstrom</a>
  * @version $Revision: 0.6 $ $Date: 2001/04/19 16:49:31 $ 
  */
public final class ByteBuffer {

	/** Byte buffer */
	private final byte [] buffer;

	/** Read a byte buffer from an InputStream */
	public ByteBuffer( InputStream i, int size ) throws IOException {
		buffer = new byte [ size ];
		int offset = 0;
		while (offset < buffer.length) {
			int bytesRead = 0;
			bytesRead = i.read(buffer, offset, size - offset);
			if (bytesRead == -1) {
				throw new EOFException();
			}
			offset += bytesRead;
		}
	}

	/** Get a String from the buffer */
	public String getString( int offset, int length ) {
		return new String( buffer, offset, length );
	}

	/** Get an unsigned byte value from the buffer */
	public int getByte( int offset ) {
		try {
			ByteArrayInputStream bis =
				new ByteArrayInputStream( buffer, offset, 1 );
			DataInputStream dis = new DataInputStream( bis );
			return dis.readUnsignedByte();
		}
		catch( IOException e ) { return 0; }
		//return ( buffer[ offset ] * 0xff );
	}

	/** Get a short value from the buffer */
	public short getShort( int offset ) {
		try {
			ByteArrayInputStream bis =
				new ByteArrayInputStream( buffer, offset, 2 );
			DataInputStream dis = new DataInputStream( bis );
			return dis.readShort();
		}
		catch( IOException e ) { return 0; }
	}

	/** Get an int value from the buffer */
	public int getInt( int offset ) {
		int result = 0;
		for ( int i = 0, j = 3; i < 4; i++, j-- ) {
			result += (( buffer[ offset + i ] & 0xff ) << ( j * 8 ) );
			//result += ( getByte( offset + i ) << ( j * 8 ) );
		}
		return result;
	}

	/** Get a double value from the buffer */
	public double getDouble( int offset ) {
		int int1 = getInt( offset );
		int int2 = getInt( offset + 4 );
		long temp = (( int1 & 0xFFFFFFFFL ) << 32 ) + ( int2 & 0xFFFFFFFFL );
		return Double.longBitsToDouble( temp );
	}

	/** Reverse the byte-order of a 2-byte value */
	public void reverseBytes2( int offset ) {
		byte temp = buffer[ offset ];
		buffer[ offset ] = buffer[ offset + 1 ];
		buffer[ offset + 1 ] = temp;
	}

	/** Reverse the byte-order of a 4-byte value */
	public void reverseBytes4( int offset ) {
		byte temp;
		temp = buffer[ offset ];
		buffer[ offset ] = buffer[ offset + 3 ];
		buffer[ offset + 3 ] = temp;
		temp = buffer[ offset + 1 ];
		buffer[ offset + 1 ] = buffer[ offset + 2 ];
		buffer[ offset + 2 ] = temp;
	}

	/** Reverse the byte-order of an 8-byte value */
	public void reverseBytes8( int offset ) {
		byte temp;
		temp = buffer[ offset ];
		buffer[ offset ] = buffer[ offset + 7 ];
		buffer[ offset + 7 ] = temp;
		temp = buffer[ offset + 1 ];
		buffer[ offset + 1 ] = buffer[ offset + 6 ];
		buffer[ offset + 6 ] = temp;
		temp = buffer[ offset + 2 ];
		buffer[ offset + 2 ] = buffer[ offset + 5 ];
		buffer[ offset + 5 ] = temp;
		temp = buffer[ offset + 3 ];
		buffer[ offset + 3 ] = buffer[ offset + 4 ];
		buffer[ offset + 4 ] = temp;
	}
}
