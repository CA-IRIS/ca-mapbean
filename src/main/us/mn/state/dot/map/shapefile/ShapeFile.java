/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2000-2005  Minnesota Department of Transportation
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
package us.mn.state.dot.map.shapefile;

import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

/**
 * ShapeFile is an ESRI shape file reader.  It reads the file and creates a
 * list of all the shapes (as java.awt.Shape).
 *
 * The specification for ESRI(tm) shapefile can be found at
 * http://www.esri.com/library/whitepapers/pdfs/shapefile.pdf
 *
 * @author Douglas Lau
 * @author <a href="mailto:erik.engstrom@dot.state.mn.us">Erik Engstrom</a>
 */
public class ShapeFile {

	/** File Code (magic number) to indicate ESRI shape file */
	static protected final int FILE_CODE = 9994;

	/** Supported version of ESRI shape file specification */
	static protected final int SHAPEFILE_VERSION = 1000;

	/** Shape file parse exception */
	protected class ParseException extends IOException {
		protected ParseException(String m) {
			super(m);
		}
	}

	/** Shape type code */
	protected int shapeType;

	/** Get the shape type code */
	public int getShapeType() {
		return shapeType;
	}

	/** Minimum X extent */
	protected double minX;

	/** Maximum X extent */
	protected double maxX;

	/** Minimum Y extent */
	protected double minY;

	/** Maximum Y extent */
	protected double maxY;

	/** Get the extent of the shape file */
	public Rectangle2D getExtent() {
		double height = maxY - minY;
		double width = maxX - minX;
		return new Rectangle2D.Double(minX, minY, width, height);
	}

	/** List of shapes in the ShapeFile */
	protected final List shapes = new LinkedList();

	/** Create a ShapeFile object from the specified file name */
	public ShapeFile( String name ) throws IOException {
		this(new File(name).toURL());
	}

	/** Create a ShapeFile object from the specified URL */
	public ShapeFile(URL url) throws IOException {
		ShapeDataInputStream in = new ShapeDataInputStream(
			url.openStream());
		try {
			readHeader(in);
			readShapes(in);
		}
		finally {
			in.close();
		}
	}

	/** Total (16-bit) words in the file */
	protected int total_words;

	/** Current word within file */
	protected int word = 0;

	/**
	 * Read the Shape File header (100 bytes).
	 *
	 *	Pos	Field		Type	Byte Order	Size
	 *
	 *	0	File Code	int	big		4
	 *	4	Unused		int	big		4
	 *	8	Unused		int	big		4
	 *	12	Unused		int	big		4
	 *	16	Unused		int	big		4
	 *	20	Unused		int	big		4
	 *	24	File Length	int	big		4
	 *	28	Version		int	little		4
	 *	32	Shape Type	int	little		4
	 *	36	Xmin		double	little		8
	 *	44	Ymin		double	little		8
	 *	52	Xmax		double	little		8
	 *	60	Ymax		double	little		8
	 *	68	Zmin		double	little		8
	 *	76	Zmax		double	little		8
	 *	84	Mmin		double	little		8
	 *	92	Mmax		double	little		8
	 */
	protected void readHeader(ShapeDataInputStream shapeIn)
		throws IOException
	{
		int magic = shapeIn.readInt();
		if(magic != FILE_CODE) {
			throw new ParseException(
				"Invalid shape file (bad file code)");
		}
		shapeIn.skipBytes(20); // unused (reserved for future use)
		total_words = shapeIn.readInt();
		int version = shapeIn.readLittleInt();
		if(version != SHAPEFILE_VERSION) {
			throw new ParseException(
				"Unsupported shape file version: " + version);
		}
		shapeType = shapeIn.readLittleInt();
		minX = shapeIn.readLittleDouble();
		minY = shapeIn.readLittleDouble();
		maxX = shapeIn.readLittleDouble();
		maxY = shapeIn.readLittleDouble();
		shapeIn.skipBytes(32); // ignore Z and M extents
		word = 50;
	}

	/** Read the shapes from the file */
	protected void readShapes(ShapeDataInputStream in) throws IOException {
		while(moreShapes())
			shapes.add(nextShape(in));
	}

	/** Check if the file contains more shapes */
	protected boolean moreShapes() {
		return word < total_words;
	}

	/** Get the list of all shapes from the file */
	public List getShapeList() {
		return shapes;
	}

	/** Null shape type */
	static public final int NULL = 0;

	/** Point shape type */
	static public final int POINT = 1;

	/** Polyline shape type */
	static public final int POLYLINE = 3;

	/** Polygon shape type */
	static public final int POLYGON = 5;

	/** Multipoint shape type */
	static public final int MULTIPOINT = 8;

	/** Point Z shape type */
	static public final int POINT_Z = 11;

	/** Polyline Z shape type */
	static public final int POLYLINE_Z = 13;

	/** Polygon Z shape type */
	static public final int POLYGON_Z = 15;

	/** Multipoint Z shape type */
	static public final int MULTIPOINT_Z = 18;

	/** Point M shape type */
	static public final int POINT_M = 21;

	/** Polyline M shape type */
	static public final int POLYLINE_M = 23;

	/** Polygon M shape type */
	static public final int POLYGON_M = 25;

	/** Multipoint M shape type */
	static public final int MULTIPOINT_M = 28;

	/** Current record counter */
	protected int record = 0;

	/** Read a geometric shape from a shape input stream */
	protected ShapeObject nextShape(ShapeDataInputStream in)
		throws IOException
	{
		int r = in.readInt();
		if(r != ++record) {
			throw new ParseException("Record (" + r +
				") is not in sequence: " + record);
		}
		int r_words = in.readInt();
		int t = in.readLittleInt();
		if(t != shapeType) {
			throw new ParseException("Shape type (" + t +
				") does not match header (" + shapeType + ")");
		}
		word += 6;
		GeneralPath path = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
		path.append(readPath(in), false);
		return new ShapeObject(path);
	}

	/** Read a path iterator from a shape input stream */
	protected PathIterator readPath(ShapeDataInputStream in)
		throws IOException
	{
		switch(shapeType) {
			case NULL:
				return new Null();
			case POINT:
				return new Point(in);
			case POLYLINE:
				return new PolyLine(in);
			case POLYGON:
				return new Polygon(in);
			case POINT_Z:
				return new PointZ(in);
			case POLYLINE_M:
				return new PolyLineM(in);
		}
		throw new IOException("Invalid shape type: " + shapeType);
	}

	/** Null shape */
	public class Null implements PathIterator {

		public int currentSegment(double [] coords) {
			return SEG_MOVETO;
		}

		public int currentSegment(float [] coords) {
			return SEG_MOVETO;
		}

		public void next() {}

		public boolean isDone() {
			return true;
		}

		public int getWindingRule() {
			return WIND_EVEN_ODD;
		}
	}

	/**
	 * Point shape structure
	 *
	 * Pos	Field		Type		Size
	 * 0	Shape Type	int (little)	4
	 * 4	X		double (little)	8
	 * 12	Y		double (little)	8
	 */
	public class Point implements PathIterator {

		protected final double x;
		protected final double y;
		protected boolean done = false;

		public Point(ShapeDataInputStream in) throws IOException {
			x = in.readLittleDouble();
			y = in.readLittleDouble();
			word += 8;
		}

		public int currentSegment(double [] coords) {
			coords[0] = x;
			coords[1] = y;
			return SEG_MOVETO;
		}

		public int currentSegment(float [] coords) {
			coords[0] = (float)x;
			coords[1] = (float)y;
			return SEG_MOVETO;
		}

		public void next() {
			done = true;
		}

		public boolean isDone() {
			return done;
		}

		public int getWindingRule() {
			return WIND_EVEN_ODD;
		}
	}

	/**
	 * Point Z shape structure
	 *
	 * Pos	Field		Type			Size
	 * 0	Shape Type	int (little)		4
	 * 4	X		double (little)		8
	 * 12	Y		double (little)		8
	 * 20	Z		double (little)		8
	 * 28	M		double (little)		8
	 */
	public class PointZ extends Point {

		protected final double z;
		protected final double measure;

		public PointZ(ShapeDataInputStream in) throws IOException {
			super(in);
			z = in.readLittleDouble();
			measure = in.readLittleDouble();
			word += 8;
		}
	}

	/**
	 * PolyLine / Polygon shape structure
	 *
	 * Pos	Field		Type			Size
	 * 0	Shape Type	int (little)		4
	 * 4	Xmin		double (little)		8
	 * 12	Ymin		double (little)		8
	 * 20	Xmax		double (little)		8
	 * 28	Ymax		double (little)		8
	 * 36	NumParts	int (little)		4
	 * 40	NumPoints	int (little)		4
	 * 44	Parts		int (little)		4 * NumParts
	 * X	Points		point			16 * NumPoints
	 */
	public abstract class Poly implements PathIterator {
		protected int numParts;
		protected int numPoints;
		protected int point = 0;
		protected double[] x;
		protected double[] y;
		protected int[] parts;

		public Poly(ShapeDataInputStream in) throws IOException {
			in.skipBytes(32);
			numParts = in.readLittleInt();
			numPoints = in.readLittleInt();
			word += 20;
			x = new double[numPoints];
			y = new double[numPoints];
			parts = new int[numParts];
			int i;
			for(i = 0; i < numParts; i++) {
				parts[i] = in.readLittleInt();
				word += 2;
			}
			for(i = 0; i < numPoints; i++) {
				x[i] = in.readLittleDouble();
				y[i] = in.readLittleDouble();
				word += 8;
			}
		}

		protected void fillCurrentSegment(double [] coords) {
			coords[0] = x[point];
			coords[1] = y[point];
		}

		public int currentSegment(double [] coords) {
			fillCurrentSegment(coords);
			return currentSegmentType();
		}

		protected void fillCurrentSegment(float [] coords) {
			coords[0] = (float)x[point];
			coords[1] = (float)y[point];
		}

		public int currentSegment(float [] coords) {
			fillCurrentSegment(coords);
			return currentSegmentType();
		}

		abstract protected int currentSegmentType();

		public void next() {
			point++;
		}

		public boolean isDone() {
			if(point < numPoints)
				return false;
			else
				return true;
		}

		public int getWindingRule() {
			return WIND_EVEN_ODD;
		}
	}

	/** PolyLine shape structure */
	public class PolyLine extends Poly {

		public PolyLine(ShapeDataInputStream in) throws IOException {
			super(in);
		}

		protected int currentSegmentType() {
			for(int i = 0; i < numParts; i++) {
				if(point == parts[i])
					return SEG_MOVETO;
			}
			return SEG_LINETO;
		}
	}

	/** Polygon shape structure */
	public class Polygon extends Poly {

		public Polygon(ShapeDataInputStream in) throws IOException {
			super(in);
		}

		protected int currentSegmentType() {
			int nextPoint = point + 1;
			for(int i = 0; i < numParts; i++) {
				int part = parts[i];
				if(point == part)
					return SEG_MOVETO;
				else if(nextPoint == part)
					return SEG_CLOSE;
			}
			if(nextPoint == numPoints)
				return SEG_CLOSE;
			return SEG_LINETO;
		}
	}

	/**
	 * PolyLineM / PolygonM shape structure
	 *
	 * Pos	Field		Type			Size
	 * 0	Shape Type	int (little)		4
	 * 4	Xmin		double (little)		8
	 * 12	Ymin		double (little)		8
	 * 20	Xmax		double (little)		8
	 * 28	Ymax		double (little)		8
	 * 36	NumParts	int (little)		4
	 * 40	NumPoints	int (little)		4
	 * 44	Parts		int (little)		4 * NumParts
	 * X	Points		point			16 * NumPoints
	 * Y	Mmin		double (little)		4
	 * Y+8	Mmax		double (little)		4
	 * Y+16 Marray		double (little)		4 * NumPoints
	 */
	public abstract class PolyM extends Poly {
		protected double[] mpoints;

		public PolyM(ShapeDataInputStream in) throws IOException {
			super(in);
			mpoints = new double[numPoints];
			in.skipBytes(16); // skip M range
			word += 8;
			for(int i = 0; i < numPoints; i++) {
				mpoints[i] = in.readLittleDouble();
				word += 4;
			}
		}
	}

	/** PolyLineM shape structure */
	public class PolyLineM extends PolyM {

		public PolyLineM(ShapeDataInputStream in) throws IOException {
			super(in);
		}

		protected int currentSegmentType() {
			for(int i = 0; i < numParts; i++) {
				if(point == parts[i])
					return SEG_MOVETO;
			}
			return SEG_LINETO;
		}
	}
}
