
//Title:        SignClient
//Version:
//Copyright:    Copyright (c) 1999
//Author:       Erik Engstrom
//Company:      MnDOT
//Description:  Your description

package us.mn.state.dot.shape;

import java.awt.geom.*;
import java.awt.*;

public final class DefaultRenderer extends ShapeRenderer {

    private Symbol symbol;
	private NumericField field = null;
	private String name = null;

	public DefaultRenderer() {
	}

	public DefaultRenderer( Symbol s ){
		setSymbol( s );
	}

	public final void setSymbol( Symbol s ){
		symbol = s;
	}

	public final Symbol getSymbol(){
		return symbol;
	}

	public void paint( Graphics2D g, GeneralPath path, int index ){
		symbol.draw( g, path );
	}

	public void setField( NumericField f ){
		field = f;
	}

	public NumericField getField(){
		return field;
	}
}