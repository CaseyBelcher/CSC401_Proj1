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
                if ( request.equals( "pquery" ) ) {
                    pQuery();
                    // else the command is get a specific RFC
                    // parse the input for which rfc
                }
                else if ( request.equals( "leave" ) ) {
                    leave();
                }
                else if ( request.startsWith( "get" ) ) {
                    final Scanner lineScan = new Scanner( request );
                    lineScan.next();
                    final int rfcNumber = lineScan.nextInt();
                    getRFC( rfcNumber );
                }
                else if ( request.equals( "register" ) ) {
                    register();
                }
                else if ( request.equals( "keep alive" ) ) {
                    keepAlive();
                }
                else {
                    System.out.println( "Please enter a valid command" );
                }
                System.out.print( "Enter a command: " );
            }
        }

        public void register () {

            final String registerMessage = "Register" + "\n" + "Host: " + ipAddress + "\n" + "Cookie: " + cookieNumber
                    + "\n" + "RFCServerPortNumber: " + localPortNumber + "\n";

            try {
                // Try to create a socket connection to the given port
                // number.
                final Socket sock = new Socket( "10.152.49.66", portNumber );
                // Get formatted input/output streams for talking with the
                // server.
                final ObjectInputStream input = new ObjectInputStream( sock.getInputStream() );
                final ObjectOutputStream output = new ObjectOutputStream( sock.getOutputStream() );
                output.writeUTF( registerMessage );
                output.flush();
                System.out.println( input.readUTF() );
                sock.close();
            }
            catch ( final IOException e ) {
                e.getMessage();
            }
        }

        public void leave () {
            try {
                // Try to create a socket connection to the given port
                // number.
                final Socket sock = new Socket( "10.152.49.66", portNumber );
                // Get formatted input/output streams for talking with the
                // server.
                final ObjectInputStream input = new ObjectInputStream( sock.getInputStream() );
                final ObjectOutputStream output = new ObjectOutputStream( sock.getOutputStream() );
                final StringBuilder message = new StringBuilder( "Leaving\nHost: " );
                message.append( ipAddress );
                message.append( "\nCookie: " );
                message.append( cookieNumber );
                output.writeUTF( message.toString() );
                output.flush();
                System.out.println( input.readUTF() );
                sock.close();
            }
            catch ( final IOException e ) {
                e.getMessage();
            }

        }

        public void keepAlive () {
            try {
                // Try to create a socket connection to the given port
                // number.
                final Socket sock = new Socket( "10.152.49.66", portNumber );
                // Get formatted input/output streams for talking with the
                // server.
                final ObjectInputStream input = new ObjectInputStream( sock.getInputStream() );
                final ObjectOutputStream output = new ObjectOutputStream( sock.getOutputStream() );
                final StringBuilder message = new StringBuilder( "KeepAlive\nHost: " );
                message.append( ipAddress );
                message.append( "\nCookie: " );
                message.append( cookieNumber );
                output.writeUTF( message.toString() );
                output.flush();
                System.out.println( input.readUTF() );
                sock.close();
            }
            catch ( final IOException e ) {
                e.getMessage();
            }
        }

        /**
         * Send the message to the RFCServer asking for its index and merges it
         * with its own RFC index
         *
         * @param output
         * @param input
         */
        public void getIndex ( PeerRecord p ) {
            final StringBuilder message = new StringBuilder( "GET RFC-Index\nHOST: " );
            try {
                // Try to create a socket connection to the given port
                // number.
                final Socket sock = new Socket( p.getHostname().trim(), p.getPortNumber() );
                // Get formatted input/output streams for talking with the
                // server.
                final ObjectInputStream input = new ObjectInputStream( sock.getInputStream() );
                final ObjectOutputStream output = new ObjectOutputStream( sock.getOutputStream() );
                message.append( p.getHostname().trim() );
                output.writeUTF( message.toString() );
                output.flush();
                System.out.println( input.readUTF() );
                final LinkedList<RFC> temp = (LinkedList<RFC>) input.readObject();
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
            for ( int i = 0; i < rfcIndex.size(); i++ ) {
                final RFC r = rfcIndex.get( i );
                if ( r.rfcNumber == RFCNumber ) {
                    return;
                }
            }
            String peerAddress = "";
            int peerPort = 0;
            for ( int j = 0; j < peerList.size(); j++ ) {
                // update the RFC index
                getIndex( peerList.get( j ) );
                for ( int i = 0; i < rfcIndex.size(); i++ ) {
                    final RFC r = rfcIndex.get( i );
                    if ( r.rfcNumber == RFCNumber ) {
                        peerAddress = r.hostname.trim();
                        peerPort = peerList.get( j ).getPortNumber();
                        break;
                    }
                }
                if ( !peerAddress.isEmpty() ) {
                    break;
                }
            }

            try {

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
                System.out.println( input.readUTF() );
                int bytesRead;
                int current = 0;
                FileOutputStream fos = null;
                BufferedOutputStream bos = null;

                String filename = new File( "" ).getAbsolutePath();
                filename = filename.concat( "/401_proj1/data/rfc1/rfc" + RFCNumber + ".txt" );

                // receive file
                final byte[] mybytearray = new byte[600000];
                // temporarily use hello.txt to ensure it is working.
                fos = new FileOutputStream( filename );
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
                final RFC r = new RFC( RFCNumber, filename, ipAddress );
                rfcIndex.add( r );
            }
            catch ( final Exception e ) {
                System.out.println( e.getMessage() );
            }
        }

        public static void pQuery () {

            try {
                // Try to create a socket connection to the given port
                // number.
                final Socket sock = new Socket( "10.152.49.66", portNumber );
                // Get formatted input/output streams for talking with the
                // server.
                final ObjectInputStream input = new ObjectInputStream( sock.getInputStream() );
                final ObjectOutputStream output = new ObjectOutputStream( sock.getOutputStream() );
                final String message = "PQuery" + "\n" + "Host: " + ipAddress + "\n" + "Cookie: " + cookieNumber + "\n";
                output.writeUTF( message );
                output.flush();
                System.out.println( input.readUTF() );
                peerList = (LinkedList<PeerRecord>) input.readObject();
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
                System.out.println( request );

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
                output.writeUTF( "200 OK" );
                output.flush();
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
                        output.writeUTF( "200 OK" );
                        output.flush();
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
        final int folderTest = 1;
        localPortNumber = ( new Random() ).nextInt( 10000 ) + 1050;

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
        if ( folderTest == 1 ) {
            for ( int i = 8423; i <= 8450; i++ ) {
                String filename = new File( "" ).getAbsolutePath();
                filename = filename.concat( "/401_proj1/data/rfc1/rfc" + i + ".txt" );
                final File f = new File( filename );
                if ( f.exists() ) {
                    final RFC r = new RFC( i, filename, ipAddress );
                    rfcIndex.add( r );
                }
            }
            System.out.println( rfcIndex.size() );
        }
        else {
            for ( int i = 8423; i <= 8496; i++ ) {
                String filename = new File( "" ).getAbsolutePath();
                filename = filename.concat( "/401_proj1/data/rfc2/rfc" + i + ".txt" );
                final File f = new File( filename );
                if ( f.exists() ) {
                    final RFC r = new RFC( i, filename, ipAddress );
                    rfcIndex.add( r );
                }
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
            final Socket mySocket = new Socket( "10.152.49.66", portNumber );
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
        final String registerMessage = "Register" + "\n" + "Host: " + ipAddress + "\n" + "Cookie: " + -1 + "\n"
                + "RFCServerPortNumber: " + localPortNumber + "\n";

        try {
            out.writeUTF( registerMessage );
            out.flush();
            System.out.println( in.readUTF() );
        }
        catch ( final IOException e ) {
            e.getMessage();
        }
    }

}
