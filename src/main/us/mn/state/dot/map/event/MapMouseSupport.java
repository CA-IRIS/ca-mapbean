/* **********************************************************************
 * 
 *  BBNT Solutions LLC, A part of GTE
 *  10 Moulton St.
 *  Cambridge, MA 02138
 *  (617) 873-2000
 * 
 *  Copyright (C) 1998, 2000
 *  This software is subject to copyright protection under the laws of 
 *  the United States and other countries.
 * 
 * **********************************************************************
 */

package us.mn.state.dot.shape.event;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This is a utility class that can be used by beans that need support
 * for handling MapMouseListeners and firing MapMouseEvents.  You can
 * use an instance of this class as a member field of your bean and
 * delegate work to it.
 * <p>
 * You can set the behavior of how MouseEvents are propagated by
 * setting whether to "consume" events.  If the MouseMode is consuming
 * events, then the event is not propagated further than the first
 * listener to successfully process it.  Otherwise the event is
 * propagated to all listeners.  The default is to consume events.
 */
public class MapMouseSupport implements java.io.Serializable {

    /**
     * The flag that dictates whether the events should be passed to
     * all the listeners or just limited to the first listener that
     * can deal with it. The default value is set to true, which means
     * the event will be consumed by the first layer that can handle
     * it. 
     */
    protected boolean consumeEvents = true;

    transient private List listeners = null;
	
    /**
     * The priority MapMouseListener will be guaranteed to receive
     * events that go hand in hand (pressed - released, etc.).
     */
    protected MapMouseListener priorityListener = null;

    /**
     * Used to determine whether a release should reset the
     * priorityListener on a mouse release.
     */
    protected boolean clickHappened = false;

    /**
     * Construct a default MapMouseSupport.
     * The default value of consumeEvents is set to true.
     */
    public MapMouseSupport () {
		this( true );
    }

    /**
     * Construct a MapMouseSupport.
     * @param shouldConsumeEvents if true, events are propagated to
     * the first MapMouseListener that successfully processes the
     * event, if false, events are propagated to all MapMouseListeners
     */
    public MapMouseSupport( boolean shouldConsumeEvents ) {
		consumeEvents = shouldConsumeEvents;
    }

    /**
     * Sets how the mouse support passes out events.  If the value
     * passed in is true, the mouse support will only pass the event
     * to the first listener that can respond to the event.  If false,
     * the mouse support will pass the event on to all its listeners.
     * @param shouldConsumeEvents true for limited distribution.
     */
    public void setConsumeEvents( boolean shouldConsumeEvents ) {
		consumeEvents = shouldConsumeEvents;
    }

   /**
    * Returns how the mouse support is set up to distribute events.
    * @return true if only one listner gets to act on an event.
    */
    public boolean isConsumeEvents () {
		return consumeEvents;
    }

    /**
     * Add a MapMouseListener to the listener list.
     * @param listener The MapMouseListener to be added
     */
    public synchronized void addMapMouseListener(
				MapMouseListener listener ) {
		if ( listeners == null ) {
	    	listeners = new java.util.Vector();
		}
		listeners.add( listener );
    }

    /**
     * Remove a MapMouseListener from the listener list.
     * @param listener The MapMouseListener to be removed
     */
    public synchronized void removeMapMouseListener(
				MapMouseListener listener ) {
		if ( listeners == null ) {
			return;
		}
		listeners.remove( listener );
    }

    /**
     * Remove all MapMouseListeners from the listener list.
     */
	public synchronized void removeAllMapMouseListeners() {
		if ( listeners == null ) {
			return;
		}
		listeners.clear();
	}

    /**
     * Get a reference to the listeners.
     */
    protected List getTargets () {
		synchronized (this) {
			if (listeners == null) {
				return null;
			}
			List result = new ArrayList();
			Collections.copy( result, listeners );
			return result;
		}
    }

    /**
     * Handle a mousePressed MouseListener event.
     * @param evt MouseEvent to be handled
     */
    public boolean fireMapMousePressed (java.awt.event.MouseEvent evt) {
		List targets = getTargets();
		if (targets == null) return false;

		for (int i = 0; i < targets.size(); i++) {
			MapMouseListener target = 
			(MapMouseListener)targets.get(i);
			if (target.mousePressed(evt) && consumeEvents) {
			priorityListener = target;
			return true;
			}
		}
		return false;
    }

    /**
     * Handle a mouseReleased MouseListener event.  Checks to see if
     * there is a priorityListener, and will direct the event to that
     * listener.  The priorityListener variable will be reset to null.
     * If there is not a priorityListener, the event is passed through
     * the listeners, subject to the consumeEvents mode.
     * @param evt MouseEvent to be handled.
     */
    public boolean fireMapMouseReleased (java.awt.event.MouseEvent evt) {
		if (priorityListener != null) {
			priorityListener.mouseReleased(evt);
			if ( !clickHappened )
				priorityListener = null;
			return true;
		}

		List targets = getTargets();
		if (targets == null) return false;

		for (int i = 0; i < targets.size(); i++) {
			MapMouseListener target = 
			(MapMouseListener)targets.get(i);
			if (target.mouseReleased(evt) && consumeEvents) {
			return true;
			}
		}
		return false;
    }

    /**
     * Handle a mouseClicked MouseListener event.  If the
     * priorityListener is set, it automatically gets the clicked
     * event.  If it is not set, the other listeners get a shot at the
     * event according to the consumeEvent mode.
     * @param evt MouseEvent to be handled.
     */
    public boolean fireMapMouseClicked (java.awt.event.MouseEvent evt) {
		clickHappened = true;
		if (priorityListener != null && evt.getClickCount() > 1){
			priorityListener.mouseClicked(evt);
			return true;
		}

		priorityListener = null;

		List targets = getTargets();
		if (targets == null) return false;

		for (int i = 0; i < targets.size(); i++) {
			MapMouseListener target = 
			(MapMouseListener)targets.get(i);
			if (target.mouseClicked(evt) && consumeEvents) {
			priorityListener = target;
			return true;
			}
		}
		return false;
    }

    /**
     * Handle a mouseEntered MouseListener event.
     * @param evt MouseEvent to be handled
     */
    public boolean fireMapMouseEntered (java.awt.event.MouseEvent evt) {
		List targets = getTargets();
		if (targets == null) return false;

		for (int i = 0; i < targets.size(); i++) {
			MapMouseListener target = 
			(MapMouseListener)targets.get(i);
			target.mouseEntered(evt);
		}
		return false;
    }

    /**
     * Handle a mouseExited MouseListener event.
     * @param evt MouseEvent to be handled
     * @return false.
     */
    public boolean fireMapMouseExited (java.awt.event.MouseEvent evt) {

		List targets = getTargets();
		if (targets == null) return false;

		for (int i = 0; i < targets.size(); i++) {
			MapMouseListener target = 
			(MapMouseListener)targets.get(i);
			target.mouseExited(evt);
		}
		return false;
    }

    /**
     * Handle a mouseDragged MouseListener event.
     * @param evt MouseEvent to be handled
     * @return false.
     */
    public boolean fireMapMouseDragged (java.awt.event.MouseEvent evt) {
		clickHappened = false;

		if (priorityListener != null){
			priorityListener.mouseDragged(evt);
			return true;
		}

		List targets = getTargets();
		if (targets == null) return false;

		for (int i = 0; i < targets.size(); i++) {
			MapMouseListener target = 
			(MapMouseListener)targets.get(i);
			if (target.mouseDragged(evt) && consumeEvents) {
			return true;
			}
		}
		return false;
    }

    /**
     * Handle a mouseMoved MouseListener event.  If the moved event
     * is consumed, the rest of the listeners that didn't have a
     * chance to respond get called in the mouse moved method without
     * arguments.
     * @param evt MouseEvent to be handled
     * @return true if the event was consumed.
     */
    public boolean fireMapMouseMoved (java.awt.event.MouseEvent evt) {
		boolean movedConsumed = false;

		List targets = getTargets();
		if (targets == null) return false;

		for (int i = 0; i < targets.size(); i++) {
			MapMouseListener target = 
			(MapMouseListener)targets.get(i);
			if (movedConsumed) target.mouseMoved();
			else if (target.mouseMoved(evt)) {
			movedConsumed = true;
			}
		}
		return movedConsumed;
    }


    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
		List v = new ArrayList();
		synchronized (this) {
			if (listeners != null) {
				Collections.copy( v, listeners);
			}
		}
		if (v != null) {
			for(int i = 0; i < v.size(); i++) {
				MapMouseListener l = (MapMouseListener)v.get(i);
				if (l instanceof Serializable) {
					s.writeObject(l);
				}
				}
			}
			s.writeObject(null);
    }


    private void readObject(ObjectInputStream s) 
			throws ClassNotFoundException, IOException {
		s.defaultReadObject();
		Object listenerOrNull;
		while(null != (listenerOrNull = s.readObject())) {
			addMapMouseListener((MapMouseListener)listenerOrNull);
		}
    }
}
