/*
 * LayerChangedEvent.java
 *
 * Created on June 16, 2000, 4:50 PM
 */

package us.mn.state.dot.shape;

/**
 *
 * @author  engs1eri
 * @version 
 */
public class LayerChangedEvent extends java.util.EventObject {
    
	private int reason;
    public final static int GEOGRAPHY = 1,
							DATA = 2,
							ANIMATION = 16;

	/** Creates new LayerChangedEvent */
    public LayerChangedEvent( Object source, int why ){
        super( source );
        reason = why;
    }

    public int getReason(){
        return reason;
    }
    
    public boolean testReason( int test ){
        return ( test & reason ) > 0;
    }
}