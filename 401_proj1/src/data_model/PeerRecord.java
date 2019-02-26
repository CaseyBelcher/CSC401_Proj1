package data_model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class PeerRecord implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -9026094576599474511L;
    /** hostname of peer */
    public String             hostname;
    /** cookie for this registered peer */
    public int                cookie;
    /** whether the peer is still in the system */
    public boolean            active;
    /** Time To Live */
    public int                ttl;
    /** port number the RFC server is listening to */
    public int                portNumber;
    /** number of times the peer has registered during the last 30 days */
    public int                activityNumber;
    /** last time /date the peer registered */
    public String             lastRegisteredDate;

    public PeerRecord ( String hostname, int cookie, int portNumber ) {

        this.hostname = hostname;
        this.cookie = cookie;
        this.active = true;
        this.ttl = 7200;
        this.portNumber = portNumber;
        this.activityNumber = 1;
        this.lastRegisteredDate = LocalDateTime.now().toString();

    }

    public PeerRecord () {

    }

    @Override
    public String toString () {
        return "Hostname:" + this.hostname + "\n" + "Cookie:" + this.cookie + "\n" + "Active:" + this.active + "\n"
                + "TTL:" + this.ttl + "\n" + "Port-Number:" + this.portNumber + "\n" + "Activity-Number:"
                + this.activityNumber + "\n" + "lastDate:" + this.lastRegisteredDate;
    }

    public String getHostname () {
        return hostname;
    }

    public void setHostname ( String hostname ) {
        this.hostname = hostname;
    }

    public int getCookie () {
        return cookie;
    }

    public void setCookie ( int cookie ) {
        this.cookie = cookie;
    }

    public boolean isActive () {
        return active;
    }

    public void setActive ( boolean active ) {
        this.active = active;
    }

    public int getTtl () {
        return ttl;
    }

    public void setTtl ( int ttl ) {
        this.ttl = ttl;
    }

    public int getPortNumber () {
        return portNumber;
    }

    public void setPortNumber ( int portNumber ) {
        this.portNumber = portNumber;
    }

    public int getActivityNumber () {
        return activityNumber;
    }

    public void setActivityNumber ( int activityNumber ) {
        this.activityNumber = activityNumber;
    }

    public String getLastRegisteredDate () {
        return lastRegisteredDate;
    }

    public void setLastRegisteredDate ( String lastRegisteredDate ) {
        this.lastRegisteredDate = lastRegisteredDate;
    }

}
