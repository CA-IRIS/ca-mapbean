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
package us.mn.state.dot.map;

import java.awt.Graphics2D;

/**
 * The default implementation of LayerRenderer.
 * Shapes are all rendered with the same symbol.
 *
 * @author Erik Engstrom
 * @author Douglas Lau
 */
public class DefaultRenderer extends AbstractRenderer {

	/** Symbol to render */
	protected final Symbol symbol;

	/** Create a new DefaultRenderer */
	public DefaultRenderer(Symbol s) {
		super(s.getLabel());
		symbol = s;
		symbols.add(symbol);
	}

	/** Render a map object on the map */
	public void render(Graphics2D g, MapObject o) {
		symbol.draw(g, o);
	}
}
