package pl.maciejprogramuje.remove.polish.chars;

import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;

import java.io.File;
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

    private SimpleBooleanProperty spinnerVisibleProperty;
    private SimpleStringProperty enterLinkStringProperty;
    private SimpleBooleanProperty startButtonDisableProperty;
    private SimpleStringProperty messageStringProperty;


    public void initialize(URL location, ResourceBundle resources) {
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
        System.out.println("klik");

        messageStringProperty.setValue("");
        final String path = enterLinkStringProperty.getValue();
        if (path.isEmpty()) {
            messageStringProperty.setValue("Unknown path!");
        } else {
            spinnerVisibleProperty.setValue(true);
            startButtonDisableProperty.setValue(true);

            final Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() {
                    System.out.println("path=" + path);

                    readNamesOfFiles(path);

                    return null;
                }
            };

            task.setOnSucceeded(event -> {
                spinnerVisibleProperty.setValue(false);
                startButtonDisableProperty.setValue(false);
                messageStringProperty.setValue("Done");
            });

            Thread thread = new Thread(task);
            thread.start();
        }
    }

    public void readNamesOfFiles(String rootPath) {
        File directory = new File(rootPath);
        File[] files = directory.listFiles();
        if (files != null) {
            for (final File f : files) {
                if (f.isFile()) {
                    System.out.println(f.getName() + "," + f.getAbsolutePath() + "," + f.getParent());

                    Platform.runLater(() -> messageStringProperty.setValue(f.getName()));
                } else if (f.isDirectory()) {
                    try {
                        readNamesOfFiles(f.getAbsolutePath());
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }
            }
        }
    }
}
