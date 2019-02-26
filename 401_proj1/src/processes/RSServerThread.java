package processes;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Scanner;

import data_model.PeerRecord;

// TODO: MUST ALSO BE MADE THREADSAFE when accessing
// TODO: what happens in peer disconnects in the middle of outputting?
public class RSServerThread extends Thread {

    private Socket                       socket               = null;
    private final LinkedList<PeerRecord> peerList;
    private Method                       currentMethod;
    private final String                 currentHost          = "";
    private final int                    currentCookie        = -1;
    private final int                    currentRFCPortNumber = -1;

    public RSServerThread ( Socket socket, LinkedList<PeerRecord> myPeerList ) {
        super( "RSServerThread" );
        this.socket = socket;
        peerList = myPeerList;

    }

    /**
     * PQuery <cr> <lf> Host: <sp> value <cr> <lf> Cookie: <sp> value <cr> <lf>
     * RFCServerPortNumber: <sp> value <cr> <lf> <cr> <lf>
     *
     * KeepAlive <cr> <lf> Host: <sp> value <cr> <lf> Cookie: <sp> value <cr>
     * <lf> <cr> <lf>
     *
     * Leaving <cr> <lf> Host: <sp> value <cr> <lf> Cookie: <sp> value <cr> <lf>
     * <cr> <lf>
     *
     *
     * peer-to-RS communication: Registering - add to peer list Leaving - mark
     * as inactive, which is done when a peer finds the RFC it wants PQuery -
     * asking for list of active peers KeepAlive - peer periodically sends this
     * to RS to reset its TTL in peer list
     *
     * method = obj.getClass().getMethod(methodName, param1.class, param2.class,
     * ..); }
     *
     **/
    public String processInput ( Scanner line ) {
        System.out.println( "calling processInput!" );
        final String output = "";

        return output;
    }

    public String pQuery () {

        boolean found = false;
        for ( int i = 0; i < peerList.size(); i++ ) {
            if ( peerList.get( i ).cookie == this.currentCookie ) {
                found = true;
                break;
            }
        }

        if ( !found ) {
            final PeerRecord newPeer = new PeerRecord( this.currentHost, this.currentCookie,
                    this.currentRFCPortNumber );
            peerList.add( newPeer );

        }

        // String output = "its-a-me...pQuery!" + "\n" +
        // "Host: " + this.currentHost + "\n" +
        // "Cookie: " + this.currentCookie + "\n" +
        // "RFCServerPortNumber: " + this.currentRFCPortNumber + "\n";

        String output = "";
        for ( int i = 0; i < peerList.size(); i++ ) {
            output += peerList.get( i ).toString();
        }

        return output + "\n";

    }

    public String keepAlive () {
        return "its-a-me...keepAlive!";
    }

    public String leaving () {
        return "imma leaving!";
    }

    @Override
    public void run () {

        try {
            // Get formatted input/output streams for this thread. These can
            // read and write
            // strings, arrays of bytes, ints, lots of things.
            final ObjectOutputStream output = new ObjectOutputStream( socket.getOutputStream() );
            final ObjectInputStream input = new ObjectInputStream( socket.getInputStream() );

            final int thing = input.readInt();
            output.writeInt( thing );
            output.flush();
            final PeerRecord p = new PeerRecord( "Patrick", thing, 0 );
            peerList.add( p );
            output.writeObject( peerList );
            output.flush();

        }
        catch ( final IOException e ) {
            System.out.println( "IO Error: " + e );
        }
        finally {
            try {
                // Close the socket on the way out.
                socket.close();
            }
            catch ( final Exception e ) {
            }
        }
    }
}
