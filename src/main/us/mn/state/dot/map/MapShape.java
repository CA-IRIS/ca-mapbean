
//Title:        Your Product Name
//Version:
//Copyright:    Copyright (c) 1999
//Author:       Erik Engstrom
//Company:      MnDOT
//Description:  Your description
package us.mn.state.dot.shape;

import java.awt.geom.*;

public class MapShape {
 private GeneralPath path;
 private Object [] fieldValues;


 public GeneralPath getPath(){
 	return path;
 }
 
	public MapShape(GeneralPath p, Object [] values) {
 	path = p;
   fieldValues = values;
	}

	public static void main(String[] args) {
	}
}
