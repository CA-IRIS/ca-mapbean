
//Title:        Renderer
//Version:      1.0
//Copyright:    Copyright (c) 1999
//Author:       Erik Engstrom
//Company:      MnDOT
//Description:  Interface all layer renderers must implement.

package us.mn.state.dot.shape;

import java.awt.*;
import java.awt.geom.*;

public abstract class ShapeRenderer {
    MapTip mapTip = null;
    Symbol symbol = null;

    public abstract void paint( Graphics2D g, GeneralPath path, int index);
    public abstract void setField(Field f);
    public abstract Field getField();
    public void setTip(MapTip m) {
        mapTip = m;
    }

    public Symbol getSymbol(){
        return symbol;
    }

    public void setSymbol(Symbol s){
        symbol = s;
    }

    public String getTip(ShapeLayer layer, int i){
        String result = null;
        if (mapTip != null){
            result = mapTip.getTip(layer, i);
        }
    	return result;
    }
}