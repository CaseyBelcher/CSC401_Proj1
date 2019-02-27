package processes;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

import data_model.PeerRecord;

public class RegistrationServer {

    public static LinkedList<PeerRecord> peerList;
    public static int                    portNumber = 65243;

    public static void main ( final String[] args ) {

        System.out.println( "Starting RS server..." );
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

}
