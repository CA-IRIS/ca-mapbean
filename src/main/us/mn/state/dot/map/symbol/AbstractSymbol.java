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
 * Base class for Symbol implementations.
 *
 * @author <a href="mailto:erik.engstrom@dot.state.mn.us">Erik Engstrom</a>
 * @version $Revision: 1.6 $ $Date: 2001/08/15 16:08:51 $ 
 */
public abstract class AbstractSymbol implements LegendItem, Symbol {

	protected Color color = Color.black;
	protected LineSymbol outlineSymbol = new SolidLine();
	protected int size;
	protected boolean outlined = false;
	protected boolean filled = true;
	private String label = "";

	public abstract void draw( Graphics2D g, Shape shape );

	public AbstractSymbol(){
		this( Color.black );
	}

	public AbstractSymbol( Color c ){
		this( c, "", false );
	}

	public AbstractSymbol( Color c, String label ){
		this( c, label, false );
	}

	public AbstractSymbol( Color color, String label, boolean outlined ){
		this.color = color;
		this.outlined = outlined;
		this.label = label;
	}

	public final void setColor ( Color color ){
		this.color = color;
	}

	public Color getColor(){
		return color;
	}

	public boolean isFilled() {
		return filled;
	}

	public void setFilled( boolean f ) {
		filled = f;
	}

	public void setSize ( int size ){
		if ( size < 0 ) {
			throw new IllegalArgumentException( "Size can't be less than 0: " +
				size );
		} else {
			this.size = size;
		}
	}

	public int getSize(){
		return size;
	}

	public void setOutLined( boolean outlined ){
		this.outlined = outlined;
	}

	public boolean isOutLined(){
		return outlined;
	}

	public void setOutLineSymbol( LineSymbol symbol ){
		outlineSymbol = symbol;
	}

	public LineSymbol getOutLineSymbol(){
		return outlineSymbol;
	}

	public String getLabel(){
		return label;
	}

	public void setLabel( String l ){
		label = l;
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
		Shape shape = object.getShape();
		if ( isOutLined() ) {
			return outlineSymbol.getBounds( object );
		} else {
			return object.getShape().getBounds2D();
		}
	}
	
	public Shape getShape( MapObject object ) {
		GeneralPath path = new GeneralPath();
		if ( isOutLined() ) {
			path.append( outlineSymbol.getStroke().createStrokedShape( 
				object.getShape() ), true );
		}
		path.append( object.getShape(), true );
		return path;
	}
	
	class ColorIcon implements Icon {
		
		/** Fill color of icon. */
		private Color color;
		private int width;
		private int height;

		public ColorIcon(){
			this( Color.gray, 25, 15 );
		}

		public ColorIcon( Color color ){
			this( color, 25, 15 );
		}

		public ColorIcon( Color color, int width, int height ){
			this.color = color;
			this.width = width;
			this.height = height;
		}

		public void paintIcon( Component c, Graphics g, int x, int y ) {
			g.setColor( Color.black );
			g.drawRect( x, y, width - 1, height - 1 );
			g.setColor( color );
			g.fillRect( x + 1, y + 1, width - 2, height - 2 );
		}
		
		public Color getColor() {
			return color;
		}

		public void setColor( Color color ){
			this.color = color;
		}

		public int getIconWidth() {
			return width;
		}

		public int getIconHeight(){
			return height;
		}
	}
}