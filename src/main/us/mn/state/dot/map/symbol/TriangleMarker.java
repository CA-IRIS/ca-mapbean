package us.mn.state.dot.shape;

import java.awt.*;
import java.awt.geom.*;

/** A TriangleMarker is a triangular shape for representing data from point shape
 * files.
 * @since 1.0
 * @author Erik Engstrom
 */
public class TriangleMarker extends PointSymbol {
	/** 
	 * Constructs a TriangleMarker
	 */
	public TriangleMarker() {
		super();
	}

	/**
	 * Constructs a TriangleMarker with the color set to c.
	 * @param c The color of the TriangleMarker.
	 */
	public TriangleMarker(Color c) {
		super( c );
	}

	protected Shape getShape(double x,double y) {
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