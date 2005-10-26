/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2004-2005  Minnesota Department of Transportation
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
package us.mn.state.dot.map;

import java.awt.Graphics2D;
import java.util.List;
import java.util.LinkedList;

/**
 * Abstract layer renderer
 *
 * @author Douglas Lau
 */
abstract public class AbstractRenderer implements LayerRenderer {

	/** Name of renderer */
	protected final String name;

	/** List of all symbols to render */
	protected final LinkedList symbols = new LinkedList();

	/** Create a new abstract renderer */
	protected AbstractRenderer(String n) {
		name = n;
	}

	/** Get a string representation of the renderer */
	public String toString() {
		if(name == null)
			return super.toString();
		else
			return name;
	}

	/** Get a list of all symbols */
	public List getSymbols() {
		return symbols;
	}

	/** Get the symbol for the specified map object */
	abstract protected Symbol getSymbol(MapObject o);

	/** Render a map object on the map */
	public void render(Graphics2D g, MapObject o) {
		Symbol s = getSymbol(o);
		if(s != null)
			s.draw(g, o);
	}

	/** Get tooltip text for the specified map object */
	public String getTip(MapObject o) {
		return o.toString();
	}
}
