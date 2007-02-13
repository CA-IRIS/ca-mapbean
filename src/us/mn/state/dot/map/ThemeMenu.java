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
 * Menu for displaying a list of themes.
 *
 * @author Erik Engstrom
 * @author Douglas Lau
 */
public class ThemeMenu extends JMenu {

  	/** Create a new theme menu */
  	public ThemeMenu(List<Theme> themes) {
		super("Themes");
		for(Theme t: themes)
			add(new ThemeMenuItem(t));
  	}

	/** MenuItem for displaying a theme.  When a check box is selected the
	 * Theme is made visible.  If it is deselected then the Theme is made
	 * invisible. */
	protected class ThemeMenuItem extends JCheckBoxMenuItem {

		protected final Theme theme;

		public ThemeMenuItem(Theme t) {
			super(t.getLayer().getName());
			theme = t;
			setSelected(theme.isVisible());
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					theme.setVisible(!theme.isVisible());
				}
			});
		}
	}
}
