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

//Title:        PointSymbol
//Version:      1.0
//Copyright:    Copyright (c) 1999
//Author:       Erik Engstrom
//Company:      MnDOT
//Description:  Symbol object used to render point shapes on map.

package us.mn.state.dot.shape;

import java.awt.*;
import java.awt.geom.*;

public abstract class PointSymbol extends Symbol {

	public PointSymbol() {
		super( Color.black );
	}

	public PointSymbol( Color c ) {
		super( c );
		this.setSize( 500 );
	}

	public PointSymbol( Color c, String label ){
		super( c, label );
	}

	public PointSymbol( Color c, String label, boolean outlined ){
		super( c, label, outlined );
	}

	abstract protected Shape getShape( double x, double y );

	/** Draw symbol on map */
	public final void draw( Graphics2D g, GeneralPath path ){
		Point2D pt = path.getCurrentPoint();
		if ( isFilled() ) {
			g.setColor( color );
			g.fill( getShape( pt.getX(), pt.getY() ) );
		}
		if ( this.isOutLined() ){
			outlineSymbol.draw( g, getShape( pt.getX(), pt.getY() ) );
		}
	}
}
