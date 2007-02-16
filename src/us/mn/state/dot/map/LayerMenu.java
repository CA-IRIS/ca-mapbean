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
package us.mn.state.dot.map;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;

/**
 * Menu for displaying a list of layers.
 *
 * @author Erik Engstrom
 * @author Douglas Lau
 */
public class LayerMenu extends JMenu {

  	/** Create a new layer menu */
  	public LayerMenu(List<LayerState> layers) {
		super("Layers");
		for(LayerState s: layers)
			add(new LayerMenuItem(s));
  	}

	/** MenuItem for displaying a layer.  When a check box is selected the
	 * layer is made visible.  If it is deselected then the layer is made
	 * invisible. */
	protected class LayerMenuItem extends JCheckBoxMenuItem {

		/** Layer state */
		protected final LayerState lstate;

		/** Create a new layer menu item */
		public LayerMenuItem(LayerState s) {
			super(s.getLayer().getName());
			lstate = s;
			setSelected(lstate.isVisible());
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					lstate.setVisible(!lstate.isVisible());
				}
			});
		}
	}
}
