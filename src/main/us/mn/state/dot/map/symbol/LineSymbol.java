/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2000-2004  Minnesota Department of Transportation
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
package us.mn.state.dot.map.symbol;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import javax.swing.Icon;
import javax.swing.JLabel;
import us.mn.state.dot.map.MapObject;
import us.mn.state.dot.map.Symbol;

/**
 * Abstract base class for shapes that render lines on a map.
 *
 * @author <a href="mailto:erik.engstrom@dot.state.mn.us">Erik Engstrom</a>
 */
abstract public class LineSymbol implements Symbol {

	/** Default line width */
	static public final float DEFAULT_SIZE = 0.1f;

	/** Color to paint line */
	protected Color color = Color.BLACK;

	/** The width of the line */
	protected final float size;

	/** Label to use for legends */
	protected final String label = "";

	/** The line stroke used to paint the line */
	protected Stroke stroke;

	public LineSymbol() {
		this(Color.BLACK);
	}

	public LineSymbol(Color c) {
		this(c, DEFAULT_SIZE);
	}

	public LineSymbol(Color c, float s) {
		color = c;
		size = s;
		stroke = createStroke();
	}

	/** Set the color to paint the line */
	public void setColor(Color color) {
		this.color = color;
	}

	/** Get the color of this line */
	public Color getColor() {
		return color;
	}

	public float getSize() {
		return size;
	}

	/** Set the color to paint the line */
	public void setOutlineColor(Color color) {
		setColor(color);
	}

	/** Get the color of this line */
	public Color getOutlineColor() {
		return getColor();
	}

	public String getLabel() {
		return label;
	}

	public Stroke getStroke() {
		return stroke;
	}

	protected abstract Stroke createStroke();

	public void draw(Graphics2D g, MapObject o) {
		Shape s = o.getShape();
		g.setColor(color);
		g.setStroke(stroke);
		g.draw(s);
	}

	public Component getLegend() {
		JLabel l = new JLabel();
		if((label != null) && (!label.equals(""))) {
			l.setText(label);
			ColorIcon icon = new ColorIcon(getColor());
			l.setIcon(icon);
		}
		return l;
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
			g.setColor(Color.BLACK);
			g.drawRect( x, y, w - 1, h - 1 );
			g.setColor( color );
			g.fillRect( x + 1, y + 1, w - 2, h - 2 );
		}

		public Color getColor() {
			return color;
		}

		public void setColor(Color color) {
			this.color = color;
		}

		public int getIconWidth() {
			return w;
		}

		public int getIconHeight() {
			return h;
		}
	}
}
