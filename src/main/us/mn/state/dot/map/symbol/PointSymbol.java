
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

	public PointSymbol( Color c, String label, boolean outline ){
		super( c, label, outline );
	}

   abstract protected Shape getShape( double x, double y );

	/** Draw symbol on map */
	public final void draw( Graphics2D g, GeneralPath path ){
		Point2D pt = path.getCurrentPoint();
		if ( filled ) {
			g.setColor( color );
			g.fill( getShape( pt.getX(), pt.getY() ) );
		}
		if ( outline ){
			g.setColor( outlineColor );
			g.draw( getShape( pt.getX(), pt.getY() ) );
		}
	}
}
