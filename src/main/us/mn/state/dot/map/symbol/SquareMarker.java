
//Title:        SignClient
//Version:
//Copyright:    Copyright (c) 1999
//Author:       Erik Engstrom
//Company:      MnDOT
//Description:  Your description

package us.mn.state.dot.shape;

import java.awt.*;
import java.awt.geom.*;

public final class SquareMarker extends PointSymbol {

	private final Rectangle2D.Double shape = new Rectangle2D.Double();

	public SquareMarker() {
		super();
	}

	public SquareMarker( Color c ) {
		super( c );
	}

	public SquareMarker( Color c, String label ){
		super( c, label );
	}

	public SquareMarker( Color c, String label, boolean outline ){
		super( c, label, outline );
	}

	protected final Shape getShape( double x, double y ){
		shape.setRect( ( x - ( size / 2 ) ), ( y -
			( size / 2 ) ), size, size );
		return shape;
	}
}