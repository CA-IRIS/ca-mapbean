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
package us.mn.state.dot.map.event;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;
import us.mn.state.dot.map.MapBean;

/**
 *
 * @author <a href="mailto:erik.engstrom@dot.state.mn.us">Erik Engstrom</a>
 */
public class ZoomMouseMode extends MouseModeAdapter {

	/**
	 * Mouse Mode identifier, which is "Zoom".
	 * This is returned on getID()
	 */
	static public final transient String modeID = "Zoom".intern();

	/** Are we drawing a selectionbox? */
	private boolean selecting = false;

	/** The start of a mouse drag */
	private Point startPoint = null;

	/** The last selection rectangle to be drawn */
	private Rectangle selectionArea = new Rectangle();

	/**
	 * Creates new ZoomMouseMode
	 * Default constructor.  Sets the ID to the modeID, and the
	 * consume mode to true.
	 */
	public ZoomMouseMode() {
		this(true);
	}

	/**
	 * Construct a ZoomMouseMode.
	 * The constructor that lets you set the consume mode.
	 * @param consumeEvents the consume mode setting.
	 */
	public ZoomMouseMode(boolean consumeEvents) {
		super(modeID, consumeEvents);
		URL url = getClass().getResource("/images/zoomMod.gif");
		ImageIcon img = new ImageIcon(url);
		cursor = Toolkit.getDefaultToolkit().createCustomCursor(
			img.getImage(), new Point(0, 0), "Zoom");
	}

	public void mouseClicked(MouseEvent e) {
		if(SwingUtilities.isRightMouseButton(e)) {
			MapBean map = (MapBean)e.getSource();
			map.zoomOut(e.getPoint());
		}
		mouseSupport.fireMapMouseClicked(e);
	}

	public void mousePressed(MouseEvent e) {
		startPoint = e.getPoint();
		selecting = false;
		mouseSupport.fireMapMousePressed(e);
	}

	public void mouseReleased(MouseEvent e) {
		if(selecting) {
			MapBean map = (MapBean)e.getSource();
			drawBox(map, selectionArea);
			selecting = false;
			map.zoom(selectionArea);
		}
		mouseSupport.fireMapMouseReleased(e);
	}

	/** Draw an XOR box (rubberbanding box) */
	protected void drawBox(MapBean map, Rectangle r) {
		Graphics g = map.getGraphics();
		if(g == null) {
			return;
		}
		g.setXORMode(Color.white);
		g.drawRect(r.x, r.y, r.width, r.height);
	}

	public void mouseEntered(MouseEvent e) {
		mouseSupport.fireMapMouseEntered(e);
	}

	public void mouseExited(MouseEvent e) {
		mouseSupport.fireMapMouseExited(e);
	}

	public void mouseDragged(MouseEvent e) {
		if(SwingUtilities.isLeftMouseButton(e)) {
			MapBean map = (MapBean)e.getSource();
			Point endPoint = e.getPoint();
			if(selecting) {
				drawBox(map, selectionArea);
			}
			selectionArea.x = (int)Math.min(startPoint.getX(),
				endPoint.getX());
			selectionArea.width = (int)Math.abs(endPoint.getX() -
				startPoint.getX());
			selectionArea.y = (int)Math.min(startPoint.getY(),
				endPoint.getY());
			selectionArea.height = (int)Math.abs(endPoint.getY() -
				startPoint.getY());
			drawBox(map, selectionArea);
			selecting = true;
		}
		mouseSupport.fireMapMouseDragged(e);
	}

	public void mouseMoved(MouseEvent e) {
		if(selecting) {
			selecting = false;
		}
		mouseSupport.fireMapMouseMoved(e);
	}
}
