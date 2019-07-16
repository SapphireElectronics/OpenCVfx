package sample;

import org.opencv.core.*;
import org.opencv.imgproc.Moments;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.abs;
import static org.opencv.core.Core.bitwise_not;
import static org.opencv.highgui.HighGui.*;
import static org.opencv.highgui.HighGui.imshow;
import static org.opencv.imgcodecs.Imgcodecs.imread;
import static org.opencv.imgcodecs.Imgcodecs.imwrite;
import static org.opencv.imgproc.Imgproc.*;
import static org.opencv.imgproc.Imgproc.drawContours;

public class Ident implements Serializable {
    transient private Mat srcInit;
    transient private Mat srcBW;
    transient private Mat junctions;
    transient private Mat overlay;

    public List<Point> junctionsList = new ArrayList<>();

    public void init() {
        // load the source image
        srcInit = imread("dat\\test.png" );

        // generate a copy of the source onto which overlay data is placed
        overlay = srcInit.clone();

        // generate a binary version of the source that is white on a black background
        srcBW = new Mat();
        cvtColor( srcInit, srcBW, COLOR_BGR2GRAY );
        bitwise_not( srcBW, srcBW );
        adaptiveThreshold( srcBW, srcBW, 255, ADAPTIVE_THRESH_MEAN_C, THRESH_BINARY, 15, -2);
    }

    public final static int AMOUNT = -1;

    public void junction( int size ) {
        junctions = srcBW.clone();

        Mat js = getStructuringElement( MORPH_RECT, new Size( size, size ));

        erode( junctions, junctions, js, new Point( AMOUNT, AMOUNT ));
        dilate( junctions, junctions, js, new Point( AMOUNT, AMOUNT ));

        List<MatOfPoint> contours = new ArrayList<>();
        Mat heir = new Mat();

        findContours( junctions, contours, heir, RETR_TREE, CHAIN_APPROX_SIMPLE );
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
//            circle( juncMat, junctions.get(i++), 5, new Scalar(0,0,255),1,8);
        }

        System.out.println( "Moments:" + junctions );
        updateJunctions();
    }

    public void updateJunctions()
    {
        overlay = srcInit.clone();
        for( Point junc : junctionsList ) {
            circle( overlay, junc, 5, new Scalar(0,0,255),1,8);
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

    public Mat getJunctions() {
        return junctions;
    }

    public Mat getOverlay() {
        return overlay;
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
