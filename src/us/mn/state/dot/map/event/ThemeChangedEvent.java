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
 * Theme changed events are sent to ThemeChangedListeners whenever a themes data
 * is changed.
 *
 * @author Erik Engstrom
 */
public class ThemeChangedEvent extends EventObject {

	/** Reason code for a geography theme change event */
	static public final int GEOGRAPHY = 1 << 0;

	/** Reason code for a data theme change event */
	static public final int DATA = 1 << 1;

	/** Reason code for a shade theme change event */
	static public final int SHADE = 1 << 2;

	/** Reason code for a highlight theme change event */
	static public final int HIGHLIGHT = 1 << 3;

	/** Reason code for an animation theme change event */
	static public final int ANIMATION = 1 << 4;

	/** Reason code for a selection theme change event */
	static public final int SELECTION = 1 << 5;

	/** Reason code for theme change */
	public final int reason;

	/** Create a new ThemeChangedEvent */
	public ThemeChangedEvent(Object source, int why) {
		super(source);
		reason = why;
	}

	/** Get the reason the Theme changed */
	public int getReason() {
		return reason;
	}
}
