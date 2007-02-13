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
import javax.swing.JComboBox;
import javax.swing.JMenuBar;
import javax.swing.JMenu;

/**
 * Toolbar used for MapBean.
 *
 * @author <a href="mailto:erik.engstrom@dot.state.mn.us">Erik Engstrom</a>
 * @author Douglas Lau
 */
public class MapToolBar extends NavigationBar {

	/** Menu bar */
	protected final JMenuBar menu = new JMenuBar();

	/** Legend menu */
	protected final JMenu legend = new JMenu("Legend");

	/** Create a new MapToolBar */
	public MapToolBar(MapBean m) {
		super(m);
		menu.setBorderPainted(false);
		menu.setAlignmentY(.5f);
		menu.add(new ThemeMenu(map.getThemes()));
		menu.add(legend);
		add(menu, 0);
	}

	/** Add a theme legend to the tool bar */
	public void addThemeLegend(Theme theme) {
		String name = theme.getLayer().getName();
		LayerRenderer r = theme.getCurrentLayerRenderer();
		LegendMenu l = new LegendMenu(name, r);
		JComboBox combo = createRendererCombo(theme, l);
		if(combo != null) {
			menu.add(combo);
			legend.add(l);
		} else if(l.getItemCount() > 1)
			legend.add(l);
	}

	/** Get the renderer selector combo box */
	protected JComboBox createRendererCombo(final Theme theme,
		final LegendMenu legend)
	{
		final JComboBox combo = new JComboBox();
		Iterator it = theme.getLayerRenderers().iterator();
		while(it.hasNext())
			combo.addItem(it.next());
		if(combo.getItemCount() < 2)
			return null;
		combo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				LayerRenderer r =
					(LayerRenderer)combo.getSelectedItem();
				theme.setCurrentLayerRenderer(r);
				legend.setMapRenderer(r);
			}
		});
		return combo;
	}
}
