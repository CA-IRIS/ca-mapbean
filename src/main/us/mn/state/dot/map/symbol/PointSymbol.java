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
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import us.mn.state.dot.map.MapObject;

/**
 * Symbol object used to render point shapes on map.
 *
 * @author <a href="mailto:erik.engstrom@dot.state.mn.us">Erik Engstrom</a>
 * @author Douglas Lau
 */
public class PointSymbol extends AbstractSymbol {

	/** Create a new point symbol */
	public PointSymbol() {
		this(Color.BLACK);
	}

	/** Create a new point symbol */
	public PointSymbol(Color c) {
		this("", c);
	}

	/** Create a new point symbol */
	public PointSymbol(String l, Color c) {
		this(l, c, null);
	}

	/** Create a new point symbol */
	public PointSymbol(String l, Color c, Color o) {
		super(l, c, o);
	}

	/** Draw symbol on map */
	public void draw(Graphics2D g, MapObject o) {
		AffineTransform t = g.getTransform();
		g.transform(o.getTransform());
		Shape s = o.getShape();
		if(color != null) {
			g.setColor(color);
			g.fill(s);
		}
		if(getOutlineColor() != null) {
			outline.draw(g, o);
		}
		g.setTransform(t);
	}
}
