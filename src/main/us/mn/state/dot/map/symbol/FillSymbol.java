
//Title:        FillSymbol
//Version:      1.0
//Copyright:    Copyright (c) 1999
//Author:       Erik Engstrom
//Company:      MnDOT
//Description:  A FillSymbol is used to paint a polygon on a Map only SOLID_FILL is implemented.

package us.mn.state.dot.shape;

import java.awt.*;
import java.awt.geom.*;

public class FillSymbol extends Symbol {

    private int style = 0;

    /** fill constants */
    public static final int SOLID_FILL = 0;
    //public static final int TRANSPARENT_FILL = 1;
    //public static final int HORIZONTAL_FILL = 2;
    //public static final int VERTICAL_FILL = 3;
    //public static final int UPWARD_DIAGONAL_FILL = 4;
    //public static final int DOWNWARD_DIAGONAL_FILL = 5;
    //public static final int CROSS_FILL = 6;
    //public static final int DIAGONAL_CROSS_FILL = 7;
    //public static final int LIGHT_GRAY_FILL = 8;
    //public static final int GRAY_FILL = 9;
    //public static final int DARK_GRAY_FILL = 10;

    public FillSymbol() {
        this(Color.black);
    }

    public FillSymbol(Color c){
    	symbolType = super.FILL_SYMBOL;
        this.setColor(c);
    }

    /** Sets style used to render symbol */
    public void setStyle(int style){
    	if ((style > 0) && (style <= 10)){
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
		switch (style){
		case SOLID_FILL:
			g.fill(path);
			break;
		/*case TRANSPARENT_FILL:
			break;
		case HORIZONTAL_FILL:
			break;
		case VERTICAL_FILL:
			break;
		case UPWARD_DIAGONAL_FILL:
			break;
		case DOWNWARD_DIAGONAL_FILL:
			break;
		case CROSS_FILL:
			break;
		case DIAGONAL_CROSS_FILL:
			break;
		case LIGHT_GRAY_FILL:
			break;
		case GRAY_FILL:
			break;
		case DARK_GRAY_FILL:
			break;*/
		}
		if (this.getOutLine()){
			g.setColor(this.getOutLineColor());
			g.draw(path);
		}
	}
}
