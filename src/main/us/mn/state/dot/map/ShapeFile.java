package us.mn.state.dot.shape;

import java.io.*;
import java.util.*;


/**
  * ShapeFile is an ESRI shape file reader.  It reads the file and creates an
  * array of all the shapes and their coordinates.
  *
  * @author Douglas Lau
  * @version 0.5 - 16 Mar 1999
  */
public final class ShapeFile {


/**
  *	ShapeFile header (100 bytes)
  *
  *	Position	Field				Value		Type				Size
  *
  *		0		File Code		9994		int (big)			4
  *		4		Unused			0			int (big)			4
  *		8		Unused			0			int (big)			4
  *		12		Unused			0			int (big)			4
  *		16		Unused			0			int (big)			4
  *		20		Unused			0			int (big)			4
  *		24		File Length					int (big)			4
  *		28		Version			1000		int (little)		4
  *		32		Shape Type					int (little)		4
  *		36		Xmin							double (little)	8
  *		44		Ymin							double (little)	8
  *		52		Xmax							double (little)	8
  *		60		Ymax							double (little)	8
  *		68		Zmin				0.0		double (little)	8
  *		76		Zmax				0.0		double (little)	8
  *		84		Mmin				0.0		double (little)	8
  *		92		Mmax				0.0		double (little)	8
  */
	private final ByteBuffer header;


	/** List of shapes in the ShapeFile */
	private final ArrayList shapes = new ArrayList();
	public ArrayList getShapeList() { return shapes; }


	/** Constructor */
	public ShapeFile( String name ) throws IOException {
		ShapeTypes types = new ShapeTypes();
		FileInputStream i = new FileInputStream( name );
		header = new ByteBuffer( i, 100 );
		header.reverseBytes4( 28 );
		header.reverseBytes4( 32 );
		header.reverseBytes8( 36 );
		header.reverseBytes8( 44 );
		header.reverseBytes8( 52 );
		header.reverseBytes8( 60 );
		header.reverseBytes8( 68 );
		header.reverseBytes8( 76 );
		header.reverseBytes8( 84 );
		header.reverseBytes8( 92 );
		ByteBuffer rhead;
		ByteBuffer record;
		int size;
		try {
			while( true ) {
				rhead = new ByteBuffer( i, 8 );
				size = 2 * rhead.getInt( 4 );
				record = new ByteBuffer( i, size );
				shapes.add( types.createShape( record ) );
			}
		}
		catch( EOFException e ) {}
		i.close();
	}


	/** Get the file code */
	public int getFileCode() {
		return header.getInt( 0 );
	}


	/** Get the file length (16-bit words) */
	public int getFileLength() {
		return header.getInt( 24 );
	}


	/** Get the version number */
	public int getVersion() {
		return header.getInt( 28 );
	}


	/** Get the shape type */
	public int getShapeType() {
		return header.getInt( 32 );
	}


	/** Get the X minimum value */
	public double getXmin() {
		return header.getDouble( 36 );
	}


	/** Get the Y minimum value */
	public double getYmin() {
		return header.getDouble( 44 );
	}


	/** Get the X maximum value */
	public double getXmax() {
		return header.getDouble( 52 );
	}


	/** Get the Y maximum value */
	public double getYmax() {
		return header.getDouble( 60 );
	}
}
