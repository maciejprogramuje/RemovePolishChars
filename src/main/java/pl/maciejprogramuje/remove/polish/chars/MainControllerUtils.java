package pl.maciejprogramuje.remove.polish.chars;

import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.concurrent.Task;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MainControllerUtils {
    private final Property<Boolean> spinnerVisibleProperty;
    private final Property<String> enterLinkStringProperty;
    private final Property<Boolean> startButtonDisableProperty;
    private final Property<String> messageStringProperty;

    public MainControllerUtils(Property<Boolean> spinnerVisibleProperty,
                               Property<String> enterLinkStringProperty,
                               Property<Boolean> startButtonDisableProperty,
                               Property<String> messageStringProperty) {
        this.spinnerVisibleProperty = spinnerVisibleProperty;
        this.enterLinkStringProperty = enterLinkStringProperty;
        this.startButtonDisableProperty = startButtonDisableProperty;
        this.messageStringProperty = messageStringProperty;
    }

    public void startButtonAction() {
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
                    try {
                        readNamesOfFiles(path);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

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


    public void readNamesOfFiles(String rootPath) throws IOException {
        File directory = new File(rootPath);
        File[] files = directory.listFiles();
        if (files != null) {
            for (final File f : files) {
                if (f.isFile()) {
                    modifyFile(f, "<SLA>50000</SLA>", "<SLA>${#Project#maxResponseTime}</SLA>");

                    Platform.runLater(() -> messageStringProperty.setValue(f.getName()));
                } else if (f.isDirectory()) {
                    readNamesOfFiles(f.getAbsolutePath());
                }
            }
        }
    }

    private void modifyFile(File file, String oldString, String newString) throws IOException {
        String data = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
        data = data.replace(oldString, newString);
        FileUtils.writeStringToFile(file, data, StandardCharsets.UTF_8);
    }
}

