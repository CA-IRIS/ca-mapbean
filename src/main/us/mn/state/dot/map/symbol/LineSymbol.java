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
import javax.swing.*;

/**
 * Abstract base class for shapes that render lines on a map.
 *
 * @author <a href="mailto:erik.engstrom@dot.state.mn.us">Erik Engstrom</a>
 * @version $Revision: 1.14 $ $Date: 2001/08/15 16:08:51 $ 
 */
public abstract class LineSymbol implements Symbol {

	/** Color to paint line */
	protected Color color = Color.black;
	
	/** The width of the line */
	protected int size = 0;
	
	public static final int DEFAULT_SIZE = 10;
	
	/** Label to use for legends. */
	private String label = "";
	
	/** The line stroke used to paint the line. */
	private Stroke stroke;

	public LineSymbol() {
		this( Color.black );
	}

	public LineSymbol( Color c ){
		this( c, DEFAULT_SIZE );
	}
	
	public LineSymbol( Color c, int size ) {
		color = c;
		setSize( size );
		stroke = createStroke();
		this.setOutLineSymbol( this );
	}
	
	/**
	 * Set the color to paint the line
	 * @param color, new color to use for painting.
	 */
	public final void setColor ( Color color ){
		this.color = color;
	}

	/**
	 * Get the color of this line.
	 */
	public Color getColor(){
		return color;
	}

	public boolean isFilled() {
		return true;
	}

	public void setFilled( boolean f ) {
	}

	public void setSize ( int size ){
		if ( size < 0 ) {
			throw new IllegalArgumentException( "Size can't be less than 0: " +
				size );
		} else {
			this.size = size;
			stroke = createStroke( );
		}
	}

	public int getSize(){
		return size;
	}

	public void setOutLined( boolean outlined ){
	}

	public boolean isOutLined(){
		return true;
	}

	public void setOutLineSymbol( LineSymbol symbol ){
	}

	public LineSymbol getOutLineSymbol(){
		return this;
	}

	public String getLabel(){
		return label;
	}

	public void setLabel( String l ){
		label = l;
	}
	
	
	public final Stroke getStroke(){
		return stroke;
	}

	protected abstract Stroke createStroke();

	/** Draw symbol on map */
	public final void draw( Graphics2D g, Shape path ){
		g.setColor( color );
		g.setStroke( stroke );
		g.draw( path );
	}

	public Component getLegend(){
		JLabel label = new JLabel();
		if ( ( getLabel() != null ) && ( ! getLabel().equals( "" ) ) ) {
			label.setText( getLabel() );
			ColorIcon icon = new ColorIcon( getColor() );
			label.setIcon( icon );
		}
		return label;
	}

	public Rectangle2D getBounds( MapObject object ) {
		return stroke.createStrokedShape( object.getShape() ).getBounds2D();
	}
	
	public Shape getShape(MapObject object) {
		return stroke.createStrokedShape( object.getShape() );
	}
	
	class ColorIcon implements Icon {
		private Color color;
		private int w, h;

		public ColorIcon(){
			this( Color.gray, 25, 15 );
		}

		public ColorIcon( Color color ){
			this( color, 25, 15 );
		}

		public ColorIcon( Color color, int w, int h ){
			this.color = color;
			this.w = w;
			this.h = h;
		}

		public void paintIcon( Component c, Graphics g, int x, int y ) {
			g.setColor( Color.black );
			g.drawRect( x, y, w - 1, h - 1 );
			g.setColor( color );
			g.fillRect( x + 1, y + 1, w - 2, h - 2 );
		}
		
		public Color getColor() {
			return color;
		}

		public void setColor( Color color ){
			this.color = color;
		}

		public int getIconWidth() {
			return w;
		}

		public int getIconHeight(){
			return h;
		}
	}
}