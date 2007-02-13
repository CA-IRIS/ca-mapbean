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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.List;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;

/**
 * Menu for displaying a list of themes.
 *
 * @author <a href="mailto:erik.engstrom@dot.state.mn.us">Erik Engstrom</a>
 * @author Douglas Lau
 */
public class ThemeMenu extends JMenu {

  	/** Create a new theme menu */
  	public ThemeMenu(List themes) {
		super("Themes");
		Iterator it = themes.iterator();
		while(it.hasNext())
			add(new ThemeMenuItem((Theme)it.next()));
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
