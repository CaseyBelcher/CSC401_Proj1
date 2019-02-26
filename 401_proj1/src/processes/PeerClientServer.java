package processes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;

import data_model.PeerRecord;
import data_model.RFC;

public class PeerClientServer {

    public LinkedList<RFC>        rfcIndex;
    // TODO: find correct non-local hostname
    private static String         hostName;
    public static int             portNumber = 65243;
    public LinkedList<PeerRecord> peerList;
    public static int             cookieNumber;

    public static void main ( final String[] args ) {

        try {
            final InetAddress ip = InetAddress.getLocalHost();
            hostName = ip.getHostName();
            System.out.println( "hostname: " + hostName );
            cookieNumber = ( new Random() ).nextInt( 500 );
        }
        catch ( final UnknownHostException e ) {
            System.err.println( "Can't find localhost???" );
            System.exit( 1 );
        }

        try (
                Socket mySocket = new Socket( hostName, portNumber );
                PrintWriter out = new PrintWriter( mySocket.getOutputStream(), true );
                BufferedReader in = new BufferedReader( new InputStreamReader( mySocket.getInputStream() ) ); ) {
            // BufferedReader stdIn =
            // new BufferedReader(new InputStreamReader(System.in));
            // String fromServer;
            String fromUser;

            final Scanner inputScan = new Scanner( System.in );
            while ( ( fromUser = inputScan.next() ) != null ) {
                if ( fromUser.equals( "PQuery" ) ) {
                    pQuery( out, in );
                }
                else if ( fromUser.equals( "Register" ) ) {
                    register( out, in );
                }
                else if ( fromUser.equals( "KeepAlive" ) ) {
                    keepAlive( out, in );
                }
                else if ( fromUser.equals( "Leave" ) ) {
                    leave( out, in );
                }
                else {
                    // change later?
                    System.out.print( "Invalid message. Enter PQuery, Register, KeepAlive, or Leave\n" );
                }
            }
            inputScan.close();

        }
        catch ( final UnknownHostException e ) {
            System.err.println( "Don't know about host " + hostName );
            System.exit( 1 );
        }
        catch ( final IOException e ) {
            System.err.println( "Couldn't get I/O for the connection to " + hostName );
            System.exit( 1 );
        }

    }

    public static void register ( final PrintWriter out, final BufferedReader in ) {
        final String test = "Register" + "\n" + "Host: me" + "\n" + "Cookie: " + cookieNumber + "\n"
                + "RFCServerPortNumber: 6" + "\n";

        out.println( test );

        try {
            String fromServer;

            while ( ( fromServer = in.readLine() ) != null ) {
                final Scanner servLine = new Scanner( fromServer );
                if ( servLine.hasNext() ) {
                    System.out.println( servLine.next() );
                }
                else {
                    break;
                }
                servLine.close();

            }

        }
        catch ( final IOException e ) {

        }
    }

    public static void keepAlive ( final PrintWriter out, final BufferedReader in ) {
        final String test = "KeepAlive" + "\n" + "Host: me" + "\n" + "Cookie: " + cookieNumber + "\n";

        out.println( test );

        try {
            String fromServer;

            while ( ( fromServer = in.readLine() ) != null ) {
                final Scanner servLine = new Scanner( fromServer );
                if ( servLine.hasNext() ) {
                    System.out.println( servLine.next() );
                }
                else {
                    break;
                }
                servLine.close();

            }

        }
        catch ( final IOException e ) {

        }
    }

    public static void leave ( final PrintWriter out, final BufferedReader in ) {
        final String test = "Leaving" + "\n" + "Host: me" + "\n" + "Cookie: " + cookieNumber + "\n";

        out.println( test );

        try {
            String fromServer;

            while ( ( fromServer = in.readLine() ) != null ) {
                final Scanner servLine = new Scanner( fromServer );
                if ( servLine.hasNext() ) {
                    System.out.println( servLine.next() );
                }
                else {
                    break;
                }
                servLine.close();

            }

        }
        catch ( final IOException e ) {

        }

    }

    public static void pQuery ( final PrintWriter out, final BufferedReader in ) {
        final String test = "PQuery" + "\n" + "Host: me" + "\n" + "Cookie: " + cookieNumber + "\n"
                + "RFCServerPortNumber: 6" + "\n";

        out.println( test );

        try {
            String fromServer;

            while ( ( fromServer = in.readLine() ) != null ) {
                final Scanner servLine = new Scanner( fromServer );
                if ( servLine.hasNext() ) {
                    System.out.println( servLine.next() );
                }
                else {
                    break;
                }
                servLine.close();

            }

        }
        catch ( final IOException e ) {

        }

    }

}
