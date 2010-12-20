/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2000-2010  Minnesota Department of Transportation
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

import java.util.EventObject;

/**
 * LayerChangedEvents are sent to LayerChangedListeners when a Layers data is
 * changed.
 *
 * @author Erik Engstrom
 * @author Douglas Lau
 */
public class LayerChangedEvent extends EventObject {

	/** Reason code for layer change */
	protected LayerChange reason;

	/** Create a new LayerChangedEvent */
	public LayerChangedEvent(Object source, LayerChange why) {
		super(source);
		reason = why;
	}

	/** Get the reason the Layer changed */
	public LayerChange getReason() {
		return reason;
	}
}
