/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2000  Minnesota Department of Transportation
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

//Title:        Renderer
//Version:      1.0
//Copyright:    Copyright (c) 1999
//Author:       Erik Engstrom
//Company:      MnDOT
//Description:  Interface all layer renderers must implement.

package us.mn.state.dot.shape;

import java.awt.*;
import java.awt.geom.*;

public abstract class ShapeRenderer implements Renderer {
    MapTip mapTip = null;
    Symbol symbol = null;
	private String name = "";

    public abstract Symbol render( int index );
	public abstract void setField( Field f );
	public abstract Field getField();
	
    public void setTip( MapTip m ) {
        mapTip = m;
    }

    public Symbol getSymbol(){
        return symbol;
    }

    public void setSymbol( Symbol s ){
        symbol = s;
    }

    public String getTip( ShapeLayer layer, int i ){
        String result = null;
        if (mapTip != null){
            result = mapTip.getTip(layer, i);
        }
    	return result;
    }
	
	public Symbol[] getSymbols() {
		Symbol[] result = { symbol };
		return result;
	}
	
	public void setName( String s ) {
		name = s;
	}
	
	/** overrides Object.toString() */
	public final String toString(){
		String result = null;
		if ( name == null ) {
			result = super.toString();
		} else {
			result = name;
		}
		return result;
	}
}