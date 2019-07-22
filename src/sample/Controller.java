package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Slider;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TitledPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.*;

import static org.opencv.core.Core.bitwise_not;
import static org.opencv.imgcodecs.Imgcodecs.imread;
import static org.opencv.imgcodecs.Imgcodecs.imwrite;

public class Controller {
    private Ident id;
    private double scale;

    private MatViewController matViewController;

    public void init()
    {
        id = new Ident();
        this.matViewController = newStage( "MatView.fxml", 2 ).getController();
        this.matViewController.init( this );
        load();
    }

    @FXML
    private Slider juncSlider;

    @FXML
    private ImageView srcImage;

    @FXML
    private ImageView currentFrame;

    @FXML
    protected void juncUpdate(MouseEvent scrollEvent) {

        findJunctions();
    }

    @FXML
    protected void toggleObject(MouseEvent scrollEvent) {
        System.out.println( "Click:[" + scrollEvent.getX() + "," + scrollEvent.getY() + "]" );

        int x = (int) (scrollEvent.getX() / this.scale );
        int y = (int) (scrollEvent.getY() / this.scale );

        id.toggleJunction( x, y );
        updateJunctions();
    }

    @FXML
    protected void juncIdent() {
        findJunctions();
        updateJunctions();
    }

    @FXML
    protected void juncErase() {
        id.eraseJunctions( (int) juncSlider.getValue());
        Utils.onFXThread( srcImage.imageProperty(), Utils.mat2Image( id.getCombiMat() ) );
    }

    @FXML
    protected void junctionStage() {
        JuncController juncController = newStage( "junction.fxml" ).getController();
        juncController.init( this.id, this );

        LineController lineController = newStage( "LineCont.fxml", 3 ).getController();
        lineController.init( this.id, this );
    }

    private FXMLLoader newStage( String name, int type ) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(name ));
        Parent root = null;
        try {
            switch( type ) {
                case 1:
                    root = (BorderPane) loader.load();
                    break;
                case 2:
                    root = (TitledPane) loader.load();
                    break;
                case 3:
                    root = (SplitPane) loader.load();
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Stage stage = new Stage();
        stage.setScene( new Scene( root, 600, 400 ));
        stage.show();

        return loader;

    }

    private FXMLLoader newStage( String name ) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(name ));
        Parent root = null;
        try {
            root = (BorderPane) loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Stage stage = new Stage();
        stage.setScene( new Scene( root, 600, 400 ));
        stage.show();

        return loader;
    }


    protected void load() {
        this.id.init();

        Utils.onFXThread( srcImage.imageProperty(), Utils.mat2Image( this.id.getOverlayMat() ) );

        // calculate and save the image scaling
        double xs = srcImage.getFitWidth() / id.getOverlayMat().cols();
        double ys = srcImage.getFitHeight() / id.getOverlayMat().rows();
        this.scale = Math.min( 1.0, Math.min( xs, ys ) );

        findJunctions();
    }

    protected void findJunctions() {


        int sz = (int) juncSlider.getValue();
        this.id.findJunctions( sz );
        Utils.onFXThread( currentFrame.imageProperty(), Utils.mat2Image( this.id.getJunctionMat() ) );

        if( matViewController != null )
            matViewController.setMat( 0, this.id.getJunctionMat() );
    }

    protected void updateJunctions(){
        this.id.drawJunctions();
        Utils.onFXThread( srcImage.imageProperty(), Utils.mat2Image( id.getOverlayMat() ) );
    }

    @FXML
    protected void writeIdent()
    {
        try {
            FileOutputStream fos = new FileOutputStream( new File( "ident.txt") );
            ObjectOutputStream oos = new ObjectOutputStream( fos );
            this.id.writeObject( oos );
            oos.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void readIdent()
    {
        try {
            FileInputStream fis = new FileInputStream( "ident.txt");
            ObjectInputStream ois = new ObjectInputStream( fis );
            System.out.println( "Avail:" + ois.available() );
            this.id.readObject( ois );
            ois.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        updateJunctions();
    }





}
