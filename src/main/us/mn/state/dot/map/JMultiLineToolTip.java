
//Title:        SignClient
//Version:
//Copyright:    Copyright (c) 1999
//Author:       Erik Engstrom
//Company:      MnDOT
//Description:  Stolen from http://www.codeguru.com/java/articles/122.shtml

package us.mn.state.dot.shape;

// JMultiLineToolTip.java
import javax.swing.*;

/**
 * @author Zafir Anjum
 */


public class JMultiLineToolTip extends JToolTip {

    private static final String uiClassID = "ToolTipUI";

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

