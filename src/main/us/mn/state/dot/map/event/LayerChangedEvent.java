package us.mn.state.dot.shape;

/**
 * LayerChangedEvents are sent to LayerChangedListeners when a Layers data is
 *changed.
 *
 * @author <a href="mailto:erik.engstrom@dot.state.mn.us">Erik Engstrom</a>
 * @version $Revision: 1.3 $ $Date: 2001/07/09 21:10:19 $
 */
public class LayerChangedEvent extends java.util.EventObject {
    
	private int reason;
    public final static int GEOGRAPHY = 1;
	public final static int	DATA = 2;
	public final static int	ANIMATION = 16;

	/**
	 * Creates new LayerChangedEvent 
	 */
    public LayerChangedEvent( Object source, int why ){
        super( source );
        reason = why;
    }

	/**
	 * Get the reason the Layer changed.
	 */
    public int getReason(){
        return reason;
    }
    
    public boolean testReason( int test ){
        return ( test & reason ) > 0;
    }
}