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
package us.mn.state.dot.map;

import java.awt.Component;

/**
 * The default implementation of LayerRenderer.
 * Shapes are all rendered with the same symbol.
 *
 * @author <a href="mailto:erik.engstrom@dot.state.mn.us">Erik Engstrom</a>
 * @author Douglas Lau
 */
public class DefaultRenderer extends AbstractRenderer {

	/** Symbol to render */
	protected final Symbol symbol;

	/** Create a new DefaultRenderer */
	public DefaultRenderer(Symbol s) {
		super(s.getLabel());
		symbol = s;
	}

	/** Get components to display the legend */
	public Component[] getLegend() {
		return new Component[] { symbol.getLegend() };
	}

	/** Get the symbol for the specified map object */
	protected Symbol getSymbol(MapObject o) {
		return symbol;
	}
}
