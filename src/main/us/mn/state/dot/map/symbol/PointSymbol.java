
//Title:        PointSymbol
//Version:      1.0
//Copyright:    Copyright (c) 1999
//Author:       Erik Engstrom
//Company:      MnDOT
//Description:  Symbol object used to render point shapes on map.

package us.mn.state.dot.shape;

import java.awt.*;
import java.awt.geom.*;
import java.lang.*;

public class PointSymbol extends Symbol {

    private int style = CIRCLE_MARKER;

    /** Point Style Constants */
    public static final int CIRCLE_MARKER = 0;
    public static final int SQUARE_MARKER = 1;
    public static final int TRIANGLE_MARKER = 2;
    public static final int CROSS_MARKER = 3;
    public static final int TRUE_TYPE_MARKER = 4;

    public PointSymbol() {
        this(CIRCLE_MARKER, Color.black);
    }

    public PointSymbol(Color c) {
    	this(CIRCLE_MARKER, c);
    }

    public PointSymbol(int style, Color c){
    	symbolType = super.POINT_SYMBOL;
        this.setSize(500);
        this.setColor(c);
        this.setStyle(style);
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
   	Point2D pt = path.getCurrentPoint();
   	float X = (float) pt.getX();
   	float Y = (float) pt.getY();
   	float radius = this.getSize() / 2;
   	Shape shape = null;
   	switch (style){
   	case CIRCLE_MARKER:
            shape = new Ellipse2D.Double((X - radius), (Y - radius),
            	(radius * 2), (radius * 2));
       	    break;
     	case SQUARE_MARKER:
       	    shape = new Rectangle2D.Double((X - radius), (Y - radius),
            	(radius * 2), (radius * 2));
      	    break;
        case TRIANGLE_MARKER:
       	    GeneralPath p = new GeneralPath(GeneralPath.WIND_NON_ZERO, 4);
       	    final float a = (float) (radius * Math.tan(60));
            final float c = (float) Math.sqrt((a * a) + (radius * radius));
            p.moveTo((X - radius), (Y - a));
            p.lineTo(X, (Y + (c)));
            p.lineTo((X + radius), (Y - a));
            p.lineTo((X - radius), (Y - a));
            shape = p;
            break;
        case CROSS_MARKER:
     	    final float d = radius / 6;
            GeneralPath p2 = new GeneralPath(GeneralPath.WIND_NON_ZERO, 13);
            p2.moveTo((X - radius), (Y - d));
            p2.lineTo((X - d), (Y - d));
            p2.lineTo((X - d),(Y - radius));
            p2.lineTo((X + d), (Y - radius));
            p2.lineTo((X + d), (Y - d));
            p2.lineTo((X + radius), (Y - d));
            p2.lineTo((X + radius), (Y + d));
            p2.lineTo((X + d), (Y + d));
            p2.lineTo((X + d), (Y + radius));
            p2.lineTo((X - d), (Y + radius));
            p2.lineTo((X - d), (Y + d));
            p2.lineTo((X - radius), (Y + d));
            p2.lineTo((X - radius), (Y - d));
            shape = p2;
     	    break;
        case TRUE_TYPE_MARKER:
     	    break;
        default:
            shape = new Ellipse2D.Double((X - radius), (Y - radius), radius * 2,
            	radius * 2);
            break;
   	}
   	g.fill(shape);
   	if (this.getOutLine()){
            g.setColor(this.getOutLineColor());
            g.draw(shape);
   	}
    }

    public int getSymbolType(){
  	return super.POINT_SYMBOL;
    }
}