
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
	public void draw( Graphics2D g, GeneralPath path ){
		g.setColor( this.getColor() );
		Point2D pt = path.getCurrentPoint();
		//float X = ( float ) pt.getX();
		//float Y = ( float ) pt.getY();
		//float radius = this.getSize() / 2;
		//Shape shape = getShape( pt.getX(), pt.getY() );
		g.fill( getShape( pt.getX(), pt.getY() ) );
		if ( this.getOutLine() ){
			g.setColor( this.getOutLineColor() );
			g.draw( getShape( pt.getX(), pt.getY() ) );
		}
	}
}
