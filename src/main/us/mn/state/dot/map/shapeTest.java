
//Title:        shapeTester
//Version:      
//Copyright:    Copyright (c) 1999
//Author:       Erik Engstrom
//Company:      MnDOT
//Description:  tests shape pane map with new panning technonolgy

package us.mn.state.dot.shape;
import us.mn.state.dot.shape.*;
import java.awt.*;
import javax.swing.*;

public class shapeTest extends JFrame {
  ShapePane map = new ShapePane();

  public shapeTest() {
    try  {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
    shapeTest shapeTest1 = new shapeTest();
    shapeTest1.setSize(500,500);
    shapeTest1.show();
  }

  private void jbInit() throws Exception {

    Layer layer1 = new Layer("c:\\gpoly\\gpoly.shp");
    layer1.setColor(Color.green);
    //layer1.setPainter(painter);
    Layer layer3 = new Layer("c:\\gpoly\\stateart.shp");
    layer3.setColor(Color.black);
    Layer layer2 = new Layer("c:\\gpoly\\water.shp");
    layer2.setColor(Color.blue);
    Layer layer4 = new Layer("c:\\gpoly\\met-csah.shp");
    layer4.setColor(Color.black);
    Layer layer5 = new Layer("c:\\gpoly\\met-cr.shp");
    layer5.setColor(Color.black);
    //layer2.setPainter(painter);
//    map = new ShapePane(layer1);
    map.addLayer(layer1);
    map.addLayer(layer3);
    map.addLayer(layer4);
    map.addLayer(layer5);
    map.addLayer(layer2);
    this.getContentPane().add(map, BorderLayout.CENTER);
  }
}