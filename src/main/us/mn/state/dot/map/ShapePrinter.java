
//Title:        SignClient
//Version:      
//Copyright:    Copyright (c) 1999
//Author:       Erik Engstrom
//Company:      MnDOT
//Description:  Your description
package us.mn.state.dot.shape;

import java.io.*;

public class ShapePrinter {

	public ShapePrinter() {
		try{
			ShapeLayer cameraLayer = new ShapeLayer("C:\\gpoly\\tvp", "cameras");
			FileWriter fw = new FileWriter("C:\\CameraShape.txt");
			boolean result = cameraLayer.writeToFile(fw);
			fw.flush();
			fw.close();
			System.out.println(result);
		} catch (IOException ex){
			ex.printStackTrace();
		}

	}

	public static void main(String[] args) {
		ShapePrinter shapePrinter = new ShapePrinter();
		shapePrinter.invokedStandalone = true;
	}
	private boolean invokedStandalone = false;
} 