/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2000-2005  Minnesota Department of Transportation
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

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import us.mn.state.dot.map.event.MapMouseMode;
import us.mn.state.dot.map.event.PanMouseMode;
import us.mn.state.dot.map.event.SelectMouseMode;
import us.mn.state.dot.map.event.ZoomMouseMode;

/**
 * ToolBar that supplies Navigation buttons for map.
 *
 * @author <a href="mailto:erik.engstrom@dot.state.mn.us">Erik Engstrom</a>
 * @author Douglas Lau
 */
public class NavigationBar extends JToolBar {

	protected final MapBean map;

	private final MapMouseMode zoomMode = new ZoomMouseMode();
	private final MapMouseMode panMode = new PanMouseMode();
	private final MapMouseMode selectMode = new SelectMouseMode();

	/** Create a new NavigationBar */
	public NavigationBar(MapBean m) {
		super();
		map = m;
		putClientProperty("JToolBar.isRollover", Boolean.TRUE);
		ButtonGroup bgToolbar = new ButtonGroup();
		addSeparator();
		addButton(getSelectButton(bgToolbar));
		addButton(getZoomButton(bgToolbar));
		addButton(getPanButton(bgToolbar));
		addSeparator();
	}

	protected ImageIcon getImage(String path) {
		URL url = this.getClass().getResource( path );
		ImageIcon img = new ImageIcon( url );
		return img;
	}

	protected JToggleButton getSelectButton(ButtonGroup bgToolbar) {
		JToggleButton b = new JToggleButton("Select",
			getImage("/images/arrow.png"));
		b.setSelected(true);
		b.setToolTipText("Select Map Item");
		b.addActionListener(new ActionListener() {
			public void actionPerformed( ActionEvent e ) {
				map.setMouseMode( selectMode );
			}
		});
		bgToolbar.add(b);
		return b;
	}

	protected JToggleButton getZoomButton(ButtonGroup bgToolbar) {
		JToggleButton b = new JToggleButton("Zoom",
			getImage("/images/zoom.png"));
		b.setToolTipText("Zoom Map");
		b.addActionListener(new ActionListener() {
			public void actionPerformed( ActionEvent e ) {
				map.setMouseMode( zoomMode );
			}
		});
		bgToolbar.add(b);
		return b;
	}

	protected JToggleButton getPanButton(ButtonGroup bgToolbar) {
		JToggleButton b = new JToggleButton("Pan",
			getImage("/images/pan.png"));
		b.setToolTipText("Pan Map");
		b.addActionListener(new ActionListener() {
			public void actionPerformed( ActionEvent e ) {
				map.setMouseMode( panMode );
			}
		});
		bgToolbar.add(b);
		return b;
	}

	public JButton createHomeButton() {
		JButton b = new JButton("Home", getImage("/images/globe.png"));
		b.setToolTipText("Set view to home extent");
		b.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				map.home();
			}
		});
		return b;
	}

	/** Add a button to the toolbar */
	public void addButton(AbstractButton b) {
		add(b);
	}
}
