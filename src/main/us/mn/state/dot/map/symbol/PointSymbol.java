
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
		this(Color.black);
	}

	public PointSymbol(Color c) {
		this.setSize(500);
		this.setColor(c);
	}

   abstract protected Shape getShape(float x, float y);

	/** Draw symbol on map */
	public void draw(Graphics2D g, GeneralPath path){
		g.setColor(this.getColor());
		Point2D pt = path.getCurrentPoint();
		float X = (float) pt.getX();
		float Y = (float) pt.getY();
		float radius = this.getSize() / 2;
		Shape shape = getShape(X, Y);
		g.fill(shape);
		if (this.getOutLine()){
			g.setColor(this.getOutLineColor());
			g.draw(shape);
		}
	}
}
