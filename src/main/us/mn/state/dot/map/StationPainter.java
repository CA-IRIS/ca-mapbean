package us.mn.state.dot.shape;


import java.awt.*;
import java.awt.geom.*;


/**
  * StationPainter
  *
  * @author Douglas Lau
  * @version 0.2 - 25 Jan 1999
  */
public final class StationPainter implements ShapePainter {

	int offset = 0;


	/** Set the color offset */
	public void setOffset( int o ) {
		offset = o;
	}


	/** Get paint for a specified station */
	public void paint( Graphics2D g, GeneralPath path, int s ) {
		switch( ( s + offset ) % 3 ) {
			case 0: g.setPaint( Color.green ); break;
			case 1: g.setPaint( Color.yellow ); break;
			case 2: g.setPaint( Color.red ); break;
		}
		g.fill( path );
	}
}
