package processes;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Scanner;

import data_model.PeerRecord;

public class RSServerThread extends Thread {

    private Socket                       socket               = null;
    private final LinkedList<PeerRecord> peerList;
    private Method                       currentMethod;
    private final String                 currentHost          = "";
    private int                          currentCookie        = -1;
    private final int                    currentRFCPortNumber = -1;

    public RSServerThread ( Socket socket, LinkedList<PeerRecord> myPeerList ) {
        super( "RSServerThread" );
        this.socket = socket;
        peerList = myPeerList;

    }

    /**
     * <cr> <lf>
     *
     *
     * peer-to-RS communication: Registering - add to peer list Leaving - mark
     * as inactive, which is done when a peer finds the RFC it wants PQuery -
     * asking for list of active peers KeepAlive - peer periodically sends this
     * to RS to reset its TTL in peer list
     *
     *
     * @throws IOException
     *
     **/
    public String processInput ( String message, ObjectOutputStream out, ObjectInputStream in ) throws IOException {
        System.out.println( "calling processInput!" );
        final String output = "";
        final Scanner s = new Scanner( message );
        final String header = s.nextLine();

        // Based on the header goes to desired method
        if ( header.equals( "Register" ) ) {
            register( message, out );
        }
        else if ( header.equals( "PQuery" ) ) {
            pQuery( message, out );
        }
        else if ( header.equals( "KeepAlive" ) ) {
            keepAlive( message, out );
        }
        else if ( header.equals( "Leaving" ) ) {
            leave( message, out );
        }

        return output;
    }

    /**
     * Resets the peers ttl to 7200
     * 
     * @param message
     * @param out
     */
    public void keepAlive ( String message, ObjectOutputStream out ) {

        final Scanner s = new Scanner( message );
        // skip over header
        s.nextLine();
        // skip over "Host: "
        s.nextLine();
        // skip over cookie for now
        s.next();
        currentCookie = s.nextInt();

        for ( int i = 0; i < peerList.size(); i++ ) {
            final PeerRecord thispeer = peerList.get( i );
            if ( thispeer.cookie == currentCookie ) {
                thispeer.ttl = 7200;
            }
        }
        try {
            out.writeUTF( "200 OK" );
            out.flush();
        }
        catch ( final IOException e ) {
            e.printStackTrace();
        }

        s.close();
    }

    /**
     * Sets the peer to inactive
     *
     * @param message
     * @param out
     */
    public void leave ( String message, ObjectOutputStream out ) {

        final Scanner s = new Scanner( message );
        // skip over header
        s.nextLine();
        // skip over "Host: "
        s.nextLine();
        // skip over cookie for now
        s.next();
        currentCookie = s.nextInt();

        for ( int i = 0; i < peerList.size(); i++ ) {
            final PeerRecord thispeer = peerList.get( i );
            if ( thispeer.cookie == currentCookie ) {
                thispeer.active = false;
            }
        }
        try {
            out.writeUTF( "200 OK" );
            out.flush();
        }
        catch ( final IOException e ) {
            e.printStackTrace();
        }

        s.close();
    }

    /**
     * Adds the peer to the peer list if it is not already in there else it
     * moves its status from inactive to active
     * 
     * @param message
     * @param out
     * @throws IOException
     */
    public void register ( String message, ObjectOutputStream out ) throws IOException {
        final Scanner s = new Scanner( message );
        // skip over header
        s.nextLine();
        // skip over "Host: "
        s.next();
        // get the host address
        final String hostAddress = s.nextLine();
        // skip over cookie for now
        s.nextLine();
        // get the local port number
        s.next();
        final int localPort = s.nextInt();
        boolean found = false;
        for ( int i = 0; i < peerList.size(); i++ ) {
            final PeerRecord current = peerList.get( i );
            if ( current.cookie == this.currentCookie ) {
                found = true;
                current.setTtl( 7200 );

                if ( !current.isActive() ) {
                    current.setActive( true );
                }
                break;
            }
        }

        if ( !found ) {
            final PeerRecord newPeer = new PeerRecord( hostAddress, peerList.size(), localPort );
            peerList.add( newPeer );

            // change later
            final int size = peerList.size() - 1;
            final String messageToPass = "200 OK\nCookie: " + size;
            out.writeUTF( messageToPass );
            out.flush();

        }
        else {
            out.writeUTF( "200 OK" );
            out.flush();
        }

    }

    /**
     * Returns the peer list to the client
     *
     * @param message
     * @param out
     */
    public void pQuery ( String message, ObjectOutputStream out ) {
        final Scanner s = new Scanner( message );
        // skip over header
        s.nextLine();
        // skip over "Host: "
        s.nextLine();
        // skip over cookie for now
        s.next();
        currentCookie = s.nextInt();
        s.nextLine();
        try {
            final LinkedList<PeerRecord> temp = new LinkedList<PeerRecord>();
            for ( int i = 0; i < peerList.size(); i++ ) {
                final PeerRecord p = peerList.get( i );
                if ( p.isActive() && p.getCookie() != currentCookie ) {
                    temp.add( p );
                }
            }
            if ( temp.isEmpty() ) {
                out.writeUTF( "400 No Active Peers" );
                out.flush();
            }
            else {
                out.writeUTF( "200 OK" );
                out.flush();
                out.writeObject( temp );
                out.flush();
            }
        }
        catch ( final IOException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @Override
    public void run () {

        try {
            // Get formatted input/output streams for this thread. These can
            // read and write
            // strings, arrays of bytes, ints, lots of things.
            final ObjectOutputStream output = new ObjectOutputStream( socket.getOutputStream() );
            final ObjectInputStream input = new ObjectInputStream( socket.getInputStream() );

            final String message = input.readUTF();
            processInput( message, output, input );
            System.out.println( message );

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
