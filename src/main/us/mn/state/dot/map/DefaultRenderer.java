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

	private NumericField field = null;
	private String name = null;

	public DefaultRenderer( Symbol s ){
		setSymbol( s );
	}

	public Symbol render( int index ){
		return symbol;
	}

	public void setField( Field f ){
		field = ( NumericField ) f;
	}

	public Field getField(){
		return field;
	}
}