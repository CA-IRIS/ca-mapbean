
//Title:        SignClient
//Version:
//Copyright:    Copyright (c) 1999
//Author:       Erik Engstrom
//Company:      MnDOT
//Description:  Your description
package us.mn.state.dot.shape;

import javax.swing.*;
import java.awt.*;

public class LegendCellRenderer implements ListCellRenderer {

	public Component getListCellRendererComponent(JList list, Object value,
			 int index, boolean isSelected, boolean cellHasFocus) {
		JLabel label = new JLabel();
		Symbol symbol = (Symbol) value;
		if ((symbol.getLabel() != null) && (! symbol.getLabel().equals(""))) {
			label.setText(symbol.getLabel());
			ColorIcon icon = new ColorIcon(symbol.getColor());
			label.setIcon(icon);
		}
		return label;
	}

	public Component getRenderedCell(Object value){
		return getListCellRendererComponent(null, value, 0, false, false);
	}

	class ColorIcon implements Icon {
		private Color color;
		private int w, h;

		public ColorIcon(){
			this(Color.gray, 25, 15);
		}

		public ColorIcon(Color color){
			this(color, 25, 15);
		}

		public ColorIcon(Color color, int w, int h){
			this.color = color;
			this.w = w;
			this.h = h;
		}

		public void paintIcon(Component c, Graphics g, int x, int y) {
			g.setColor(Color.black);
			g.drawRect(x, y, w - 1, h - 1);
			g.setColor(color);
			g.fillRect(x + 1, y + 1, w - 2, h - 2);
		}
		public Color getColor() {
			return color;
		}

		public void setColor(Color color){
			this.color = color;
		}

		public int getIconWidth() {
			return w;
		}

		public int getIconHeight(){
			return h;
		}
	}



} 