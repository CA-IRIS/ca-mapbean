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


/**
  * ShapeTypes is a class for reading shapes from ESRI shape files.
  *
  * @author Douglas Lau
  * @author <a href="mailto:erik.engstrom@dot.state.mn.us">Erik Engstrom</a>
  * @version $Revision: 0.5 $ $Date: 2001/04/19 16:49:31 $ 
  */
public final class ShapeTypes {


	/** Null shape */
	public final class Null implements PathIterator {
		public Null() {}
		public int currentSegment( double [] coords ) {
			return SEG_MOVETO;
		}
		public int currentSegment( float [] coords ) {
			return SEG_MOVETO;
		}
		public void next() {}
		public boolean isDone() { return true; }
		public int getWindingRule() { return WIND_EVEN_ODD; }
	}


	/**
	  * Point shape structure
	  *
	  * Position	Field			Type				Size
	  *	0			Shape Type	int (little)		4
	  *	4			X				double (little)	8
	  *	12			Y				double (little)	8
	  */
	public final class Point implements PathIterator {
		private final double X;
		private final double Y;
		private boolean done = false;
		public Point( ByteBuffer b ) {
			b.reverseBytes8( 4 );
			X = b.getDouble( 4 );
			b.reverseBytes8( 12 );
			Y = b.getDouble( 12 );
		}
		public Point( double x, double y) {
			X = x;
			Y = y;
		}
		public int currentSegment( double [] coords ) {
			coords[ 0 ] = X;
			coords[ 1 ] = Y;
			return SEG_MOVETO;
		}
		public int currentSegment( float [] coords ) {
			coords[ 0 ] = (float)X;
			coords[ 1 ] = (float)Y;
			return SEG_MOVETO;
		}
		public void next() { done = true; }
		public final boolean isDone() { return done; }
		public final int getWindingRule() { return WIND_EVEN_ODD; }
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
	public abstract class Poly implements PathIterator {
		protected ByteBuffer buffer;
		protected int numParts;
		protected int numPoints;
		protected int points;
		protected int point = 0;

		/** Private constructor */
		public Poly( ByteBuffer b ) {
			buffer = b;
			buffer.reverseBytes8( 4 );
			buffer.reverseBytes8( 12 );
			buffer.reverseBytes8( 20 );
			buffer.reverseBytes8( 28 );
			buffer.reverseBytes4( 36 );
			buffer.reverseBytes4( 40 );
			numParts = buffer.getInt( 36 );
			numPoints = buffer.getInt( 40 );
			points = 44 + numParts * 4;
			int i;
			for( i = 0; i < numParts; i++ )
				buffer.reverseBytes4( 44 + i * 4 );
			for( i = 0; i < numPoints; i++ ) {
				buffer.reverseBytes8( points + i * 16 );
				buffer.reverseBytes8( 8 + points + i * 16 );
			}
		}
		protected final void fillCurrentSegment( double [] coords ) {
			coords[ 0 ] = buffer.getDouble( points + point * 16 );
			coords[ 1 ] = buffer.getDouble( 8 + points + point * 16 );
		}
		protected final void fillCurrentSegment( float [] coords ) {
			coords[ 0 ] = (float)buffer.getDouble( points + point * 16 );
			coords[ 1 ] = (float)buffer.getDouble( 8 + points + point * 16 );
		}
		public final void next() { point++; }
		public final boolean isDone() {
			if( point < numPoints ) return false;
			return true;
		}
		public final int getWindingRule() { return WIND_EVEN_ODD; }
	}


	/** PolyLine shape structure */
	public final class PolyLine extends Poly {

		/** Private constructor */
		public PolyLine( ByteBuffer b ) {
			super( b );
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
			for( int i = 0; i < numParts; i++ )
				if( point == buffer.getInt( 44 + i * 4 ) ) return SEG_MOVETO;
			return SEG_LINETO;
		}
	}


	/** Polygon shape structure */
	public final class Polygon extends Poly {

		/** Private constructor */
		public Polygon( ByteBuffer b ) {
			super( b );
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
			for( int i = 0; i < numParts; i++ ) {
				int part = buffer.getInt( 44 + i * 4 );
				if( point == part ) return SEG_MOVETO;
				if( nextPoint == part ) return SEG_CLOSE;
			}
			if( nextPoint == numParts ) return SEG_CLOSE;
			return SEG_LINETO;
		}
	}


	/** Shape Type Constants */
	public static final int NULL = 0;
	public static final int POINT = 1;
	public static final int POLYLINE = 3;
	public static final int POLYGON = 5;


	/** Create a Shape and return a PathIterator for it */
	public PathIterator createShape( ByteBuffer b ) {
		b.reverseBytes4( 0 );
		switch( b.getInt( 0 ) ) {
			case NULL:
				return new Null();
			case POINT:
				return new Point( b );
			case POLYLINE:
				return new PolyLine( b );
			case POLYGON:
				return new Polygon( b );
		}
		return null;
	}
}
