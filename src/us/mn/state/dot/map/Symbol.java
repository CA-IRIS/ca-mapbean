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
import javax.swing.Icon;

/**
 * A symbol is an object which renders map objects onto a map. A layer
 * renderer is responsible for selecting which symbol to use for a particular
 * map object.
 *
 * @author Erik Engstrom
 * @author Douglas Lau
 */
public interface Symbol {

	/** Get the symbol label */
	String getLabel();

	/** Get an icon to use for the legend */
	Icon getLegend();

	/** Draw the symbol for the specified map object */
	void draw(Graphics2D g, MapObject o);
}
