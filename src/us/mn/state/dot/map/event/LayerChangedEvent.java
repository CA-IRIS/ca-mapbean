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

import java.util.EventObject;

/**
 * LayerChangedEvents are sent to LayerChangedListeners when a Layers data is
 * changed.
 *
 * @author Erik Engstrom
 */
public class LayerChangedEvent extends EventObject {

	/** Reason code for geography change */
	static public final int GEOGRAPHY = 1 << 0;

	/** Reason code for data change */
	static public final int DATA = 1 << 1;

	/** Reason code for shade change */
	static public final int SHADE = 1 << 2;

	/** Reason code for highlight change */
	static public final int HIGHLIGHT = 1 << 3;

	/** Reason code for animation change */
	static public final int ANIMATION = 1 << 4;

	/** Reason code for selection change */
	static public final int SELECTION = 1 << 5;

	/** Reason code for layer change */
	protected int reason;

	/** Create a new LayerChangedEvent */
	public LayerChangedEvent(Object source, int why) {
		super(source);
		reason = why;
	}

	/** Get the reason the Layer changed */
	public int getReason() {
		return reason;
	}
}
