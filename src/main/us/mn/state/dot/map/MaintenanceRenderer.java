
//Title:        SignClient
//Version:
//Copyright:    Copyright (c) 1999
//Author:       Erik Engstrom
//Company:      MnDOT
//Description:  Your description
package us.mn.state.dot.shape;

import java.awt.*;

public final class MaintenanceRenderer extends ClassBreaksRenderer {

	public MaintenanceRenderer( NumericField field ) {
		super( field, 17, "Maintenance" );
		for ( int i = 0; i < 17; i++ ) {
			setBreak( i, i + 1 );
		}
		setSymbol( 0, new FillSymbol( Color.gray, "1" ) );
		setSymbol( 1, new FillSymbol( Color.red, "2" ) );
		setSymbol( 2, new FillSymbol( Color.green, "3" ) );
		setSymbol( 3, new FillSymbol( Color.blue, "4" ) );
		setSymbol( 4, new FillSymbol( Color.black, "5" ) );
		setSymbol( 5, new FillSymbol( Color.yellow, "6" ) );
		setSymbol( 6, new FillSymbol( Color.orange, "7" ) );
		setSymbol( 7, new FillSymbol( Color.cyan, "8" ) );
		setSymbol( 8, new FillSymbol( Color.magenta, "9" ) );
		setSymbol( 9, new FillSymbol( new Color( 153, 153, 51 ), "10" ) );
		setSymbol( 10, new FillSymbol( Color.darkGray, "11" ) );
		setSymbol( 11, new FillSymbol( Color.pink, "12" ) );
		setSymbol( 12, new FillSymbol( new Color( 255, 255, 191 ), "13" ) );
		setSymbol( 13, new FillSymbol( new Color( 0, 0, 128 ), "14" ) );
		setSymbol( 14, new FillSymbol( new Color( 151, 112, 88 ), "15" ) );
		setSymbol( 15, new FillSymbol( new Color( 175, 175, 120 ), "16" ) );
		setSymbol( 16, new FillSymbol( new Color( 183, 0, 255 ), "17" ) );
		setSymbol( 17, new FillSymbol( new Color( 0, 96, 0 ), "18" ) );
		setTip( new MapTip(){
			public String getTip( ShapeLayer layer, int i ){
				String result = null;
				result = new String( "Maintenance District - "
					+ layer.getField( "MAINT" ).getStringValue( i ) );
				return result;
			}
		});
	}
}