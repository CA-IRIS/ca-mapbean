
//Title:        Renderer
//Version:      1.0
//Copyright:    Copyright (c) 1999
//Author:       Erik Engstrom
//Company:      MnDOT
//Description:  Interface all layer renderers must implement.

package us.mn.state.dot.shape;

import java.awt.*;
import java.awt.geom.*;

public interface Renderer {

    public void paint( Graphics2D g, GeneralPath path, int index);
    public void setField(Field f);
    public Field getField();

}