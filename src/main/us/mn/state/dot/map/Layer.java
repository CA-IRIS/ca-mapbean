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

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;
import us.mn.state.dot.map.event.LayerChangedEvent;
import us.mn.state.dot.map.event.LayerChangedListener;

/**
 * A layer is a grouping of similar elements which are painted on the map
 *
 * @author <a href="mailto:erik.engstrom@dot.state.mn.us">Erik Engstrom</a>
 */
public interface Layer {

	/** Get the name of this layer */
	public String getName();

	/** Get the theme for the layer */
	public Theme getTheme();

	/** Get the extent of the layer */
	public Rectangle2D getExtent();

	/** Paint the layer */
	public void paint(Graphics2D g, LayerRenderer r);

	/** Register a LayerChangedListener with the layer */
	public void addLayerChangedListener(LayerChangedListener listener);

	/** Remove a LayerChangedListener from the layer */
	public void removeLayerChangedListener(LayerChangedListener listener);

	/** Notify all LayerChangedListeners that the layer has changed */
	public void notifyLayerChangedListeners(LayerChangedEvent event);

	/** Search the layer for a MapObject which is located at or near the
	 * specified point */
	public MapObject search(Point2D p, LayerRenderer renderer);
}
