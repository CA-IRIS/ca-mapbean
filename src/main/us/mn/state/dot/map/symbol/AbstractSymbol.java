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
public abstract class AbstractSymbol implements Symbol {

	protected final int size;
	protected Color color = Color.BLACK;
	protected boolean filled = true;
	protected boolean outlined = false;
	protected LineSymbol outlineSymbol = new SolidLine(Color.BLACK, 20);
	protected final String label;

	public abstract void draw(Graphics2D g, Shape shape);

	public AbstractSymbol(int size, Color color, String label,
		boolean outlined)
	{
		this.size = size;
		this.color = color;
		this.outlined = outlined;
		this.label = label;
	}

	public int getSize() {
		return size;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public Color getColor() {
		return color;
	}

	public boolean isFilled() {
		return filled;
	}

	public void setFilled(boolean f) {
		filled = f;
	}

	public void setOutlineColor(Color color) {
		outlineSymbol.setColor(color);
	}

	public Color getOutlineColor() {
		return outlineSymbol.getColor();
	}

	public void setOutlined(boolean outlined) {
		this.outlined = outlined;
	}

	public boolean isOutlined() {
		return outlined;
	}

	public String getLabel() {
		return label;
	}

	public Rectangle2D getBounds(MapObject object) {
		if(isOutlined()) {
			return outlineSymbol.getBounds(object);
		} else {
			return object.getShape().getBounds2D();
		}
	}

	public Shape getShape(MapObject object) {
		GeneralPath path = new GeneralPath();
		if(isOutlined()) {
			path.append(outlineSymbol.getStroke().createStrokedShape(
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
			if(isFilled()) {
				g.setColor(getColor());
				g.fillRect(x + 1, y + 1, WIDTH - 2, HEIGHT - 2);
			}
			if(isOutlined()) {
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
