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
import us.mn.state.dot.shape.DbaseReader.*;


/**
  * StationPainter
  *
  * @author Douglas Lau
  */
public final class StationPainter implements ShapePainter {

	int offset = 0;


	/** Set the color offset */
	public void setOffset( int o ) {
		offset = o;
	}


	/** Get paint for a specified station */
	public void paint( Graphics2D g, GeneralPath path, int s ) {
		switch( ( s + offset ) % 3 ) {
			case 0: g.setPaint( Color.green ); break;
			case 1: g.setPaint( Color.yellow ); break;
			case 2: g.setPaint( Color.red ); break;
		}
		g.fill( path );
	}
}
