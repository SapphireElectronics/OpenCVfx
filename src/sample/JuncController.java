package sample;

import javafx.fxml.FXML;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class JuncController {
    private Ident id;
    private Controller controller;

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
        Utils.onFXThread( juncImage.imageProperty(), Utils.mat2Image(this.id.getJunctionMat()));
        findJunctions();
    }

    protected void findJunctions() {
        int sz = (int) sizeSlider.getValue();
        this.id.findJunctions( sz );
        Utils.onFXThread( juncImage.imageProperty(), Utils.mat2Image( this.id.getJunctionMat() ) );
    }
}
