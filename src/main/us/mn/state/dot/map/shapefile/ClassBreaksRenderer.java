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

import java.awt.*;
import java.awt.geom.*;
import us.mn.state.dot.shape.shapefile.ShapeRenderer;
import us.mn.state.dot.shape.shapefile.ShapeObject;

/**
 * A renderer that renders objects base on a numeric field and a set of class breaks.
 *
 * @author <a href="mailto:erik.engstrom@dot.state.mn.us">Erik Engstrom</a>
 * @version $Revision: 1.21 $ $Date: 2001/08/13 16:23:54 $ 
 */
public class ClassBreaksRenderer extends ShapeRenderer {

	private double [] classBreaks;
	private Symbol [] symbols;
	private String field;
	
	public ClassBreaksRenderer( String field, int breakCount ) {
		this.field = field;
		classBreaks = new double[ breakCount ];
		symbols = new Symbol[ breakCount + 1 ];
	}

	public ClassBreaksRenderer( String field, int breakCount,
			String name ){
		this( field, breakCount );
		setName( name );
	}

	public final void setBreak( int index, double value ){
		classBreaks[ index ] = value;
	}

	public final double getBreak( int index ){
		return classBreaks[ index ];
	}

	public final void setBreaks( double[] values ){
		classBreaks = values;
	}

	public final int getBreakCount(){
		return classBreaks.length;
	}

	public final void setSymbol( int index, Symbol s ){
		symbols[ index ] = s;
	}

	public final Symbol getSymbol( int index ){
		return symbols[ index ];
	}

	public final Symbol[] getSymbols(){
		return symbols;
	}
	
	/**
	 * Draw the object.
	 */
	public final void render( Graphics2D g, MapObject object ) {
		ShapeObject shapeObject = ( ShapeObject ) object;
		Number number = ( Number ) shapeObject.getValue( field );
		if ( number == null ) {
			return;
		}
		double value = number.doubleValue();
		int classBreak = getRenderingClass( value );
		Symbol symbol = null;
		if ( ( classBreak > -1 ) && ( symbols[ classBreak ] != null ) ){
			symbol = symbols[ classBreak ];
		} 
		if ( symbol != null ) {
			symbol.draw( g, object.getShape() );
		}
	}
	
	/**
	 * Determine which class the value falls into.
	 */
	private int getRenderingClass( double value ) {
		for ( int i = 0; i < classBreaks.length; i++ ) {
			if ( value <= classBreaks[ i ] ) {
				return i;
			}
		}
		if ( value >= classBreaks[ classBreaks.length - 1 ] ) {
			return classBreaks.length;
		}
		return -1;
	}
	
	/**
	 * Gets the shape that would be used to render this object.
	 */
	public Shape getShape( MapObject object ) {
		return object.getShape();
	}

	public final void setField( String field ){
		this.field = field;
	}

	public final String getField(){
		return field;
	}
	
}