package us.mn.state.dot.map.event;

import java.util.EventObject;

/**
 * LayerChangedEvents are sent to LayerChangedListeners when a Layers data is
 * changed.
 *
 * @author <a href="mailto:erik.engstrom@dot.state.mn.us">Erik Engstrom</a>
 */
public class LayerChangedEvent extends EventObject {

	/** Reason code for geography change */
	static public final int GEOGRAPHY = 1 << 0;

	/** Reason code for data change */
	static public final int DATA = 1 << 1;

	/** Reason code for animation change */
	static public final int ANIMATION = 1 << 4;

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
