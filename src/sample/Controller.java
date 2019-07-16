package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.*;

import static org.opencv.core.Core.bitwise_not;
import static org.opencv.imgcodecs.Imgcodecs.imread;
import static org.opencv.imgcodecs.Imgcodecs.imwrite;

public class Controller {
    private Ident id;
    private double scale;

    public void init()
    {
        id = new Ident();
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
    protected void junctionStage() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("junction.fxml"));
        Parent root = null;
        try {
            root = (BorderPane) loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Stage junctionStage = new Stage();
        junctionStage.setScene( new Scene( root, 600, 400 ));
        junctionStage.show();

        JuncController controller = loader.getController();
        controller.init( this.id, this );
    }

    protected void load() {
        this.id.init();

        Utils.onFXThread( srcImage.imageProperty(), Utils.mat2Image( this.id.getOverlay() ) );

        // calculate and save the image scaling
        double xs = srcImage.getFitWidth() / id.getOverlay().cols();
        double ys = srcImage.getFitHeight() / id.getOverlay().rows();
        this.scale = Math.min( 1.0, Math.min( xs, ys ) );

        findJunctions();
    }

    protected void findJunctions() {
        int sz = (int) juncSlider.getValue();
        this.id.junction( sz );
        Utils.onFXThread( currentFrame.imageProperty(), Utils.mat2Image( this.id.getJunctions() ) );
    }

    protected void updateJunctions(){
        this.id.updateJunctions();
        Utils.onFXThread( srcImage.imageProperty(), Utils.mat2Image( id.getOverlay() ) );
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
