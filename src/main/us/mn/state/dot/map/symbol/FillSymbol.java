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

//Title:        FillSymbol
//Version:      1.0
//Copyright:    Copyright (c) 1999
//Author:       Erik Engstrom
//Company:      MnDOT
//Description:  A FillSymbol is used to paint a polygon on a Map only SOLID_FILL is implemented.

package us.mn.state.dot.shape;

import java.awt.*;
import java.awt.geom.*;

public final class FillSymbol extends AbstractSymbol {

	/** fill constants */
	public static final int SOLID_FILL = 0;
	//public static final int TRANSPARENT_FILL = 1;
	//public static final int HORIZONTAL_FILL = 2;
	//public static final int VERTICAL_FILL = 3;
	//public static final int UPWARD_DIAGONAL_FILL = 4;
	//public static final int DOWNWARD_DIAGONAL_FILL = 5;
	//public static final int CROSS_FILL = 6;
	//public static final int DIAGONAL_CROSS_FILL = 7;
	//public static final int LIGHT_GRAY_FILL = 8;
	//public static final int GRAY_FILL = 9;
	//public static final int DARK_GRAY_FILL = 10;

	public FillSymbol() {
		super();
	}

	public FillSymbol( Color c ){
		super( c );
	}

	public FillSymbol( Color c, String label ){
		super( c, label );
	}

	public FillSymbol( Color c, String label, boolean outlined ){
		super( c, label, outlined );
	}

	/** Draw symbol on map */
	public void draw( Graphics2D g, Shape path ){
		if ( isFilled() ) { 
			g.setColor( color );
			g.fill( path );
		}
		if ( isOutLined() ){
			outlineSymbol.draw( g, path );
		}
	}
}
