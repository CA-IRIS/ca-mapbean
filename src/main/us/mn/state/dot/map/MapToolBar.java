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
import javax.swing.JToolBar;

/**
 * Toolbar used for MapBean.
 *
 * @author <a href="mailto:erik.engstrom@dot.state.mn.us">Erik Engstrom</a>
 * @author Douglas Lau
 */
public class MapToolBar extends NavigationBar {

	/** Create a new MapToolBar */
	public MapToolBar(MapBean m, Theme theme) {
		super(m);
		JMenuBar b = new JMenuBar();
		b.setBorderPainted(false);
		b.setAlignmentY(.5f);
		b.add(new ThemeMenu(map.getThemes()));
		LayerRenderer r = theme.getCurrentLayerRenderer();
		LegendMenu legend = new LegendMenu(r);
		JComboBox combo = createRendererCombo(theme, legend);
		if(combo != null) b.add(combo);
		b.add(legend);
		b.add(new JToolBar.Separator());
		add(b, 0);
	}

	/** Get the renderer selector combo box */
	protected JComboBox createRendererCombo(final Theme theme,
		final LegendMenu legend)
	{
		final JComboBox combo = new JComboBox();
		Iterator it = theme.getLayerRenderers().iterator();
		while(it.hasNext()) {
			combo.addItem(it.next());
		}
		if(combo.getItemCount() < 2) return null;
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
