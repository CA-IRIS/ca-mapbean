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
import javax.swing.JLabel;

/**
 * A FillSymbol is used to paint a filled polygon on a map
 *
 * @author <a href="mailto:erik.engstrom@dot.state.mn.us">Erik Engstrom</a>
 */
public class FillSymbol extends AbstractSymbol {

	/** Create a new fill symbol */
	public FillSymbol() {
		super();
	}

	/** Create a new fill symbol of the given color */
	public FillSymbol(Color c) {
		super(c);
	}

	/** Create a new fill symbol with the given color and label */
	public FillSymbol(Color c, String label) {
		super(c, label);
	}

	/** Create a new fill symbol with the given color, label, and outline */
	public FillSymbol(Color c, String label, boolean outlined) {
		super(c, label, outlined);
	}

	/** Draw a shape on map with the fill symbol */
	public void draw(Graphics2D g, Shape shape) {
		if(isFilled()) {
			g.setColor(color);
			g.fill(shape);
		}
		if(isOutlined()) {
			outlineSymbol.draw(g, shape);
		}
	}
}
