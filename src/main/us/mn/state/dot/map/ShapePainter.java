package us.mn.state.dot.shape;


import java.awt.*;
import java.awt.geom.*;


/**
  * ShapePainter is an interface for painting shapes in a Layer.
  *
  * @author Douglas Lau
  */
public interface ShapePainter {

	/** Paint a specific shape
	  * @param g Graphics Context to paint on
	  * @param path GeneralPath definition of the shape to paint
	  * @param s Index (Record #) of the shape within the layer
	  */
	public void paint( Graphics2D g, GeneralPath path, int s );
}
