
//Title:        SignClient
//Version:
//Copyright:    Copyright (c) 1999
//Author:       Erik Engstrom
//Company:      MnDOT
//Description:  Your description

package us.mn.state.dot.shape;

import javax.swing.*;
import java.awt.*;

public class MapLegend extends JPanel {

	public MapLegend(ClassBreaksRenderer renderer) {
		setRenderer(renderer);
	}

	public void setRenderer(ClassBreaksRenderer renderer){
		this.removeAll();
		for (int i = 0; i < renderer.getBreakCount(); i++){
			ColorIcon icon = new ColorIcon(renderer.getSymbol(i).getColor());
			String text = renderer.getLabel(i);
			if (text == null) {
				text = new Double(renderer.getBreak(i)).toString();
			}
			if (! text.equals("")){
				JLabel label = new JLabel(text, icon, SwingConstants.RIGHT);
				add(label);
			}
		}
		ColorIcon icon = new ColorIcon(renderer.getSymbol(
			renderer.getBreakCount()).getColor());
		String text = renderer.getLabel(renderer.getBreakCount());
		if (text == null) {
			text = "> " + new Double(renderer.getBreak(renderer.getBreakCount()
				- 1)).toString();
		}
		if (! text.equals("")){
			JLabel label = new JLabel(text, icon, SwingConstants.RIGHT);
			add(label);
		}
		revalidate();
		repaint();
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