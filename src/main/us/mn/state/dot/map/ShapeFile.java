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

/**
  * ShapeFile is an ESRI shape file reader.  It reads the file and creates an
  * array of all the shapes and their coordinates.
  *
  * @author Douglas Lau
  * @author <a href="mailto:erik.engstrom@dot.state.mn.us">Erik Engstrom</a>
  * @version $Revision: 0.6 $ $Date: 2001/05/12 00:10:40 $
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
	private final int shapeType;
	private final int version;
	private final double minX;
	private final double maxX;
	private final double minY;
	private final double maxY;
	
	/** List of shapes in the ShapeFile */
	private final ArrayList shapes = new ArrayList();
	
	/** Constructor */
	public ShapeFile( String name ) throws IOException {
		this( new FileInputStream( name ) );
	}

	public ShapeFile( URL url ) throws IOException {
		this( url.openStream() );
	}

	/** Constructor */
	public ShapeFile( InputStream i ) throws IOException {
		ShapeFileInputStream in = new ShapeFileInputStream( i );
		in.skipBytes( 28 ); //start of header unused
		version = in.readLittleInt();
		shapeType = in.readLittleInt();
		minX = in.readLittleDouble();
		minY = in.readLittleDouble();
		maxX = in.readLittleDouble();
		maxY = in.readLittleDouble();
		in.skipBytes( 32 ); //end of header unused
		try {
			while( true ) {
				shapes.add( ShapeFactory.readShape( in ) );
			}
		}
		catch( EOFException e ) {}
		i.close();
	}

	public ArrayList getShapeList() {
		return shapes; 
	}

	/** Get the version number */
	public int getVersion() {
		return version;
	}

	/** Get the shape type */
	public int getShapeType() {
		return shapeType;
	}

	/** Get the X minimum value */
	public double getXmin() {
		return minX;
	}

	/** Get the Y minimum value */
	public double getYmin() {
		return minY;
	}

	/** Get the X maximum value */
	public double getXmax() {
		return maxX;
	}

	/** Get the Y maximum value */
	public double getYmax() {
		return maxY;
	}
}