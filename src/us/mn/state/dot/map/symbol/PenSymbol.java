/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2000-2007  Minnesota Department of Transportation
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
 */
package us.mn.state.dot.map.symbol;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import javax.swing.Icon;
import us.mn.state.dot.map.MapObject;
import us.mn.state.dot.map.Outline;
import us.mn.state.dot.map.Symbol;

/**
 * A pen symbol which can stroke/fill map objects.
 *
 * @author Erik Engstrom
 * @author Douglas Lau
 */
public class PenSymbol implements Symbol {

	/** Symbol label */
	protected final String label;

	/** Legend icon */
	protected final Icon legend = new LegendIcon();

	/** Fill color */
	protected final Color fill_color;

	/** Symbol outline */
	public final Outline outline;

	/** Create a new pen symbol with given label, outline and fill color */
	public PenSymbol(String l, Outline o, Color f) {
		label = l;
		outline = o;
		fill_color = f;
	}

	/** Create a new pen symbol with no outline */
	public PenSymbol(String l, Color f) {
		this(l, null, f);
	}

	/** Get the symbol label */
	public String getLabel() {
		return label;
	}

	/** Get the legend icon */
	public Icon getLegend() {
		return legend;
	}

	/** Draw a shape on a graphics context */
	public void draw(Graphics2D g, Shape s) {
		if(fill_color != null) {
			g.setColor(fill_color);
			g.fill(s);
		}
		if(outline != null) {
			g.setColor(outline.color);
			g.setStroke(outline.stroke);
			g.draw(s);
		}
	}

	/** Draw a map object with the pen symbol */
	public void draw(Graphics2D g, MapObject o) {
		Shape s = o.getShape();
		if(s != null)
			draw(g, s);
	}

	/** Inner class for icon displayed on the legend */
	protected class LegendIcon implements Icon {

		/** Width of icon */
		static public final int WIDTH = 25;

		/** Height of icon */
		static public final int HEIGHT = 15;

		/** Paint the icon onto the given component */
		public void paintIcon(Component c, Graphics g, int x, int y) {
			if(fill_color != null) {
				g.setColor(fill_color);
				g.fillRect(x + 1, y + 1, WIDTH - 2, HEIGHT - 2);
			}
			if(outline != null) {
				g.setColor(outline.color);
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
