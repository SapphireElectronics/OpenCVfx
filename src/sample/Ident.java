package sample;

import org.opencv.core.*;
import org.opencv.imgproc.Moments;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.abs;
import static java.lang.Math.max;
import static org.opencv.core.Core.bitwise_not;
import static org.opencv.imgcodecs.Imgcodecs.imread;
import static org.opencv.imgcodecs.Imgcodecs.imwrite;
import static org.opencv.imgproc.Imgproc.*;
import static org.opencv.imgproc.Imgproc.drawContours;

public class Ident implements Serializable {
    transient private Mat srcInit;
    transient private Mat srcBW;
    transient private Mat junctionMat;
    transient private Mat textMat;
    transient private Mat overlayMat;

    transient public List<Point> junctionsList = new ArrayList<>();

    public void init() {
        // load the source image
        srcInit = imread("dat\\test.png" );

        // generate a copy of the source onto which overlayMat data is placed
        overlayMat = srcInit.clone();
        junctionMat = srcInit.clone();
        textMat = srcInit.clone();

        // generate a binary version of the source that is white on a black background
        srcBW = new Mat();
        cvtColor( srcInit, srcBW, COLOR_BGR2GRAY );
        bitwise_not( srcBW, srcBW );
        adaptiveThreshold( srcBW, srcBW, 255, ADAPTIVE_THRESH_MEAN_C, THRESH_BINARY, 15, -2);
    }

    public final static int AMOUNT = -1;

    public void findJunctions(int size ) {
        junctionMat = srcBW.clone();

        Mat js = getStructuringElement( MORPH_RECT, new Size( size, size ));

        erode(junctionMat, junctionMat, js, new Point( AMOUNT, AMOUNT ));
        dilate(junctionMat, junctionMat, js, new Point( AMOUNT, AMOUNT ));

        List<MatOfPoint> contours = new ArrayList<>();
        Mat heir = new Mat();

        findContours(junctionMat, contours, heir, RETR_TREE, CHAIN_APPROX_SIMPLE );
        System.out.println( "Contours: " + contours.size() );

        junctionsList.clear();

        int i = 0;
        int cx, cy;
        for( MatOfPoint cont : contours ) {
            Moments moments = moments( cont );
            cx = (int) ( moments.m10 / moments.m00 );
            cy = (int) ( moments.m01 / moments.m00 );
            junctionsList.add( new Point( cx, cy ) );
//            System.out.println( "Moment[" + i++ + "]: (" + cx + "," + cy + ")" );
//            circle( juncMat, junctionMat.get(i++), 5, new Scalar(0,0,255),1,8);
        }

        System.out.println( "Moments:" + junctionMat);
        drawJunctions();
    }

    public void findText( int p1, int p2, int p3, int p4 )
    {
        textMat = srcBW.clone();

        p1 = max( p1, 1);
        p2 = max( p2, 1);
        p3 = max( p3, 1);
        p4 = max( p4, 1);

        Mat se = getStructuringElement( MORPH_RECT, new Size( p1, p2 ));

        Point anchor = new Point(-1, -1);

//        erode(textMat, textMat, se, anchor );
//        dilate(textMat, textMat, se, anchor );

        se = getStructuringElement( MORPH_RECT, new Size( p3, p4 ));
        System.out.println( "Before: " + se.dump() );
        Scalar scale = new Scalar(( p3 * p4 ) / 3.0 );

        System.out.println( "x:" + p3 + " y:" + p4 + " scale:" + ( p3 * p4 ) / 3.0 );
//        Core.multiply( se, scale, se);
        System.out.println( "After: " + se.dump() );

        double delta =  1 -( p3 * p4 );
        System.out.println( "Delta: " + delta );

        // convert to binary with values 0,1 for each pixel
        threshold( textMat, textMat, 127, 1, THRESH_BINARY );

        filter2D(textMat, textMat, -1, se );

        // the max value of each pixel will now be p3 x p4, so threshold at 1/2 that
        int thresh = p3 * p4 * p2 / 25;
//        int thresh = p3 * p4 / (p2 / 5);
        System.out.println( "Max: " + p3 * p4 + "  Thresh: " + thresh );
        threshold( textMat, textMat, thresh, 255, THRESH_BINARY );

    }

    public void drawJunctions()
    {
        overlayMat = srcInit.clone();
        for( Point junc : junctionsList ) {
            circle(overlayMat, junc, 5, new Scalar(0,0,255),1,8);
        }
    }

    public void eraseJunctions( int size )
    {
        int x,y;
        for( Point junc : junctionsList ) {
            x = (int) junc.x;
            y = (int) junc.y;
            double[] c = { 0, 255, 0 };

            for( int i=0; i<size/2; i++ ) {
                for( int j=-size/2; j<size/2; j++ ) {
                    System.out.println( "size:(x,y)" + size + ":(" + x + "," + y + ")" );
                    System.out.println( "Array" + overlayMat.get( x, y ).length );
//                    overlayMat.put( x-i, y-j, overlayMat.get( x-i, y ));
//                    overlayMat.put( x-i, y+j, overlayMat.get( x-i, y ));
                    overlayMat.put( y-j, x-i, c );
                    overlayMat.put( y+j, x-i, c );
                }
            }
        }
    }

    public void toggleJunction( int x, int y )
    {
        boolean found = false;
        for( Point junc : junctionsList ) {
            if (abs(junc.x - x) < 7 && abs(junc.y - y) < 7) {
                junctionsList.remove(junc);
                found = true;
                break;
            }
        }
        if( !found )
            junctionsList.add( new Point( x, y) );
    }

    public Mat getJunctionMat() {
        return junctionMat;
    }

    public Mat getOverlayMat() {
        return overlayMat;
    }

    public Mat getTextMat() {
        return textMat;
    }

    public List<Point> getJunctionList() {
        return junctionsList;
    }

    public void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException
    {
        junctionsList.clear();
        System.out.println( "Avail:" + ois.available() );

        int jcns = ois.readInt();
        for( int i=0; i<jcns; i++ )
        {
            junctionsList.add( new Point( ois.readDouble(), ois.readDouble() ));
        }
    }

    public void writeObject(ObjectOutputStream oos) throws IOException, ClassNotFoundException
    {
        oos.writeInt( junctionsList.size() );

        for( Point junc : junctionsList ) {
            oos.writeDouble( junc.x );
            oos.writeDouble( junc.y );
        }
    }

}
