package sample;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class LineController {
    private Ident id;
    private Controller controller;

    public void init( Ident id, Controller controller )
    {
        this.id = id;
        this.controller = controller;
        load();
    }
    @FXML
    private ImageView image;

    @FXML
    private Slider param1;

    @FXML
    private Slider param2;

    @FXML
    private Slider param3;

    @FXML
    private Slider param4;

    @FXML
    private Label param1label;

    @FXML
    private Label param2label;

    @FXML
    private Label param3label;

    @FXML
    private Label param4label;

    @FXML
    protected void param1update(MouseEvent scrollEvent) {
    }
    @FXML
    protected void param2update(MouseEvent scrollEvent) {
    }
    @FXML
    protected void param3update(MouseEvent scrollEvent) {
    }
    @FXML
    protected void param4update(MouseEvent scrollEvent) {
    }

    public void load() {
        Utils.onFXThread( image.imageProperty(), Utils.mat2Image(this.id.getJunctions()));
        findJunctions();
    }

    protected void findJunctions() {
        int sz = (int) param1.getValue();
        this.id.junction( sz );
        Utils.onFXThread( image.imageProperty(), Utils.mat2Image( this.id.getJunctions() ) );
    }
}
