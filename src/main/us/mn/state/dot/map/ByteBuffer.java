package us.mn.state.dot.shape;

import java.io.*;


/**
  * ByteBuffer is a utility class for reading data from an InputStream and
  * converting integer or floating-point values from little-endian to big-
  * endian (or vice versa).
  *
  * @author Douglas Lau
  * @version 0.4 - 28 Jan 1999
  */
public final class ByteBuffer {


	/** Byte buffer */
	private final byte [] buffer;


	/** Read a byte buffer from an InputStream */
	public ByteBuffer( InputStream i, int size ) throws IOException {
		buffer = new byte [ size ];
		if( i.read( buffer ) < 0 ) throw new EOFException();
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
		try {
			ByteArrayInputStream bis =
				new ByteArrayInputStream( buffer, offset, 4 );
			DataInputStream dis = new DataInputStream( bis );
			return dis.readInt();
		}
		catch( IOException e ) { return 0; }
	}


	/** Get a double value from the buffer */
	public double getDouble( int offset ) {
		try {
			ByteArrayInputStream bis =
				new ByteArrayInputStream( buffer, offset, 8 );
			DataInputStream dis = new DataInputStream( bis );
			return dis.readDouble();
		}
		catch( IOException e ) { return 0d; }
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
