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
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import javax.swing.Icon;
import javax.swing.JLabel;
import us.mn.state.dot.map.MapObject;
import us.mn.state.dot.map.Symbol;

/**
 * Base class for Symbol implementations.
 *
 * @author <a href="mailto:erik.engstrom@dot.state.mn.us">Erik Engstrom</a>
 */
abstract public class AbstractSymbol implements Symbol {

	/** Size of the symbol */
	protected final int size;

	/** Fill color */
	protected Color color;

	/** Outline symbol */
	protected LineSymbol outline = new SolidLine(Color.BLACK, 20);

	/** Label */
	protected final String label;

	public abstract void draw(Graphics2D g, Shape shape);

	/** Create a new abstract symbol */
	public AbstractSymbol(int s, Color c, String l, Color o) {
		size = s;
		color = c;
		label = l;
		outline.setColor(o);
	}

	public int getSize() {
		return size;
	}

	/** Set the fill color (null means not filled) */
	public void setColor(Color c) {
		color = c;
	}

	/** Get the fill color (null means not filled) */
	public Color getColor() {
		return color;
	}

	/** Set the outline color (null means not outlined) */
	public void setOutlineColor(Color c) {
		outline.setColor(c);
	}

	/** Get the outline color (null means not outlined) */
	public Color getOutlineColor() {
		return outline.getColor();
	}

	public String getLabel() {
		return label;
	}

	public Rectangle2D getBounds(MapObject object) {
		if(getOutlineColor() != null) {
			return outline.getBounds(object);
		} else {
			return object.getShape().getBounds2D();
		}
	}

	public Shape getShape(MapObject object) {
		GeneralPath path = new GeneralPath();
		if(getOutlineColor() != null) {
			path.append(outline.getStroke().createStrokedShape(
				object.getShape()), true);
		}
		path.append(object.getShape(), true);
		return path;
	}

	/** Get the legend component for the symbol */
	public Component getLegend() {
		JLabel l = new JLabel();
		if(label != null && (!label.equals(""))) {
			l.setText(label);
			l.setIcon(new LegendIcon());
		}
		return l;
	}

	/** Inner class for icon displayed on the legend */
	protected class LegendIcon implements Icon {

		/** Width of icon */
		static public final int WIDTH = 25;

		/** Height of icon */
		static public final int HEIGHT = 15;

		/** Paint the icon onto the given component */
		public void paintIcon(Component c, Graphics g, int x, int y) {
			if(color != null) {
				g.setColor(getColor());
				g.fillRect(x + 1, y + 1, WIDTH - 2, HEIGHT - 2);
			}
			if(getOutlineColor() != null) {
				g.setColor(getOutlineColor());
				g.drawRect(x, y, WIDTH - 1, HEIGHT - 1);
			}
		}

		/** Get the icon width */
		public int getIconWidth() {
			return WIDTH;
		}

		/** Get the icon height */
		public int getIconHeight() {
			return HEIGHT;
		}
	}
}
