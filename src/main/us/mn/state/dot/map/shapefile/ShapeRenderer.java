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
package us.mn.state.dot.shape.shapefile;

import us.mn.state.dot.shape.LayerRenderer;
import us.mn.state.dot.shape.MapObject;
import us.mn.state.dot.shape.MapTip;
import us.mn.state.dot.shape.Symbol;

/**
 * Base class for all renderers used for shape files.
 *
 * @author <a href="mailto:erik.engstrom@dot.state.mn.us">Erik Engstrom</a>
 */
abstract public class ShapeRenderer implements LayerRenderer {

	/** Name of renderer */
	protected final String name;

	/** MapTip to display */
	protected MapTip mapTip = null;

	/** Create a new shape renderer */
	public ShapeRenderer(String n) {
		name = n;
	}

	/** Set the MapTip used by this renderer */
	public void setTip(MapTip m) {
		mapTip = m;
	}

	/** Get the tip for the given map object */
	public String getTip(MapObject object) {
		String result = null;
		if(mapTip != null) {
			result = mapTip.getTip(object);
		}
		return result;
	}

	/** Overrides Object.toString() */
	public String toString() {
		if(name == null) return super.toString();
		else return name;
	}
}
