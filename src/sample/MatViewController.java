package sample;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;

import java.util.ArrayList;
import java.util.List;

import static org.opencv.core.Core.*;

public class MatViewController {
    private Ident id;
    private Controller controller;

    private Mat mat[] = new Mat[4], display;

    private boolean visible[] = new boolean[4], negative[] = new boolean[4], only[] = new boolean[4];


    public void init( Controller controller )
    {
        this.controller = controller;
        this.mat = new Mat[4];
        for( int i=0; i<4; i++ )
            this.mat[i] = new Mat();
        this.display = new Mat();

    }

    @FXML private ImageView view;

    @FXML private Label image0label;
    @FXML private Label image1label;
    @FXML private Label image2label;
    @FXML private Label image3label;

    @FXML private CheckBox visible0, visible1, visible2, visible3;
    @FXML private CheckBox negative0, negative1, negative2, negative3;
    @FXML private CheckBox only0, only1, only2, only3;

    @FXML private void visible0click() { this.visible[0] = visible0.isSelected(); update(); };
    @FXML private void visible1click() { visible[1] = visible1.isSelected(); update(); };
    @FXML private void visible2click() { visible[2] = visible2.isSelected(); update(); };
    @FXML private void visible3click() { visible[3] = visible3.isSelected(); update(); };

    @FXML private void negative0click() { this.negative[0] = negative0.isSelected(); update(); };
    @FXML private void negative1click() { negative[1] = negative1.isSelected(); update(); };
    @FXML private void negative2click() { negative[2] = negative2.isSelected(); update(); };
    @FXML private void negative3click() { negative[3] = negative3.isSelected(); update(); };

    @FXML private void only0click() { only[0] = only0.isSelected(); update(); };
    @FXML private void only1click() { only[1] = only1.isSelected(); update(); };
    @FXML private void only2click() { only[2] = only2.isSelected(); update(); };
    @FXML private void only3click() { only[3] = only3.isSelected(); update(); };

    @FXML private void animateClick() {};


    public void setMat( int index, Mat newMat ) {
        this.mat[index] = newMat;
    }

    public void update() {
        this.display = new Mat();

        if( this.visible[0] ) {
            this.display = this.mat[0].clone();

            if( this.negative[0] ) {
                List<Mat> channels = new ArrayList<>();
                split(this.display, channels);
                for (Mat channel : channels)
                    bitwise_not( channel, channel );
//                    subtract(new Scalar(255), channel, channel);
                merge(channels, this.display);
            }
        }

        Utils.onFXThread( view.imageProperty(), Utils.mat2Image( this.display ) );
    }

}
