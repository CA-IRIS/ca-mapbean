package us.mn.state.dot.shape;


import java.awt.*;
import java.awt.geom.*;


/**
  * CirclePainter
  *
  * @author Douglas Lau
  * @version 0.1 - 25 Jan 1999
  */
public final class CirclePainter implements ShapePainter {


	private final double coords[] = new double[ 6 ];
	private final Ellipse2D.Double circle =
		new Ellipse2D.Double( 0, 0, 600.0, 600.0 );


	/** Get paint for a specified station */
	public void paint( Graphics2D g, GeneralPath path, int s ) {
		PathIterator pi = path.getPathIterator( new AffineTransform() );
		pi.currentSegment( coords );
		circle.x = coords[ 0 ];
		circle.y = coords[ 1 ];
		g.fill( circle );
	}
}
