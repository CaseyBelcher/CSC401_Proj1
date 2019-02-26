package data_model;

import java.time.LocalDateTime;

public class PeerRecord {

    /** hostname of peer */
    public String  hostname;
    /** cookie for this registered peer */
    public int     cookie;
    /** whether the peer is still in the system */
    public boolean active;
    /** Time To Live */
    public int     ttl;
    /** port number the RFC server is listening to */
    public int     portNumber;
    /** number of times the peer has registered during the last 30 days */
    public int     activityNumber;
    /** last time /date the peer registered */
    public String  lastRegisteredDate;

    public PeerRecord ( final String hostname, final int cookie, final int portNumber, final boolean active ) {

        this.hostname = hostname;
        this.cookie = cookie;
        this.active = active;
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

    public void setHostname ( final String hostname ) {
        this.hostname = hostname;
    }

    public int getCookie () {
        return cookie;
    }

    public void setCookie ( final int cookie ) {
        this.cookie = cookie;
    }

    public boolean isActive () {
        return active;
    }

    public void setActive ( final boolean active ) {
        this.active = active;
    }

    public int getTtl () {
        return ttl;
    }

    public void setTtl ( final int ttl ) {
        this.ttl = ttl;
    }

    public int getPortNumber () {
        return portNumber;
    }

    public void setPortNumber ( final int portNumber ) {
        this.portNumber = portNumber;
    }

    public int getActivityNumber () {
        return activityNumber;
    }

    public void setActivityNumber ( final int activityNumber ) {
        this.activityNumber = activityNumber;
    }

    public String getLastRegisteredDate () {
        return lastRegisteredDate;
    }

    public void setLastRegisteredDate ( final String lastRegisteredDate ) {
        this.lastRegisteredDate = lastRegisteredDate;
    }

}
