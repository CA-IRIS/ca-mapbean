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
package us.mn.state.dot.shape;

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

/**
 * The default implementation of LayerRenderer.
 * Shapes are all rendered with the same symbol.
 *
 * @author <a href="mailto:erik.engstrom@dot.state.mn.us">Erik Engstrom</a>
 */
public class DefaultRenderer implements LayerRenderer {

	/** Symbol to render */
	protected Symbol symbol;

	/**
	 * Create a new DefaultRenderer.
	 * @param s, symbol that all shapes will be painted with.
	 */
	public DefaultRenderer(Symbol s) {
		setSymbol(s);
	}

	/** Set the symbol used by the renderer */
	public void setSymbol(Symbol s) {
		symbol = s;
	}

	/** Get the shape that would be used to render this object */
	public Shape getShape(MapObject object) {
		return symbol.getShape(object);
	}

	/** Render the MapObject on the graphics */
	public void render(Graphics2D g, MapObject object) {
		symbol.draw(g, object.getShape());
	}

	/** Get the legend for this layer */
	public Component[] getLegend() {
		return new Component[0];
	}

	/** Get the bounds of the rendered MapObject */
	public Rectangle2D getBounds(MapObject object) {
		return symbol.getBounds(object);
	}

	/** Get tooltip text for the given map object */
	public String getTip(MapObject o) {
		return o.toString();
	}
}
