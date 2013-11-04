/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2000-2013  Minnesota Department of Transportation
 * Copyright (C) 2010  AHMCT, University of California
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
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.TreeMap;
import javax.swing.AbstractButton;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JToolBar;

/**
 * ToolBar that supplies Navigation buttons for map.
 *
 * @author Douglas Lau
 * @author Michael Darter
 * @author Erik Engstrom
 */
public class NavigationBar extends JToolBar {

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
		add(b_box);
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

	/** Add a button to the toolbar */
	public void addButton(AbstractButton b) {
		b.setMinimumSize(new Dimension(12, 20));
		b.setMargin(new Insets(1, 1, 1, 1));
		buttons.put(b.getText(), b);
		rebuildButtonBox();
	}

	/** Clear the buttons */
	public void clear() {
		buttons.clear();
		addZoomButtons();
		revalidate();
		repaint();
	}

	/** Rebuild the button box */
	protected void rebuildButtonBox() {
		b_box.removeAll();
		// horizontal space between legend and extent buttons
		b_box.add(createFiller(0, 20, Short.MAX_VALUE));
		for(AbstractButton b: buttons.values()) {
			// horizontal space between buttons
			b_box.add(createFiller(0, 4, 4));
			b_box.add(b);
		}
	}

	/** Create horizontal filler space with the specified widths. */
	private static Box.Filler createFiller(int min, int pref, int max) {
		Dimension minDim = new Dimension(min, 10);
		Dimension prefDim = new Dimension(pref, 10);
		Dimension maxDim = new Dimension(max, 10);
		return new Box.Filler(minDim, prefDim, maxDim);
	}
}
