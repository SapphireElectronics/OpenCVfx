package sample;

import javafx.fxml.FXML;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class JuncController {
    private Ident id;
    private Controller controller;
    private double scale;

    public void init( Ident id, Controller controller )
    {
        this.id = id;
        this.controller = controller;
        load();
    }
    @FXML
    private ImageView juncImage;

    @FXML
    private Slider sizeSlider;

    @FXML
    protected void transferPress() {
        findJunctions();
        controller.updateJunctions();
    }

    @FXML
    protected void closePress() {

    }

    @FXML
    protected void sizeUpdate(MouseEvent scrollEvent) {
        findJunctions();
    }


    public void load() {
        Utils.onFXThread( juncImage.imageProperty(), Utils.mat2Image(this.id.getJunctions()));

        // calculate and save the image scaling
        double xs = juncImage.getFitWidth() / id.getOverlay().cols();
        double ys = juncImage.getFitHeight() / id.getOverlay().rows();
        this.scale = Math.min(1.0, Math.min(xs, ys));

        findJunctions();
    }

    protected void findJunctions() {
        int sz = (int) sizeSlider.getValue();
        this.id.junction( sz );
        Utils.onFXThread( juncImage.imageProperty(), Utils.mat2Image( this.id.getJunctions() ) );
    }

}
