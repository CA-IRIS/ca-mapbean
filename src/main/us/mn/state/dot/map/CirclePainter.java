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


/**
  * CirclePainter
  *
  * @author Douglas Lau
  */
public final class CirclePainter implements ShapePainter {

	private final double coords[] = new double[ 6 ];
	private final Ellipse2D.Double circle =
		new Ellipse2D.Double( 0, 0, 600.0, 600.0 );

	/** Get paint for a specified station */
	public void paint( Graphics2D g, GeneralPath path, int s ) {
		PathIterator pi = path.getPathIterator( new AffineTransform() );
		pi.currentSegment( coords );
		circle.x = coords[ 0 ];
		circle.y = coords[ 1 ];
		g.fill( circle );
	}
}
