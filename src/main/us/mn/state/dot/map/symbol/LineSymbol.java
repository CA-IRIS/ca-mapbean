
//Title:        LineSymbol
//Version:      1.0
//Copyright:    Copyright (c) 1999
//Author:       Erik Engstrom
//Company:      MnDOT
//Description:  Shape used for rendering line shapes on map.

package us.mn.state.dot.shape;

import java.awt.*;
import java.awt.geom.*;

public abstract class LineSymbol extends Symbol {

	public LineSymbol() {
		this(Color.black);
	}

	public LineSymbol(Color c){
		this.setColor(c);
	}

	public abstract BasicStroke getStroke();

	/** Draw symbol on map */
	public void draw(Graphics2D g, GeneralPath path){
		g.setColor(this.getColor());
		AffineTransform at = g.getTransform();
		BasicStroke stroke = getStroke();
		g.setStroke(stroke);
		g.draw(path);
	}
}
