
//Title:        SignClient
//Version:      
//Copyright:    Copyright (c) 1999
//Author:       Erik Engstrom
//Company:      MnDOT
//Description:  Your description

package us.mn.state.dot.shape;

import java.awt.*;
import java.awt.geom.*;

public class TriangleMarker extends PointSymbol {

	public TriangleMarker() {
		super();
	}

	public TriangleMarker( Color c ) {
		super( c );
	}

	protected Shape getShape( double x, double y ){
		float length = getSize() / 2;
		GeneralPath p = new GeneralPath( GeneralPath.WIND_NON_ZERO, 4 );
		final float a = ( float ) (length * Math.tan( 60 ));
		final float c = ( float ) Math.sqrt(( a * a ) + ( length * length ));
		p.moveTo(( x - length ), ( y - a ));
		p.lineTo( x, ( y + c ));
		p.lineTo(( x + length ), ( y - a ));
		p.lineTo(( x - length ), ( y - a ));
		return p;
	}
} 