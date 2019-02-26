package data_model;

public class RFC {

    /** four digit number */
    public int    rfcNumber;
    /** title of RFC */
    public String title;
    /** hostname of peer containing the RFC */
    public String hostname;

    /**
     * = Time To Live initialized to 7200 for RFCs maintained locally this never
     * changes for RFCs maintained at remote peer, decrement every second
     */
    public int    ttl;

    public RFC () {
        this.ttl = 7200;

    }

}
