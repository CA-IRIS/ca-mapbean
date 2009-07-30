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

import java.awt.BorderLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.TreeMap;
import javax.swing.AbstractButton;
import javax.swing.Box;
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

	/** Button mapping */
	protected final TreeMap<String, AbstractButton> buttons =
		new TreeMap<String, AbstractButton>();

	/** Button box */
	protected final Box b_box = Box.createHorizontalBox();

	/** Create a new navigation bar */
	public NavigationBar(MapBean m) {
		setLayout(new BorderLayout());
		map = m;
		add(b_box, BorderLayout.LINE_END);
		addZoomButtons();
	}

	/** Add the zoom buttons */
	protected void addZoomButtons() {
		JButton b = new JButton(" + ");
		b.setToolTipText("Zoom map view in");
		b.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				map.zoom(true);
			}
		});
		addButton(b);
		b = new JButton(" - ");
		b.setToolTipText("Zoom map view out");
		b.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				map.zoom(false);
			}
		});
		addButton(b);
	}

	/** Add a "home" button */
	public void addHomeButton() {
		JButton b = new JButton("Home", getImage("/images/globe.png"));
		b.setToolTipText("Set view to home extent");
		b.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				map.home();
			}
		});
		addButton(b);
	}

	/** Add a button to the toolbar */
	public void addButton(AbstractButton b) {
		b.setMargin(new Insets(1, 1, 1, 1));
		buttons.put(b.getText(), b);
		rebuildButtonBox();
	}

	/** Remove a button from the toolbar */
	public void removeButton(AbstractButton b) {
		buttons.remove(b.getText());
		rebuildButtonBox();
	}

	/** Rebuild the button box */
	protected void rebuildButtonBox() {
		b_box.removeAll();
		for(AbstractButton b: buttons.values()) {
			b_box.add(Box.createHorizontalStrut(4));
			b_box.add(b);
		}
	}
}
