package us.mn.state.dot.shape;

import java.io.*;
import java.util.*;


/**
  * dBase III PLUS table file reader
  *
  * @author Douglas Lau
  */
public final class Debase {


/**
  *	dBase header (32 bytes without field descriptors)
  *
  *	Position	Field				Value		Type				Size
  *
  *      0     dBase Version	3			byte					1
  *		1		Year (-1900)	YY			byte					1
  *		2		Update Month	MM			byte					1
  *		3		Update Day		DD			byte					1
  *		4		Table records				int (little)		4
  *		8		Header size					short (little)		2
  *		10		Record size					short (little)		2
  *		12		Reserved											  20
  */
	public final int records;
	public final short headSize;
	public final short recSize;
	public final short fields;
	public final Field [] field;
	public final ByteBuffer [] record;


	/** Constructor */
	public Debase( String name ) throws IOException {
		FileInputStream i = new FileInputStream( name );
		ByteBuffer header = new ByteBuffer( i, 32 );
		header.reverseBytes4( 4 );
		records = header.getInt( 4 );
		header.reverseBytes2( 8 );
		headSize = header.getShort( 8 );
		header.reverseBytes2( 10 );
		recSize = header.getShort( 10 );
		fields = (short)( ( ( headSize - 1 ) / 32 ) - 1 );
		field = new Field [ fields ];
		int o = 1;
		for( int f = 0; f < fields; f++ ) {
			field[ f ] = new Field( i, o );
			o += field[ f ].length;
		}
		i.skip( 1 );
		record = new ByteBuffer[ records ];
		for( int r = 0; r < records; r++ ) {
			record[ r ] = new ByteBuffer( i, recSize );
		}
		i.close();
	}


	/** dBase field class */
	public final class Field {

		/** Field properties */
		public final String name;
		public final char type;
		public final int offset;
		public final int length;
		public final int decimal;

		/** Create a new dBase field */
		public Field( InputStream i, int o ) throws IOException {
			ByteBuffer buffer = new ByteBuffer( i, 32 );
			name = buffer.getString( 0, 11 );
			type = buffer.getString( 11, 1 ).charAt( 0 );
			offset = o;
			length = buffer.getByte( 16 );
			decimal = buffer.getByte( 17 );
		}

		/** Get the field's value from a given record */
		public String getValue( ByteBuffer record ) {
			return record.getString( offset, length ).trim();
		}
	}


	static public void main( String args[] ) {
		try {
			Debase file = new Debase( args[ 0 ] );
System.out.println( "File: " + args[ 0 ] );
System.out.println( "Records: " + file.records );
System.out.println( "Header size: " + file.headSize );
System.out.println( "Record size: " + file.recSize );
System.out.println( "Fields: " + file.fields );
for( int i = 0; i < file.fields; i++ ) 
System.out.println( "   Field " + ( i + 1 ) + ": " + file.field[ i ].name +
	"   type: " + file.field[ i ].type +
	"   offset: " + file.field[ i ].offset +
	"   length: " + file.field[ i ].length +
	"   decimal: " + file.field[ i ].decimal );
for( int r = 0; r < 10; r++ ) {
	System.out.print( "Record " + r + ": " );
	for( int f = 0; f < file.fields; f++ )
		System.out.print( file.field[ f ].getValue( file.record[ r ] ) + "  " );
	System.out.println( "" );
}
		}
		catch( IOException e ) {
			e.printStackTrace();
		}
	}
}
