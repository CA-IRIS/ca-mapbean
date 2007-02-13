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
package us.mn.state.dot.map.event;

import java.util.EventListener;

/**
 * A LayerChangedListener can be registered with a Layer to recieve
 * notifications when the layer's data changes.
 *
 * @author Erik Engstrom
 */
public interface LayerChangedListener extends EventListener {

	/** Called by the Layer when the layers data is changed */
	public void layerChanged(LayerChangedEvent event);
}
