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

/**
 * A renderer that renders objects base on a numeric field and a set of class breaks.
 *
 * @author <a href="mailto:erik.engstrom@dot.state.mn.us">Erik Engstrom</a>
 * @version $Revision: 1.17 $ $Date: 2001/08/09 20:43:43 $ 
 */
public class ClassBreaksRenderer extends ShapeRenderer {

	private double [] classBreaks;
	private Symbol [] symbols;
	//private NumericField field;
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

	/** Paints a shape. */
	/*public final void paint( Graphics2D g, GeneralPath path, int index ){
		int classBreak = field.getRenderingClass( index, classBreaks );
		if (( classBreak > -1 ) && ( symbols[ classBreak ] != null )){
			symbols[ classBreak ].draw( g, path );
		}
	}*/
	
	public final void render( Graphics2D g, MapObject object ) {
		//int classBreak = field.getRenderingClass( index, classBreaks );
		//if ( ( classBreak > -1 ) && ( symbols[ classBreak ] != null ) ){
		//	return symbols[ classBreak ];
		//} 
		//return null;
		//FIXME
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