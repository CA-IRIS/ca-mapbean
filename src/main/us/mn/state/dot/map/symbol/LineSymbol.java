
//Title:        LineSymbol
//Version:      1.0
//Copyright:    Copyright (c) 1999
//Author:       Erik Engstrom
//Company:      MnDOT
//Description:  Shape used for rendering line shapes on map.

package us.mn.state.dot.shape;

import java.awt.*;
import java.awt.geom.*;

public class LineSymbol extends Symbol {

	private int style = 0;

	/** Line Style Constants */
	public static final int SOLID_LINE = 0;
	public static final int DASH_LINE = 1;
	public static final int DOT_LINE = 2;
	public static final int DASH_DOT_LINE = 3;
	public static final int DASH_DOT_DOT_LINE = 4;

	public LineSymbol() {
		this(SOLID_LINE, Color.black);
	}

	public LineSymbol(Color c){
		this(SOLID_LINE, c);
	}

	public LineSymbol(int style, Color c){
		this.setStyle(style);
		this.symbolType = super.LINE_SYMBOL;
		this.setColor(c);
	}

	/** Sets style used to render symbol */
	public void setStyle(int style){
		if ((style > 0) && (style <= 4)){
			this.style = style;
		}
	}

	/** Returns style used to render symbol */
	public int getStyle(){
		return style;
	}

	/** Draw symbol on map */
	public void draw(Graphics2D g, GeneralPath path){
		g.setColor(this.getColor());
		AffineTransform at = g.getTransform();
		BasicStroke stroke = new BasicStroke(super.getSize());
		switch (style){
		case SOLID_LINE:
			break;
		case DASH_LINE:
			stroke = new BasicStroke(8, BasicStroke.CAP_BUTT,
			BasicStroke.JOIN_MITER, 10, new float[]{120, 40}, 0);
			break;
		case DOT_LINE:
			stroke = new BasicStroke(super.getSize(), BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_MITER, 10, new float[]{4, 4}, 0);
			break;
		case DASH_DOT_LINE:
			stroke = new BasicStroke(super.getSize(),BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_MITER, 10, new float[]{8, 2, 2, 4}, 0);
			break;
		case DASH_DOT_DOT_LINE:
			stroke = new BasicStroke(super.getSize(), BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_MITER, 10, new float[]{8, 1, 2, 1, 2, 1}, 0);
			break;
		}
		g.setStroke(stroke);
		g.draw(path);
	}
}
