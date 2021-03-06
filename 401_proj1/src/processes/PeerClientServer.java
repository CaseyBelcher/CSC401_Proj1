package processes;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;

import data_model.PeerRecord;
import data_model.RFC;

public class PeerClientServer {

    public static LinkedList<RFC>        rfcIndex;
    // TODO: find correct non-local hostname
    private static String                hostName;
    public static int                    portNumber = 65243;
    public static int                    localPortNumber;
    public static LinkedList<PeerRecord> peerList;
    public static int                    cookieNumber;
    public static String                 ipAddress;

    /**
     * Thread that interacts with the user from the terminal to do the requested
     * commands
     *
     * @author Patrick
     *
     */
    public static class RFCClient extends Thread {

        RFCClient () {
        }

        /**
         * Loops for the input from user to do the specified command
         */
        @Override
        public void run () {
            final Scanner s = new Scanner( System.in );
            System.out.print( "Enter a command: " );
            while ( true ) {
                final String request = s.nextLine();
                // if command is get index then get the index from the other
                // peer
                if ( request.equals( "get index" ) ) {
                    getIndex();
                }
                else if ( request.equals( "p" ) ) {
                    pQuery();
                    // else the command is get a specific RFC
                    // parse the input for which rfc
                }
                else if ( request.equals( "leave" ) ) {
                    break;
                }
                else {
                    final Scanner lineScan = new Scanner( request );
                    lineScan.next();
                    final int rfcNumber = lineScan.nextInt();
                    getRFC( rfcNumber );
                }
                System.out.print( "Enter a command: " );
                // sock.close();
            }
            System.out.println( "gone" );
        }

        /**
         * Send the message to the RFCServer asking for its index and merges it
         * with its own RFC index
         *
         * @param output
         * @param input
         */
        public void getIndex () {
            final StringBuilder message = new StringBuilder( "GET RFC-Index\nHOST: " );
            int peerPort = 0;
            String peerAddress = "";
            try {
                for ( int i = 0; i < peerList.size(); i++ ) {
                    if ( peerList.get( i ).getPortNumber() != localPortNumber ) {
                        peerPort = peerList.get( i ).getPortNumber();
                        peerAddress = peerList.get( i ).getHostname().trim();
                        break;
                    }
                }
                // Try to create a socket connection to the given port
                // number.
                System.out.println( peerPort + peerAddress );
                final Socket sock = new Socket( peerAddress, peerPort );
                // Get formatted input/output streams for talking with the
                // server.
                final ObjectInputStream input = new ObjectInputStream( sock.getInputStream() );
                final ObjectOutputStream output = new ObjectOutputStream( sock.getOutputStream() );
                message.append( peerAddress );
                output.writeUTF( message.toString() );
                output.flush();
                final LinkedList<RFC> temp = (LinkedList<RFC>) input.readObject();
                System.out.println( temp.size() );
                for ( int i = 0; i < temp.size(); i++ ) {
                    if ( !rfcIndex.contains( temp.get( i ) ) ) {
                        rfcIndex.add( temp.get( i ) );
                    }
                }
                sock.close();
            }
            catch ( IOException | ClassNotFoundException e ) {
                System.out.println( e.getStackTrace() );
            }
        }

        /**
         * Sends a message to the RFC Server asking for a specific RFC it has
         * and uploads it to a file locally
         *
         * @param output
         * @param input
         * @param RFCNumber
         */
        public void getRFC ( int RFCNumber ) {
            String peerAddress = "";
            for ( int i = 0; i < rfcIndex.size(); i++ ) {
                final RFC r = rfcIndex.get( i );
                if ( r.rfcNumber == RFCNumber ) {
                    peerAddress = r.hostname;
                    System.out.println( peerAddress );
                    break;
                }
            }
            int peerPort = 0;
            try {
                for ( int i = 0; i < peerList.size(); i++ ) {
                    final PeerRecord p = peerList.get( i );
                    System.out.println( p.getPortNumber() );
                    System.out.println( p.getHostname() );

                    if ( p.getPortNumber() != localPortNumber && p.getHostname().trim().equals( peerAddress ) ) {
                        peerPort = peerList.get( i ).getPortNumber();
                        break;
                    }
                }

                // Try to create a socket connection to the given port
                // number.
                final Socket sock = new Socket( peerAddress, peerPort );
                // Get formatted input/output streams for talking with the
                // server.
                final ObjectInputStream input = new ObjectInputStream( sock.getInputStream() );
                final ObjectOutputStream output = new ObjectOutputStream( sock.getOutputStream() );
                final StringBuilder message = new StringBuilder( "GET RFC " );
                message.append( RFCNumber );
                message.append( "\nHOST: " );
                message.append( peerAddress );
                output.writeUTF( message.toString() );
                output.flush();
                int bytesRead;
                int current = 0;
                FileOutputStream fos = null;
                BufferedOutputStream bos = null;

                // receive file
                final byte[] mybytearray = new byte[600000];
                // temporarily use hello.txt to ensure it is working.
                fos = new FileOutputStream( "hello.txt" );
                bos = new BufferedOutputStream( fos );
                bytesRead = input.read( mybytearray, 0, mybytearray.length );
                current = bytesRead;

                do {
                    bytesRead = input.read( mybytearray, current, ( mybytearray.length - current ) );
                    if ( bytesRead >= 0 ) {
                        current += bytesRead;
                    }
                }
                while ( bytesRead > -1 );

                bos.write( mybytearray, 0, current );
                bos.flush();
                sock.close();
            }
            catch ( final Exception e ) {
                System.out.println( e.getMessage() );
            }
        }

        public static void pQuery () {

            try {
                // Try to create a socket connection to the given port
                // number.
                final Socket sock = new Socket( hostName, portNumber );
                // Get formatted input/output streams for talking with the
                // server.
                final ObjectInputStream input = new ObjectInputStream( sock.getInputStream() );
                final ObjectOutputStream output = new ObjectOutputStream( sock.getOutputStream() );
                final String message = "PQuery" + "\n" + "Host: " + ipAddress + "\n" + "Cookie: " + 0 + "\n";
                output.writeUTF( message );
                output.flush();
                peerList = (LinkedList<PeerRecord>) input.readObject();
                System.out.println( peerList.size() );
                sock.close();

            }
            catch ( final IOException | ClassNotFoundException e ) {
                System.out.println( e.getMessage() );
            }

        }
    }

    /**
     * RFC Server thread that handles requests from RFC clients
     *
     * @author Patrick
     *
     */
    public static class RFCServer extends Thread {
        Socket sock;

        RFCServer ( Socket sock ) {
            this.sock = sock;
        }

        // Processes the request and calls the process method
        @Override
        public void run () {
            try {
                // Get formatted input/output streams for this thread. These can
                // read and write
                // strings, arrays of bytes, ints, lots of things.
                final ObjectOutputStream output = new ObjectOutputStream( sock.getOutputStream() );
                final ObjectInputStream input = new ObjectInputStream( sock.getInputStream() );
                final String request = input.readUTF();
                process( request, output );

            }
            catch ( final IOException e ) {
                System.out.println( "IO Error: " );
            }
            finally {
                try {
                    // Close the socket on the way out.
                    sock.close();
                }
                catch ( final Exception e ) {
                }
            }
        }

        /**
         * Sends its RFC index to the client
         *
         * @param output
         */
        public void getIndex ( ObjectOutputStream output ) {
            try {
                output.writeObject( rfcIndex );
                output.flush();
            }
            catch ( final IOException e ) {
                e.printStackTrace();
            }

        }

        /**
         * Retrieves the RFC it has locally and sends it to the client
         *
         * @param number
         * @param output
         */
        public void getRFC ( int number, ObjectOutputStream output ) {
            for ( int i = 0; i < rfcIndex.size(); i++ ) {
                if ( rfcIndex.get( i ).rfcNumber == number ) {
                    final File f = new File( rfcIndex.get( i ).title );
                    final byte[] mybytearray = new byte[(int) f.length()];
                    try {
                        final FileInputStream fis = new FileInputStream( f );
                        final BufferedInputStream bis = new BufferedInputStream( fis );
                        bis.read( mybytearray, 0, mybytearray.length );
                        output.write( mybytearray, 0, mybytearray.length );
                        output.flush();
                    }
                    catch ( final IOException e ) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    System.out.println( "Done." );
                }
            }
        }

        /**
         * Parses the request and calls the appropriate method
         *
         * @param request
         * @param output
         */
        public void process ( String request, ObjectOutputStream output ) {
            final Scanner s = new Scanner( request );
            s.next();
            final String method = s.next();
            if ( method.equals( "RFC" ) ) {
                final int rfcNumber = s.nextInt();
                getRFC( rfcNumber, output );
            }
            else {
                getIndex( output );
            }
        }
    }

    public static void main ( String[] args ) throws InterruptedException, ClassNotFoundException {
        // Create random local port number
        localPortNumber = ( new Random() ).nextInt( 10000 ) + 1050;
        // print it out so it is easier to test
        System.out.println( localPortNumber );

        try {
            final InetAddress ip = InetAddress.getLocalHost();
            ipAddress = ip.getHostAddress();
            System.out.println( "hostname: " + ipAddress );
        }
        catch ( final UnknownHostException e ) {
            System.err.println( "Can't find localhost???" );
            System.exit( 1 );
        }
        rfcIndex = new LinkedList<RFC>();
        // check to see which of the 60 most recent RFCs we have
        for ( int i = 8423; i <= 8496; i++ ) {
            String filename = new File( "" ).getAbsolutePath();
            filename = filename.concat( "/401_proj1/data/latest_60RFCs/rfc" + i + ".txt" );
            final File f = new File( filename );
            if ( f.exists() ) {
                final RFC r = new RFC( i, filename, ipAddress );
                rfcIndex.add( r );
            }
        }

        ServerSocket serverSocket = null;

        // One-time setup.
        try {

            // Open a socket for listening.
            serverSocket = new ServerSocket( localPortNumber );
        }
        catch ( final Exception e ) {
            System.err.println( "Can't initialize server: " + e );
            e.printStackTrace();
            System.exit( 1 );
        }

        // Register the client
        try {
            final Socket mySocket = new Socket( hostName, portNumber );
            final ObjectOutputStream out = new ObjectOutputStream( mySocket.getOutputStream() );
            final ObjectInputStream in = new ObjectInputStream( mySocket.getInputStream() );
            register( out, in );
            mySocket.close();

        }
        catch ( final UnknownHostException e ) {
            System.err.println( "Don't know about host " + hostName );
            System.exit( 1 );
        }
        catch ( final IOException e ) {
            System.err.println( "Couldn't get I/O for the connection to " + hostName );
            System.exit( 1 );
        }

        // Start the client thread
        new RFCClient().start();

        // Keep trying to accept new connections and serve them.
        while ( true ) {
            try {
                // Try to get a new client connection.
                final Socket sock = serverSocket.accept();

                // Handle interaction with the client
                new RFCServer( sock ).start();
            }
            catch ( final IOException e ) {
                System.err.println( "Failure accepting client " + e );
            }
        }
    }

    public static void register ( final ObjectOutputStream out, final ObjectInputStream in ) {
        final String registerMessage = "Register" + "\n" + "Host: " + ipAddress + "\n" + "Cookie: " + 0 + "\n"
                + "RFCServerPortNumber: " + localPortNumber + "\n";

        try {
            out.writeUTF( registerMessage );
            out.flush();
            final String returnMessage = in.readUTF();
            System.out.println( returnMessage );
        }
        catch ( final IOException e ) {
            e.getMessage();
        }
    }

}
