package pl.maciejprogramuje.remove.polish.chars;

import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.concurrent.Task;

import java.io.File;

import static pl.maciejprogramuje.remove.polish.chars.MainController.ARABIC_CHARS;
import static pl.maciejprogramuje.remove.polish.chars.MainController.POLISH_CHARS;

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
                    String outputString = replacePolishChars(f.getName());
                    if (outputString != null) {
                        renameDirOrFile(f, f.getParent() + "\\" + outputString);
                    }

                    Platform.runLater(() -> messageStringProperty.setValue(f.getName()));
                } else if (f.isDirectory()) {
                    try {
                        readNamesOfFiles(f.getAbsolutePath());
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }
            }

            for (final File f : files) {
                if (f.isDirectory()) {
                    try {
                        String outputString = replacePolishChars(f.getAbsolutePath());
                        if (outputString != null) {
                            renameDirOrFile(f, outputString);
                        }

                        readNamesOfFiles(f.getAbsolutePath());
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }
            }
        }
    }

    private void renameDirOrFile(File fileToChange, String outputString) {
        System.out.println("renamed=" + outputString);
        fileToChange.renameTo(new File(outputString));
    }

    private String replacePolishChars(String inputString) {
        char[] inputChars = inputString.toCharArray();

        for (int i = 0; i < inputChars.length; i++) {
            for (int j = 0; j < POLISH_CHARS.length; j++) {
                if (inputChars[i] == POLISH_CHARS[j]) {
                    inputChars[i] = ARABIC_CHARS[j];
                    return new String(inputChars);
                }
            }
        }

        return null;
    }
}
