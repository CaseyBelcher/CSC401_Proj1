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
    private int                          currentCookie        = -1;
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
     * @throws IOException
     *
     **/
    public String processInput ( String message, ObjectOutputStream out, ObjectInputStream in ) throws IOException {
        System.out.println( "calling processInput!" );
        final String output = "";
        final Scanner s = new Scanner( message );
        final String header = s.nextLine();
        if ( header.equals( "Register" ) ) {
            register( message, out );
        }
        else if ( header.equals( "PQuery" ) ) {
            pQuery( message, out );
        }

        return output;
    }

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
            final PeerRecord newPeer = new PeerRecord( hostAddress, 0, localPort );
            peerList.add( newPeer );

            // change later
            out.writeInt( peerList.size() );
            out.flush();

        }
        else {
            out.writeInt( -1 );
            out.flush();
        }

    }

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
            out.writeObject( temp );
            out.flush();
        }
        catch ( final IOException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

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

            final String message = input.readUTF();
            processInput( message, output, input );
            System.out.println( peerList.size() );

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
