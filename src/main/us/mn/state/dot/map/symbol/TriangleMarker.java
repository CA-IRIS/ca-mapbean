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

import java.awt.*;
import java.awt.geom.*;

/** A TriangleMarker is a triangular shape for representing data from point shape
 * files.
 * @since 1.0
 * @author Erik Engstrom
 */
public class TriangleMarker extends PointSymbol {
	/** 
	 * Constructs a TriangleMarker
	 */
	public TriangleMarker() {
		super();
	}

	/**
	 * Constructs a TriangleMarker with the color set to c.
	 * @param c The color of the TriangleMarker.
	 */
	public TriangleMarker( Color c ) {
		super( c );
	}

	protected Shape getShape( double x, double y ) {
		float length = getSize() / 2;
		GeneralPath p = new GeneralPath( GeneralPath.WIND_NON_ZERO, 4 );
		final float a = ( float ) ( length * Math.tan( 60 ) );
		final float c = ( float ) Math.sqrt( ( a * a ) + ( length * length ));
		p.moveTo( ( float )( x - length ), ( float )( y - a ));
		p.lineTo( ( float ) x, ( float ) ( y + c ));
		p.lineTo( ( float ) ( x + length ),  ( float ) ( y - a ));
		p.lineTo( ( float ) ( x - length ),  ( float ) ( y - a ));
		return p;
	}
} 