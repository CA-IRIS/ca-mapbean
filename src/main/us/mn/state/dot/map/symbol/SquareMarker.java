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
 * A PointSymbol that renders a square.
 *
 * @author <a href="mailto:erik.engstrom@dot.state.mn.us">Erik Engstrom</a>
 * @version $Revision: 1.11 $ $Date: 2001/08/14 21:29:58 $ 
 */
public final class SquareMarker extends PointSymbol {

	private final Rectangle2D.Double shape = new Rectangle2D.Double();

	/**
	 * Create a new SquareMarker.
	 */
	public SquareMarker() {
		super();
	}

	/**
	 * Create a new SquareMarker with the given color.
	 * @param c, the color to use.
	 */
	public SquareMarker( Color c ) {
		super( c );
	}

	/**
	 * Create a new SquareMarker with the given color and the given legend 
	 * lable.
	 *
	 * @param c, color to use.
	 * @param label, the label to use in this symbols legend.
	 */
	public SquareMarker( Color c, String label ){
		super( c, label );
	}

	/**
	 * Create a new SquareMarker with the given color, and label.
	 *
	 * @param c, color to use.
	 * @param lable, the label to use in this symbols legend.
	 * @param outlined, the symbol is outlined if true.
	 */
	public SquareMarker( Color c, String label, boolean outlined ){
		super( c, label, outlined );
	}

	protected final Shape getShape( double x, double y ){
		shape.setRect( ( x - ( size / 2 ) ), ( y -
			( size / 2 ) ), size, size );
		return shape;
	}
}
