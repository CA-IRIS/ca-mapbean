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
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import us.mn.state.dot.map.MapObject;
import us.mn.state.dot.map.Outline;

/**
 * A pen symbol can stroke/fill any arbitrary map objects
 *
 * @author Douglas Lau
 */
public class PenSymbol extends AbstractSymbol {

	/** Create a new pen symbol with given label, outline and fill color */
	public PenSymbol(String l, Outline o, Color f) {
		super(l, o, f);
	}

	/** Draw a shape on a graphics context */
	protected void doDraw(Graphics2D g, MapObject o) {
		Shape s = o.getShape();
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

	/** Draw a shape on map with the pen symbol */
	public void draw(Graphics2D g, MapObject o) {
		AffineTransform ot = o.getTransform();
		if(ot != null) {
			AffineTransform gt = g.getTransform();
			g.transform(ot);
			doDraw(g, o);
			g.setTransform(gt);
		} else
			doDraw(g, o);
	}
}
