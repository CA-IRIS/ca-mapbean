/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2000  Minnesota Department of Transportation
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

//Description:  Stolen from http://www.codeguru.com/java/articles/122.shtml

package us.mn.state.dot.shape;

import javax.swing.JComponent;
import javax.swing.JToolTip;

/**
 * A ToolTip that allows multiple lines.
 *
 * @author Zafir Anjum
 * @author <a href="mailto:erik.engstrom@dot.state.mn.us">Erik Engstrom</a>
 * @version $Revision: 1.7 $ $Date: 2003/05/19 21:23:41 $ 
 */
public final class JMultiLineToolTip extends JToolTip {

    String tipText;
    JComponent component;

    public JMultiLineToolTip() {
        updateUI();
    }

    public void updateUI() {
        setUI(MultiLineToolTipUI.createUI(this));
    }

    public void setColumns(int columns) {
    	this.columns = columns;
    	this.fixedwidth = 0;
    }

    public int getColumns() {
    	return columns;
    }

    public void setFixedWidth(int width){
    	this.fixedwidth = width;
    	this.columns = 0;
    }

    public int getFixedWidth() {
    	return fixedwidth;
    }

    protected int columns = 0;
    protected int fixedwidth = 0;
}

