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

package us.mn.state.dot.shape;

import java.awt.Shape;
import java.awt.Graphics2D;

/**
 * A default implementation of a ShapeRenderer.  Shapes are all rendered with the
 * same symbol.
 *
 * @author <a href="mailto:erik.engstrom@dot.state.mn.us">Erik Engstrom</a>
 * @version $Revision: 1.16 $ $Date: 2001/08/09 21:03:34 $ 
 */
public final class DefaultRenderer implements LayerRenderer {

	private String name = null;
	
	private Symbol symbol;

	/**
	 * Create a new DefaultRenderer.
	 * @param s, symbol that all shapes will be painted with.
	 */
	public DefaultRenderer( Symbol s ){
		setSymbol( s );
	}

	/**
	 * Is this object visible?
	 */
	public boolean isVisible( MapObject object ) {
		return true;
	}
	
	/** 
	 * Set the symbol used by the renderer.
	 */
	public void setSymbol( Symbol s ) {
		symbol = s;
	}
	
	/**
	 * Gets the shape that would be used to render this object.
	 */
	public Shape getShape( MapObject object ) {
		return object.getShape();
	}
	
	/**
	 * Renders the MapObject on the graphics.
	 * @param object, the MapObject to render.
	 */
	public void render( Graphics2D g, MapObject object ) {
		symbol.draw( g, object.getShape() );		
	}
	
	/**
	 * Get they symbols used by this renderer.
	 */
	public Symbol[] getSymbols() {
		return new Symbol[] { symbol };
	}
	
	/**
	 * Set the name of this renderer.
	 */
	public void setName( String s ) {
		name = s;
	}
	
	/** 
	 * Overrides Object.toString() 
	 */
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