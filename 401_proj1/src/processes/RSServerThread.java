package processes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
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
    private String                       currentHost          = "";
    private int                          currentCookie        = -1;
    private int                          currentRFCPortNumber = -1;
    private final int                    ttl                  = 7200;

    public RSServerThread ( final Socket socket, final LinkedList<PeerRecord> myPeerList ) {
        super( "RSServerThread" );
        this.socket = socket;
        peerList = myPeerList;

    }

    /**
     * Register <cr> <lf> Host: <sp> value <cr> <lf> Cookie: <sp> value <cr>
     * <lf> RFCServerPortNumber: <sp> value <cr> <lf> <cr> <lf>
     *
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
    public String processInput ( final Scanner line ) {
        System.out.println( "calling processInput!" );
        String output = "";
        try {
            if ( line.hasNext() ) {
                final String thisWord = line.next();

                if ( thisWord.equals( "PQuery" ) ) {
                    // System.out.println("PQuery");
                    this.currentMethod = this.getClass().getDeclaredMethod( "pQuery" );
                }
                else if ( thisWord.equals( "KeepAlive" ) ) {
                    // System.out.println("KeepAlive");
                    this.currentMethod = this.getClass().getDeclaredMethod( "keepAlive" );
                }
                else if ( thisWord.equals( "Leaving" ) ) {
                    // System.out.println("Leaving");
                    this.currentMethod = this.getClass().getDeclaredMethod( "leaving" );
                }
                else if ( thisWord.equals( "Host:" ) ) {
                    this.currentHost = line.next();
                }
                else if ( thisWord.equals( "Cookie:" ) ) {
                    this.currentCookie = Integer.parseInt( line.next() );
                }
                else if ( thisWord.equals( "RFCServerPortNumber:" ) ) {
                    this.currentRFCPortNumber = Integer.parseInt( line.next() );
                }
                // else if(thisWord.equals("#")) {
                // if(this.currentMethod != null) {
                // output = (String) currentMethod.invoke(this);
                // }
                // this.currentMethod = null;
                // }

            }
            else {
                if ( this.currentMethod != null ) {
                    output = (String) currentMethod.invoke( this );
                }
                this.currentMethod = null;
            }

        }
        catch ( final NoSuchMethodException e ) {
            // System.out.println(e.getMessage());
            throw new IllegalArgumentException( e.getMessage() );
        }
        catch ( final InvocationTargetException e ) {
            throw new IllegalArgumentException( e.getMessage() );
        }
        catch ( final IllegalAccessException e ) {
            throw new IllegalArgumentException( e.getMessage() );
        }

        return output;
    }

    public String register () {
        boolean found = false;
        for ( int i = 0; i < peerList.size(); i++ ) {
            final PeerRecord current = peerList.get( i );
            if ( current.cookie == this.currentCookie ) {
                found = true;
                current.setTtl( ttl );

                if ( !current.isActive() ) {
                    current.setActive( true );
                }
                break;
            }
        }

        if ( !found ) {
            final PeerRecord newPeer = new PeerRecord( this.currentHost, this.currentCookie, this.currentRFCPortNumber,
                    true );
            peerList.add( newPeer );

            // change later
            return "You are now registered";

        }
        else {
            return "Welcome back, you previously registered";
        }

    }

    public String pQuery () {

        boolean found = false;
        for ( int i = 0; i < peerList.size(); i++ ) {
            final PeerRecord current = peerList.get( i );
            if ( current.cookie == this.currentCookie ) {
                found = true;
                current.setTtl( ttl );

                if ( !current.isActive() ) {
                    current.setActive( true );
                }
                // if inactive or always?
                break;
            }
        }

        if ( !found ) {
            final PeerRecord newPeer = new PeerRecord( this.currentHost, this.currentCookie, this.currentRFCPortNumber,
                    true );
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
        boolean found = false;
        for ( int i = 0; i < peerList.size(); i++ ) {
            final PeerRecord current = peerList.get( i );
            if ( current.cookie == this.currentCookie ) {
                current.setTtl( ttl );

                if ( !current.isActive() ) {
                    current.setActive( true );
                }
                found = true;
                break;
            }
        }
        if ( found ) {
            return "its-a-me...keepAlive!";
        }
        else {
            final PeerRecord newPeer = new PeerRecord( this.currentHost, this.currentCookie, this.currentRFCPortNumber,
                    true );
            peerList.add( newPeer );

            // change later
            return "You are now registered and set to active.";
        }

    }

    public String leaving () {
        boolean found = false;
        for ( int i = 0; i < peerList.size(); i++ ) {
            final PeerRecord current = peerList.get( i );
            if ( current.cookie == this.currentCookie ) {
                current.setActive( false );
                found = true;
                break;
            }
        }
        if ( found ) {
            return "imma leaving!";
        }
        else {
            final PeerRecord newPeer = new PeerRecord( this.currentHost, this.currentCookie, this.currentRFCPortNumber,
                    false );
            peerList.add( newPeer );

            // change later
            return "You are now registered and set to inactive";
        }

    }

    @Override
    public void run () {

        try (
                PrintWriter out = new PrintWriter( socket.getOutputStream(), true );
                BufferedReader in = new BufferedReader( new InputStreamReader( socket.getInputStream() ) ); ) {
            String inputLine, outputLine;

            // countdown each active peer in here? This while loop should run
            // forever, right?
            while ( ( inputLine = in.readLine() ) != null ) {
                final Scanner line = new Scanner( inputLine );
                outputLine = processInput( line );
                if ( !outputLine.equals( "" ) ) {
                    out.println( outputLine );
                }

                // set the actual end condition
                if ( outputLine.equals( "done" ) ) {
                    break;
                }

            }
            socket.close();
        }
        catch ( final IOException e ) {
            e.printStackTrace();
        }
    }
}
