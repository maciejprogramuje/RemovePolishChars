package pl.maciejprogramuje.remove.polish.chars;

import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    @FXML
    public ProgressIndicator spinner;

    @FXML
    public TextField enterLinkTextField;

    @FXML
    private Button startButton;

    @FXML
    private Label messageLabel;

    MainControllerUtils mainControllerUtils;

    private Property<Boolean> spinnerVisibleProperty;
    private Property<String> enterLinkStringProperty;
    private Property<Boolean> startButtonDisableProperty;
    private Property<String> messageStringProperty;

    public void initialize(URL location, ResourceBundle resources) {
        bindProperties();

        mainControllerUtils = new MainControllerUtils(spinnerVisibleProperty,
                enterLinkStringProperty,
                startButtonDisableProperty,
                messageStringProperty);
    }

    private void bindProperties() {
        spinnerVisibleProperty = new SimpleBooleanProperty(false);
        spinner.visibleProperty().bind(spinnerVisibleProperty);

        enterLinkStringProperty = new SimpleStringProperty();
        enterLinkTextField.textProperty().bindBidirectional(enterLinkStringProperty);

        startButtonDisableProperty = new SimpleBooleanProperty();
        startButton.disableProperty().bind(startButtonDisableProperty);

        messageStringProperty = new SimpleStringProperty("");
        messageLabel.textProperty().bind(messageStringProperty);
    }

    public void handleStartButtonAction() {
        mainControllerUtils.startButtonAction();
    }
}
