
//Title:        SignClient
//Version:
//Copyright:    Copyright (c) 1999
//Author:       Erik Engstrom
//Company:      MnDOT
//Description:  Your description
package us.mn.state.dot.shape;

import java.awt.*;
import java.awt.geom.*;

public final class CircleMarker extends PointSymbol {

	private final Ellipse2D shape = new Ellipse2D.Double();

	public CircleMarker() {
		this( Color.black );
	}

	public CircleMarker( Color c ) {
		super( c );
	}

	protected final Shape getShape( double x, double y ){
		shape.setFrame(( x - ( size / 2 )), ( y - ( size / 2 )),
			size, size );
		return shape;
	}
}