import java.net.*;
import java.io.*;
import javax.sound.sampled.*;
import java.nio.*;

public class app {
	static String E = "E7087";
    static String ET = E + "T06";
    static String E0 = "E0000";
	static String M = "M5015CAM=";
    static int SP = 999;
    static String v = "V8562";
    static int ServerPort = 38018;
    static int ClientPort = 48018;

    byte[] hostIP, rxbuffer, txbuffer;
    DatagramSocket s, r;
    DatagramPacket p, q;
    InetAddress hostAddress;

	public static void main(String[] args) {
        app app = new app( 128 );
        System.out.println( "- - - - - - - - - - Computer Netrowks 2 - - - - - - - - - -" );
        while( true ){ 
            System.out.println( "\nChoose a number to select functionality, or x to exit." );
            System.out.println( "0: echo with delay" );
            System.out.println( "1: echo without delay" );
            System.out.println( "2: echo with temperature" );
            System.out.println( "3: throughput with delay" );
            System.out.println( "4: throughput without delay" );
            System.out.println( "5: image from cam 1" );
            System.out.println( "6: image from cam 2" );
            System.out.println( "7: frequency generator" );
            System.out.println( "8: music with DPCM" );
            System.out.println( "9: music with AQ-DPCM" );
            int song;
            String cc;
            switch( in() ){
                case 0:
                    app.echo( app, E );
                    break;
                case 1:
                    app.echo( app, E0 );
                    break;
                case 2:
                    app.echotemp( app, E );
                    break;
                case 3:
                    app.throughput( app, E );
                    break;
                case 4:
                    app.throughput( app, E0 );
                    break;
                case 5:
                    app.image( app, M + "1" );
                    break;
                case 6:
                    app.image( app, M + "2" );
                    break;
                case 7:
                    app.playmusic( app.fetchdpcm( app, v + "T" + SP ), 8 );
                    break;
                case 8:
                    System.out.println( "- - - music with DPCM - - -" );
                    System.out.println( "Select song number" );
                    song = in();
                    cc = song < 10 ? "0" + song : song + "";
                    app.music( app, v + "L" + cc + "F" + SP );
                    break;
                case 9:
                    System.out.println( "- - - music with AQ-DPCM - - -" );
                    System.out.println( "Select song number" );
                    app.resizebuffer( app, 132 );
                    song = in();
                    cc = song < 10 ? "0" + song : song + "";
                    app.music( app, v + "AQL" + cc + "F" + SP );
                    app.resizebuffer( app, 128 );
                    break;
                default:
                    System.out.println( "Wrong input. Try again..." );
            }
        }
	}
    public static int in(){
        System.out.print( ">" );
        try{
            BufferedReader in = new BufferedReader( new InputStreamReader( System.in ) );
            String line = in.readLine();
            try{
                return Integer.parseInt( line );
            }
            catch( Exception x ){
                System.exit( 1 );
            }
        }
        catch( IOException e ){
            System.exit( 1 );
        }
        return 0;
    }
    public void echo( app a, String code ){
        a.setCode( code );
        long ts, t1, t2;
        ts = System.currentTimeMillis();
        t1 = ts;
        t2 = t1;
        
        System.out.println( "Input number of minutes" );
        int mins = in();
        
        System.out.println( "Tail file " + code + ".txt for the output." );
         
        try{
            FileWriter fw = new FileWriter( code + ".txt" );
            BufferedWriter bw = new BufferedWriter( fw );
            while( t2 < ts + mins * 60 * 1000 ){
                try{
                    t1 = System.currentTimeMillis();
                    a.s.send( a.p );
                    a.r.receive( a.q );
                    t2 = System.currentTimeMillis();
                    String message = new String( a.rxbuffer ); 
                    bw.write( t2 - t1 + "\n" );
                } 
                catch( Exception e ){
                }
            }
            bw.close();
        }
        catch( Exception x ){
            System.out.println( "Error while echoing: " + x );
        }
    }
    public void echotemp( app a, String code ){
        System.out.println( "Tail file temp_" + code + ".txt for the output." );
        try{
            FileWriter fw = new FileWriter( "temp_" + code + ".txt" );
            BufferedWriter bw = new BufferedWriter( fw );
            for( int i = 1; i <= 8; ++i ){
                a.setCode( code + "T0" + i );
                a.s.send( a.p );
                a.r.receive( a.q );
                String message = new String( a.rxbuffer );
                String temp = message.split( " " )[ 6 ];
                if( i == 8 ){
                    temp = message.split( " " )[ 5 ];
                }
                bw.write( temp + "\n" );
            }
            bw.close();
        }
        catch( Exception x ){
            System.out.println( x );
        }
    }
    public void throughput( app a, String code ){
        a.setCode( code );
        int counter = 0, packetsToAverage = 8;
        long ts, t1, t2;
        long []tt = new long[ 400000 ];
        ts = System.currentTimeMillis();
        t1 = ts;
        t2 = ts;
        
        System.out.println( "Input number of minutes" );
        int mins = in();
        
        System.out.println( "Tail file tp_" + code + ".txt for the output." );

        try{
            FileWriter fw = new FileWriter( "tp_" + code + ".txt" );
            BufferedWriter bw = new BufferedWriter( fw );
            
            while( t2 < ts + mins * 60 * 1000 ){
                try{
                    t1 = System.currentTimeMillis();
                    a.s.send( a.p );
                    a.r.receive( a.q );
                    t2 = System.currentTimeMillis();
                    tt[ counter++ ] = t2 - t1;
                    if( counter >= packetsToAverage ){
                        int s = 0;
                        for( int i = counter - packetsToAverage; i < counter; ++i ){
                            s += tt[ i ];
                        }
                        int thr = 128 * packetsToAverage * 1000 / s;
                        bw.write( thr + "\n" );
                    }
                }
                catch( Exception e ){
                }
            }
            bw.close();
        }
        catch( Exception x ){
            System.out.println( "Error while calculating throughput: " + x );
        }
        
    }
    public void image( app a, String code ){
        a.setCode( code );
        
        System.out.println( "Image will be saved on file " + code + ".jpg." );
        
        try{
            a.s.send( a.p );
			FileOutputStream out = new FileOutputStream( code + ".jpg" );
			while( true ){
                try{
                    a.r.receive( a.q );
                    out.write( a.q.getData() );
                } catch( Exception x ){
                    System.out.println( x );
                    break;
                }
			}
            out.close();
        }
        catch( Exception x ){
            System.out.println( "Error while fetching and saving image: " + x );
        }
    }
    public void music( app a, String code ){
        System.out.println( "Audio on custom ascii encoding will be saved on file " + code + ".data." );
        boolean isAQ = code.substring( 5, 7 ).equals( "AQ" );
        
        if( !isAQ ){
            this.playmusic( this.fetchdpcm( a, code ), 8 );
        }
        else{
            this.playmusic( this.fetchaqdpcm( a, code ), 16 );
        }
    }
    public void playmusic( byte[] audio, int Q ){
        try{
            AudioFormat linearPCM = new AudioFormat( 8000, Q, 1, true, false );
            SourceDataLine lineOut = AudioSystem.getSourceDataLine( linearPCM );
            lineOut.open( linearPCM, 32000 );
            lineOut.start();
            lineOut.write( audio, 0, audio.length );
            lineOut.stop();
            lineOut.close();
        }
        catch( Exception x ){
            System.out.println( "Error while playing music: " + x );
        }
    }
    public byte[] fetchdpcm( app a, String code ){
        a.setCode( code );
        byte[] buff = new byte[ 128 * 2 ];
        byte[] audio = new byte[ SP * 2 * 128 ];
        int counter = 0;
        int nibble = 0;
        try{
            FileWriter fw = new FileWriter( code + ".data" );
            BufferedWriter bw = new BufferedWriter( fw );
            a.s.send( a.p );
            for( int j = 0; j < SP; ++j ){
                try{
                    a.r.receive( a.q );
                    buff = a.q.getData();
                    for( int i = 0; i < 128; ++i ){
                        int X1 = ( buff[ i ] >> 4 ) & 0x0f;
                        int X2 = buff[ i ] & 0x0f;
                         
                        X1 = X1 - 8;
                        X2 = X2 - 8;
                        
                        X1 += nibble;
                        if( X1 > 127 ){
                            X1 = 127;
                        }
                        if( X1 < -128 ){
                            X1 = -128;
                        }
                        X2 += X1;
                        if( X2 > 127 ){
                            X2 = 127;
                        }
                        if( X2 < -128 ){
                            X2 = -128;
                        }
                        nibble = X2;
                        
                        byte x1 = ( byte ) X1;
                        byte x2 = ( byte ) X2;
                        
                        audio[ counter++ ] = x1;
                        audio[ counter++ ] = x2;
                        
                        bw.write( X1 + " " );
                        bw.write( X2 + " " );
                    }
                }
                catch( Exception x ){
                    break;
                }
            }
            bw.close();
        }
        catch( Exception x ){
            System.out.println( "Error while fetching music: " + x );
        }
        return audio;
    }
    public byte[] fetchaqdpcm( app a, String code ){
        a.setCode( code );
        byte[] buff = new byte[ 132 * 2 ];
        byte[] audio = new byte[ SP * 4 * 128 ];
        int counter = 0;
        int nibble = 0;
        try{
            FileWriter fw1 = new FileWriter( code + "-log.data" );
            BufferedWriter log = new BufferedWriter( fw1 );
            FileWriter fw = new FileWriter( code + ".data" );
            BufferedWriter bw = new BufferedWriter( fw );
            a.s.send( a.p );
            for( int j = 0; j < SP; ++j ){
                try{
                    a.r.receive( a.q );
                    buff = a.q.getData();
                    
                    byte[] bb = new byte[ 4 ];
                    byte sign = (byte)( ( buff[ 1 ] & 0x80 ) != 0 ? 0xff : 0x00 );
                    bb[ 3 ] = sign;
                    bb[ 2 ] = sign;
                    bb[ 1 ] = buff[ 1 ];
                    bb[ 0 ] = buff[ 0 ];

                    int m = ByteBuffer.wrap( bb ).order( ByteOrder.LITTLE_ENDIAN ).getInt();
                    

                    sign = (byte)( ( buff[ 3 ] & 0x80 ) != 0 ? 0xff : 0x00 );
                    bb[ 3 ] = sign;
                    bb[ 2 ] = sign;
                    bb[ 1 ] = buff[ 3 ];
                    bb[ 0 ] = buff[ 2 ];

                    int b = ByteBuffer.wrap( bb ).order( ByteOrder.LITTLE_ENDIAN ).getInt();
                    log.write( m + " " + b + "\n" );
                    for( int i = 4; i < 132; ++i ){
                        int D1 = ( buff[ i ] >>> 4 ) & 0x0f;
                        int D2 = buff[ i ] & 0x0f;
                        
                        int d1 = D1 - 8;
                        int d2 = D2 - 8;
                        
                        int delta1 = d1 * b;
                        int delta2 = d2 * b;
                        
                        int X1 = delta1 + nibble;
                        int X2 = delta2 + delta1;
                        nibble = delta2;

                        int x1 = X1 + m;
                        int x2 = X2 + m;
                        
                        audio[ counter++ ] = ( byte ) ( x1 );
                        audio[ counter++ ] = ( byte ) ( x1 / 256 > 127 ? 127 : x1 / 256 < -128 ? -128 : x1 / 256 );
                        audio[ counter++ ] = ( byte ) ( x2 );
                        audio[ counter++ ] = ( byte ) ( x2 / 256 > 127 ? 127 : x2 / 256 < -128 ? -128 : x2 / 256 );
                        
                        bw.write( x1 + " " );
                        bw.write( x2 + " " );
                    }
                    
                }
                catch( Exception x ){
                    break;
                }
            }
            bw.close();
            log.close();
        }
        catch( Exception x ){
            System.out.println( "Error while fetching AQ-DPCM: " + x );
        }
        return audio;
    }

    public app( int packetsize ){
        hostIP = new byte[ 4 ];
        hostIP[ 0 ] = ( byte ) 155;
        hostIP[ 1 ] = ( byte ) 207;
        hostIP[ 2 ] = ( byte ) 18;
        hostIP[ 3 ] = ( byte ) 208;
        rxbuffer = new byte[ packetsize ];
        
        try{
            s = new DatagramSocket();
            r = new DatagramSocket( ClientPort );
            r.setSoTimeout( 4000 );
            hostAddress = InetAddress.getByAddress( hostIP );

            q = new DatagramPacket( rxbuffer, rxbuffer.length );

        }
        catch( Exception x ){
            System.out.println( "Error while initializing app: " + x );
        }
    }
    public void resizebuffer( app a, int size ){
        rxbuffer = new byte[ size ];
        try{
            q = new DatagramPacket( rxbuffer, rxbuffer.length );
        }
        catch( Exception x ){
            System.out.println( "Error while resizing buffer: " + x );
        }
    }
    public void setCode( String code ){
        txbuffer = code.getBytes();
        System.out.println( "code: " + code );
        try{
            p = new DatagramPacket( txbuffer, txbuffer.length, hostAddress, ServerPort );
        }
        catch( Exception x ){
            System.out.println( "Error while setting transmit code: " + x );
        }
    }
}
