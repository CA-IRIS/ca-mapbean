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

import java.awt.geom.*;
import java.io.*;

/**
  * ShapeFactory reads shape data from a ShapeFileInputStream and creates 
  * pathiterators.
  *  The specification for ESRI(tm) shapefile can be found at 
  * http://www.esri.com/library/whitepapers/pdfs/shapefile.pdf
  *
  * @author Douglas Lau
  * @author <a href="mailto:erik.engstrom@dot.state.mn.us">Erik Engstrom</a>
  * @version $Revision: 1.2 $ $Date: 2001/07/18 16:20:28 $ 
  */
public final class ShapeFactory {
	
	/** Shape Type Constants */
	public static final int NULL = 0;
	
	public static final int POINT = 1;
	
	public static final int POLYLINE = 3;
	
	public static final int POLYGON = 5;
	
	public static final int POINTZ = 11;

	/** Create a Shape and return a PathIterator for it */
	static public PathIterator readShape( ShapeFileInputStream in ) 
			throws IOException {
		int skipped = in.skipBytes( 8 );
		int type = in.readLittleInt();
		switch( type ) {
			case NULL:
				return new Null();
			case POINT:
				return new Point( in );
			case POLYLINE:
				return new PolyLine( in );
			case POLYGON:
				return new Polygon( in );
			case POINTZ:
				return new PointZ( in );
			default:
				System.err.println( " Invalid shape type: " + type );
				System.err.println( " " + skipped + " bytes were skipped." );
				throw new IOException( "Invalid shape type" );
		}
	}

	/** Null shape */
	static public final class Null implements PathIterator {
		
		public Null() {
		}
		
		public int currentSegment( double [] coords ) {
			return SEG_MOVETO;
		}
		
		public int currentSegment( float [] coords ) {
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
	  * Position	Field			Type				Size
	  *	0			Shape Type	int (little)		4
	  *	4			X				double (little)	8
	  *	12			Y				double (little)	8
	  */
	static public final class Point implements PathIterator {
		
		private final double X;
		private final double Y;
		private boolean done = false;
		
		public Point( ShapeFileInputStream in ) throws IOException {
			X = in.readLittleDouble();
			Y = in.readLittleDouble();
		}
		
		public Point( double x, double y ) {
			X = x;
			Y = y;
		}
		
		public int currentSegment( double [] coords ) {
			coords[ 0 ] = X;
			coords[ 1 ] = Y;
			return SEG_MOVETO;
		}
		
		public int currentSegment( float [] coords ) {
			coords[ 0 ] = ( float ) X;
			coords[ 1 ] = ( float ) Y;
			return SEG_MOVETO;
		}
		
		public void next() {
			done = true; 
		}
		
		public final boolean isDone() {
			return done; 
		}
		
		public final int getWindingRule() {
			return WIND_EVEN_ODD; 
		}
	}
		
	/**
	  * Point shape structure
	  *
	  * Position	Field			Type				Size
	  *	0			Shape Type	int (little)		4
	  *	4			X				double (little)	8
	  *	12			Y				double (little)	8
	  * 20          z               double (little) 8
	  */
	static public final class PointZ implements PathIterator {
		
		private final double X;
		private final double Y;
		private final double Z;
		private final double measure;
		private boolean done = false;
		
		public PointZ( ShapeFileInputStream in ) throws IOException {
			X = in.readLittleDouble();
			Y = in.readLittleDouble();
			Z = in.readLittleDouble();
			measure = in.readLittleDouble();
		}
		
		public PointZ( double x, double y, double z, double measure ) {
			X = x;
			Y = y;
			Z = z;
			this.measure = measure;
		}
		
		public int currentSegment( double [] coords ) {
			coords[ 0 ] = X;
			coords[ 1 ] = Y;
			return SEG_MOVETO;
		}
		
		public int currentSegment( float [] coords ) {
			coords[ 0 ] = ( float ) X;
			coords[ 1 ] = ( float ) Y;
			return SEG_MOVETO;
		}
		
		public void next() {
			done = true; 
		}
		
		public final boolean isDone() {
			return done; 
		}
		
		public final int getWindingRule() {
			return WIND_EVEN_ODD; 
		}
	}

	/**
	  * PolyLine / Polygon shape structure
	  *
	  * Position	Field			Type				Size
	  *	0			Shape Type	int (little)		4
	  *	4			Xmin			double (little)	8
	  *	12			Ymin			double (little)	8
	  *	20			Xmax			double (little)	8
	  *	28			Ymax			double (little)	8
	  *	36			NumParts		int (little)		4
	  *	40			NumPoints	int (little)		4
	  *	44			Parts			int (little)		4 * NumParts
	  *	X			Points		point					16 * NumPoints
	  */
	static public abstract class Poly implements PathIterator {
		protected int numParts;
		protected int numPoints;
		protected int point = 0;
		protected double[] xpoints;
		protected double[] ypoints;
		protected int[] parts;

		/** Private constructor */
		public Poly( ShapeFileInputStream in ) throws IOException {
			in.skipBytes( 32 );
			numParts = in.readLittleInt();
			numPoints = in.readLittleInt();
			xpoints = new double[ numPoints ];
			ypoints = new double[ numPoints ];
			parts = new int[ numParts ];
			int i;
			for( i = 0; i < numParts; i++ ) {
				parts[ i ] = in.readLittleInt();
			}
			for( i = 0; i < numPoints; i++ ) {
				xpoints[ i ] = in.readLittleDouble();
				ypoints[ i ] = in.readLittleDouble();

			}
		}
		
		protected final void fillCurrentSegment( double [] coords ) {
			coords[ 0 ] = xpoints[ point ];
			coords[ 1 ] = ypoints[ point ];
		}
		
		protected final void fillCurrentSegment( float [] coords ) {
			coords[ 0 ] = ( float ) xpoints[ point ];
			coords[ 1 ] = ( float ) ypoints[ point ];
		}
		
		public final void next() {
			point++; 
		}
		
		public final boolean isDone() {
			if ( point < numPoints ) {
				return false;
			}
			return true;
		}
		
		public final int getWindingRule() {
			return WIND_EVEN_ODD; 
		}
	}


	/** PolyLine shape structure */
	static public final class PolyLine extends Poly {

		/** Private constructor */
		public PolyLine( ShapeFileInputStream in ) throws IOException {
			super( in );
		}
		
		public int currentSegment( double [] coords ) {
			super.fillCurrentSegment( coords );
			return currentSegmentType();
		}
		
		public int currentSegment( float [] coords ) {
			super.fillCurrentSegment( coords );
			return currentSegmentType();
		}
		
		private int currentSegmentType() {
			for ( int i = 0; i < numParts; i++ ) {
				if ( point == parts[ i ] ) {
					return SEG_MOVETO;
				}
			}
			return SEG_LINETO;
		}
	}


	/** Polygon shape structure */
	static public final class Polygon extends Poly {

		/** Private constructor */
		public Polygon( ShapeFileInputStream in ) throws IOException {
			super( in );
		}
		
		public int currentSegment( double [] coords ) {
			super.fillCurrentSegment( coords );
			return currentSegmentType();
		}
		
		public int currentSegment( float [] coords ) {
			super.fillCurrentSegment( coords );
			return currentSegmentType();
		}
		
		private int currentSegmentType() {
			int nextPoint = point + 1;
			for ( int i = 0; i < numParts; i++ ) {
				int part = parts[ i ];
				if ( point == part ) {
					return SEG_MOVETO;
				} else if( nextPoint == part ) {
					return SEG_CLOSE;
				}
			}
			if ( nextPoint == numParts ) {
				return SEG_CLOSE;
			}
			return SEG_LINETO;
		}
	}
}