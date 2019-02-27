package data_model;

import java.io.Serializable;

public class RFC implements Serializable {

    private static final long serialVersionUID = 7013950818363829503L;
    /** four digit number */
    public int                rfcNumber;
    /** title of RFC */
    public String             title;
    /** hostname of peer containing the RFC */
    public String             hostname;

    /**
     * = Time To Live initialized to 7200 for RFCs maintained locally this never
     * changes for RFCs maintained at remote peer, decrement every second
     */
    public int                ttl;

    public RFC ( int rfcNumber, String title, String hostname ) {
        this.rfcNumber = rfcNumber;
        this.title = title;
        this.hostname = hostname;
        this.ttl = 7200;

    }

}
