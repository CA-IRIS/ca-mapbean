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

import java.awt.geom.Rectangle2D;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
  * ShapeFile is an ESRI shape file reader.  It reads the file and creates an
  * array of all the shapes and their coordinates.
  *
  * @author Douglas Lau
  * @author <a href="mailto:erik.engstrom@dot.state.mn.us">Erik Engstrom</a>
  * @version $Revision: 1.5 $ $Date: 2003/05/19 21:23:41 $
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
	private int shapeType;
	private int version;
	private double minX;
	private double maxX;
	private double minY;
	private double maxY;
	
	/** List of shapes in the ShapeFile */
	private final List shapes = new ArrayList();
	
	public ShapeFile( URL url ) throws IOException {
		ShapeDataInputStream in = new ShapeDataInputStream( url.openStream() );
		String location = url.toExternalForm();
		location = location.substring( 0, location.length() - 4 ) + ".dbf";
		URL dbUrl = new URL( location );
		DbaseInputStream dbaseStream = new DbaseInputStream( dbUrl );
		readData( in, dbaseStream );
	}
	
	/** Constructor */
	public ShapeFile( String name ) throws IOException {
		ShapeDataInputStream in = new ShapeDataInputStream( 
			new FileInputStream( name + ".shp" ) );
		DbaseInputStream dbaseStream = new DbaseInputStream( name + ".dbf" );
		readData( in, dbaseStream );
	}
	
	public void readData( ShapeDataInputStream shapeIn,
			DbaseInputStream dbaseStream ) throws IOException {
		shapeIn.skipBytes( 28 ); //start of header unused
		version = shapeIn.readLittleInt();
		shapeType = shapeIn.readLittleInt();
		minX = shapeIn.readLittleDouble();
		minY = shapeIn.readLittleDouble();
		maxX = shapeIn.readLittleDouble();
		maxY = shapeIn.readLittleDouble();
		shapeIn.skipBytes( 32 ); //end of header unused
		try {
			while( dbaseStream.hasNext() ) {
				shapes.add( new ShapeObject( ShapeFactory.readShape( shapeIn ), 
					dbaseStream.nextRecord() ) );
			}
		} catch( EOFException e ) {
			throw new IOException( "Shape file and Dbase file have different " +
				" numbers of records." );
		}
		dbaseStream.close();
		shapeIn.close();
	}
	
	/**
	 * Get the extent of the shape file.
	 */
	public Rectangle2D getExtent() {
		double height = maxY - minY;
		double width = maxX - minX;
		return new Rectangle2D.Double( minX, minY, width, height );
	}
	
	public List getShapeList() {
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

}