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
package us.mn.state.dot.shape.symbol;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import javax.swing.Icon;
import javax.swing.JLabel;
import us.mn.state.dot.shape.MapObject;
import us.mn.state.dot.shape.Symbol;

/**
 * Base class for Symbol implementations.
 *
 * @author <a href="mailto:erik.engstrom@dot.state.mn.us">Erik Engstrom</a>
 */
public abstract class AbstractSymbol implements Symbol {

	protected boolean filled = true;
	protected Color color = Color.BLACK;
	protected Color outlineColor = Color.BLACK;
	protected boolean outlined = false;
	protected LineSymbol outlineSymbol = new SolidLine();
	private String label = "";

	public abstract void draw(Graphics2D g, Shape shape);

	public AbstractSymbol() {
		this(Color.BLACK);
	}

	public AbstractSymbol(Color c) {
		this(c, "", false);
	}

	public AbstractSymbol(Color c, String label) {
		this(c, label, false);
	}

	public AbstractSymbol(Color color, String label, boolean outlined) {
		this.color = color;
		this.outlined = outlined;
		this.label = label;
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

	public void setLabel(String l) {
		label = l;
	}

	public Component getLegend() {
		JLabel label = new JLabel();
		if((getLabel() != null) && (!getLabel().equals(""))) {
			label.setText(getLabel());
			ColorIcon icon = new ColorIcon(this);
			label.setIcon(icon);
		}
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

	class ColorIcon implements Icon {

		private int width;
		private int height;
		private final Symbol symbol;

		public ColorIcon(Symbol symbol) {
			this(symbol, 25, 15);
		}

		public ColorIcon(Symbol symbol, int width, int height) {
			this.symbol = symbol;
			this.width = width;
			this.height = height;
		}

		public void paintIcon(Component c, Graphics g, int x, int y) {
			if(symbol.isOutlined()) {
				g.setColor(symbol.getOutlineColor());
				g.drawRect(x, y, width - 1, height - 1);
			}
			if(symbol.isFilled()) {
				g.setColor(symbol.getColor());
				g.fillRect(x + 1, y + 1, width - 2, height - 2);
			}
		}

		public int getIconWidth() {
			return width;
		}

		public int getIconHeight(){
			return height;
		}
	}
}
