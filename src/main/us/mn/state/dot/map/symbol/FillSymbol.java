
//Title:        FillSymbol
//Version:      1.0
//Copyright:    Copyright (c) 1999
//Author:       Erik Engstrom
//Company:      MnDOT
//Description:  A FillSymbol is used to paint a polygon on a Map only SOLID_FILL is implemented.

package us.mn.state.dot.shape;

import java.awt.*;
import java.awt.geom.*;

public class FillSymbol extends Symbol {

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

	public FillSymbol( Color c, String label, boolean outline ){
		super( c, label, outline );
	}

	/** Draw symbol on map */
	public void draw( Graphics2D g, GeneralPath path ){
		g.setColor( this.getColor() );
		g.fill( path );
		/*case TRANSPARENT_FILL:
			break;
		case HORIZONTAL_FILL:
			break;
		case VERTICAL_FILL:
			break;
		case UPWARD_DIAGONAL_FILL:
			break;
		case DOWNWARD_DIAGONAL_FILL:
			break;
		case CROSS_FILL:
			break;
		case DIAGONAL_CROSS_FILL:
			break;
		case LIGHT_GRAY_FILL:
			break;
		case GRAY_FILL:
			break;
		case DARK_GRAY_FILL:
			break;*/
		if ( getOutLine() ){
			g.setColor( getOutLineColor() );
			g.draw( path );
		}
	}
}
