
//Title:        SignClient
//Version:
//Copyright:    Copyright (c) 1999
//Author:       Erik Engstrom
//Company:      MnDOT
//Description:  Your description
package us.mn.state.dot.shape;

import java.awt.*;
import java.awt.geom.*;

public class CircleMarker extends PointSymbol {

	private final Ellipse2D shape = new Ellipse2D.Double();

	public CircleMarker() {
		this( Color.black );
	}

	public CircleMarker( Color c ) {
		super( c );
	}

	protected Shape getShape(double x, double y){
		shape.setFrame(( x - ( getSize() / 2 )), ( y - ( getSize() / 2 )),
			getSize(), getSize() );
		return shape;
	}

}