
//Title:        Symbol
//Version:      1.0
//Copyright:    Copyright (c) 1999
//Author:       Erik Engstrom
//Company:      MnDOT
//Description:  Your description

package us.mn.state.dot.shape;

import java.awt.*;
import java.awt.geom.*;

public abstract class Symbol {
    protected int symbolType;
	private Color color = Color.black;
    private Color outlineColor = Color.black;
    private int size;
    private boolean outline = false;

    public static final int POINT_SYMBOL = 0;
    public static final int LINE_SYMBOL = 1;
    public static final int FILL_SYMBOL = 2;

    public abstract void draw(Graphics2D g, GeneralPath path);

    public abstract void setStyle(int s);

    public abstract int getStyle();

    public void setColor (Color color){
    	this.color = color;
    }

    public Color getColor(){
		return color;
	}

	public void setSize (int size){
		this.size = size;
	}

	public int getSize(){
		return size;
	}

	public void setOutLine(boolean outline){
		this.outline = outline;
	}

	public boolean getOutLine(){
		return outline;
	}

	public void setOutLineColor(Color c){
		outlineColor = c;
	}

	public Color getOutLineColor(){
		return outlineColor;
	}

	public int getSymbolType(){
		return symbolType;
	}
}