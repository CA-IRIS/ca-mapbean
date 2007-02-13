/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2000-2005  Minnesota Department of Transportation
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
import javax.swing.Icon;
import us.mn.state.dot.map.MapObject;
import us.mn.state.dot.map.Outline;
import us.mn.state.dot.map.Symbol;

/**
 * Base class for Symbol implementations.
 *
 * @author Erik Engstrom
 * @author Douglas Lau
 */
abstract public class AbstractSymbol implements Symbol {

	/** Symbol label */
	protected final String label;

	/** Legend icon */
	protected final Icon legend = new LegendIcon();

	/** Fill color */
	protected final Color fill_color;

	/** Symbol outline */
	protected final Outline outline;

	/** Draw a map object with this symbol */
	abstract public void draw(Graphics2D g, MapObject o);

	/** Create a new abstract symbol */
	public AbstractSymbol(String l, Outline o, Color f) {
		label = l;
		outline = o;
		fill_color = f;
	}

	/** Get the symbol label */
	public String getLabel() {
		return label;
	}

	/** Get the legend icon */
	public Icon getLegend() {
		return legend;
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
