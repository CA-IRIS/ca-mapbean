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

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import us.mn.state.dot.shape.shapefile.ShapeObject;
import us.mn.state.dot.shape.shapefile.ShapeRenderer;

/**
 * A renderer that renders objects base on a numeric field and a set of class breaks.
 *
 * @author <a href="mailto:erik.engstrom@dot.state.mn.us">Erik Engstrom</a>
 * @version $Revision: 1.24 $ $Date: 2003/05/06 20:58:15 $ 
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
		Symbol symbol = getSymbol( object );
		if ( symbol != null ) {
			symbol.draw( g, object.getShape() );
		} 
	}
	
	/**
	 * Determine which class the value falls into.
	 */
	private Symbol getSymbol( MapObject object ) {
		ShapeObject shapeObject = ( ShapeObject ) object;
		Number number = ( Number ) shapeObject.getValue( field );
		if ( number == null ) {
			return null;
		}
		double value = number.doubleValue();
		for ( int i = 0; i < classBreaks.length; i++ ) {
			if ( value <= classBreaks[ i ] ) {
				return symbols[ i ];
			}
		}
		if ( value >= classBreaks[ classBreaks.length - 1 ] ) {
			return symbols[ classBreaks.length ];
		}
		return null;
	}
	
	/**
	 * Gets the shape that would be used to render this object.
	 */
	public Shape getShape( MapObject object ) {
		Symbol symbol = getSymbol( object );
		if ( symbol != null ) {
			return symbol.getShape( object );
		}
		return null;
	}
	
	public Rectangle2D getBounds( MapObject object ) {
		Symbol symbol = getSymbol( object );
		if ( symbol != null ) {
			return symbol.getBounds( object );
		}
		return null;
	}

	public final void setField( String field ){
		this.field = field;
	}

	public final String getField(){
		return field;
	}
	
}