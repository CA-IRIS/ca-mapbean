
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

	public CircleMarker() {
		super();
	}

	public CircleMarker(Color c) {
		super(c);
	}

	protected Shape getShape(float x, float y){
		return new Ellipse2D.Double((x - (getSize() / 2)), (y - (getSize() / 2)),
			getSize(), getSize());
	}

}