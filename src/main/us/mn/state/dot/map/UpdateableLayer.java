
//Title:        SignClient
//Version:      
//Copyright:    Copyright (c) 1999
//Author:       Erik Engstrom
//Company:      MnDOT
//Description:  Your description

package us.mn.state.dot.shape;

public interface UpdateableLayer {

    public void addLayerListener(LayerListener l);

    public void updateLayer(Layer l);
    
} 