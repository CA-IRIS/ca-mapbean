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
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import us.mn.state.dot.map.MapObject;

/**
 * Symbol object used to render point shapes on map.
 *
 * @author <a href="mailto:erik.engstrom@dot.state.mn.us">Erik Engstrom</a>
 */
public abstract class PointSymbol extends AbstractSymbol {

	public PointSymbol(int size, Color c, String label, boolean outlined) {
		super(size, c, label, outlined);
	}

	abstract protected Shape getShape( double x, double y );

	/** Draw symbol on map */
	public void draw(Graphics2D g, Shape shape) {
		Rectangle2D pt = shape.getBounds();
		Shape s = getShape(pt.getX(), pt.getY());
		if(isFilled()) {
			g.setColor(color);
			g.fill(s);
		}
		if(isOutlined()) {
			outlineSymbol.draw(g, s);
		}
	}

	public Shape getShape(MapObject object) {
		Rectangle2D bounds = object.getShape().getBounds();
		Shape renderedShape = getShape(bounds.getX(), bounds.getY());
		GeneralPath path = new GeneralPath();
		if(isOutlined()) {
			path.append(outlineSymbol.getStroke().createStrokedShape(
				renderedShape), true);
		}
		path.append(renderedShape, true);
		return path;
	}

	public Rectangle2D getBounds(MapObject object) {
		return getShape(object).getBounds();
	}
}
