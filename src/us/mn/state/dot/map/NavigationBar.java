/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2000-2009  Minnesota Department of Transportation
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToolBar;

/**
 * ToolBar that supplies Navigation buttons for map.
 *
 * @author Erik Engstrom
 * @author Douglas Lau
 */
public class NavigationBar extends JToolBar {

	/** Lookup an image resource */
	static protected ImageIcon getImage(String path) {
		URL url = NavigationBar.class.getResource(path);
		return new ImageIcon(url);
	}

	/** Map associated with the navigation bar */
	protected final MapBean map;

	/** Create a new navigation bar */
	public NavigationBar(MapBean m) {
		super();
		map = m;
		putClientProperty("JToolBar.isRollover", Boolean.TRUE);
		addSeparator();
	}

	/** Create a "home" button */
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
