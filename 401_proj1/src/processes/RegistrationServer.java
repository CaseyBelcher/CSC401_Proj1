package processes;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

import data_model.PeerRecord;

public class RegistrationServer {

    public static LinkedList<PeerRecord> peerList;
    public static int                    portNumber = 65243;
    public static int                    cookieNumber;

    public static void main ( String[] args ) {

        System.out.println( "Starting RS server..." );

        // initialize the peer list
        peerList = new LinkedList<PeerRecord>();

        ServerSocket serverSocket = null;

        // One-time setup.
        try {

            // Open a socket for listening.
            serverSocket = new ServerSocket( portNumber );
        }
        catch ( final Exception e ) {
            System.err.println( "Can't initialize server: " + e );
            e.printStackTrace();
            System.exit( 1 );
        }

        // start a thread to decrement the ttls
        final TimerThread timer = new TimerThread();
        timer.start();

        // Keep trying to accept new connections and serve them.
        while ( true ) {
            try {
                // Try to get a new client connection.
                final Socket sock = serverSocket.accept();

                // Handle interaction with the client
                new RSServerThread( sock, peerList ).start();
            }
            catch ( final IOException e ) {
                System.err.println( "Failure accepting client " + e );
            }
        }
    }

    /**
     *
     * Thread to decrement each of the ttls in the peer list every second
     *
     */
    private static class TimerThread extends Thread {

        public TimerThread () {
            super( "TimerThread" );
        }

        @Override
        public void run () {
            try {
                while ( true ) {
                    Thread.sleep( 1000 );
                    for ( int i = 0; i < peerList.size(); i++ ) {
                        final PeerRecord thispeer = peerList.get( i );
                        thispeer.ttl--;
                        if ( thispeer.ttl <= 0 ) {
                            thispeer.active = false;
                        }

                    }

                }
            }
            catch ( final InterruptedException e ) {
                e.getStackTrace();
            }

        }

    }

}
