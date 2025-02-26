package com.example.project;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.web.HTMLEditor;

import java.awt.Desktop;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import javax.swing.JFileChooser;
import javax.swing.text.html.HTML;

public class Controller {
    @FXML
    private TabPane tabPane;
    @FXML
    private SplitPane splitPane;
    @FXML
    private Button SBCloseBtn;
    @FXML
    private AnchorPane rightPane;

    int clickCount = 0;

    public void initialize() {
        rightPane.layoutXProperty().addListener((_, _, newX) ->
                SBCloseBtn.setLayoutX(newX.doubleValue() - 30.0)
        );
    }

    public Menu getFileMenu(EventHandler eventHandler1, EventHandler eventHandler2) {
        Menu file = new Menu("file");
        MenuItem save = new MenuItem("save");
        MenuItem saveAs = new MenuItem("save as");

        saveAs.setOnAction(eventHandler1);
        save.setOnAction(eventHandler2);

        file.getItems().addAll(
                save,
                saveAs
        );
        return file;
    }

    public void openSaveFileChooser(Stage stage, File file) {
        FileChooser fileChooser = new FileChooser();

        fileChooser.setTitle("save file");
        fileChooser.showSaveDialog(stage);

        if (file != null) {
            try (FileWriter writer = new FileWriter(file)) {
                writer.write("Hello, this is the saved content!");
                System.out.println("File saved: " + file.getAbsolutePath());
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public void newFile() {
        Stage stage = new Stage();

        // open the popup
        // the root node
        BorderPane root = new BorderPane();

        // the input of the user (the name of the file)
        TextField textField = new TextField();
        textField.setPromptText("file name");

        // the submit button
        Button submit = new Button("submit");

        // adding an event handler for the submit button so that it would do its job
        submit.setOnAction(_ -> {
            // saving the file
            FileChooser fileChooser = new FileChooser();
            fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("text files", "*.txt"));
            File file = fileChooser.showSaveDialog(stage);

            if (file != null) {
                try {
                    if (file.createNewFile()) {
                        System.out.println("file created successfully");
                    } else {
                        System.out.println("file already exists");
                    }
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            } else {
                System.out.println("file is null");
            }

            // adding the tab
            String fileName = textField.getText();
            if (!fileName.isEmpty()) {
                Group tabRoot = new Group();
                HTMLEditor editor = new HTMLEditor();
                Tab tab = new Tab(fileName);

                editor.setLayoutY(30.0);

                tabRoot.getChildren().add(editor);
                tabRoot.getChildren().add(
                        new MenuBar(
                        getFileMenu(
                                _ -> openSaveFileChooser(stage, file),
                                _ -> saveFile(tab, file)
                        ))
                );

                editor.setPrefSize(600.0, 700.0);
                tab.setContent(tabRoot);

                Platform.runLater(() -> tabPane.getTabs().add(tab));
            }
            stage.close();
        });

        // adding the nodes to the root node
        root.setCenter(textField);
        root.setBottom(submit);

        // setting the scene
        stage.setScene(new Scene(root));
        stage.show();
    }

    // function to open the file
    public void openFile(ActionEvent event) {
        String buttonText = ((Button) event.getSource()).getText(); // it gets the text of the button that clicked it
        // because there are two buttons who call this function (open file and open folder)

        // we create a new file chooser
        JFileChooser fileChooser = new JFileChooser();
        // setting the filter in function of which button clicks it
        if (buttonText == "open file") {
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        } else {
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        }

        int result = fileChooser.showOpenDialog(null);
        File selectedFile = new File(fileChooser.getSelectedFile().getAbsolutePath());

        // what happens after we click the approve option
        if (result == JFileChooser.APPROVE_OPTION) {
            if (buttonText.equals("open file")) { // if we want to open file
                // we add a new tab with the chosen file name
                Tab tab = new Tab(selectedFile.getName());
                tab.setClosable(true);

                HTMLEditor editor = new HTMLEditor();
                editor.setPrefSize(
                        600.0,
                        700.0
                );

                Group root = new Group();
                root.getChildren().addAll(
                        new MenuBar(
                                getFileMenu(
                                        _ -> System.out.println("save"),
                                        _ -> openSaveFileChooser(null, selectedFile)
                                )
                        ),
                        editor
                );
                tab.setContent(root);

                tabPane.getTabs().add(tab);
            } else { // if we want to open folder
                try {
                    Desktop desktop = Desktop.getDesktop();
                    desktop.open(selectedFile);
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        } else if (result == JFileChooser.CANCEL_OPTION) { // for if we click on cancel
            System.out.println("cancelled");
        }
    }

    public void SBClose() {
        if (clickCount % 2 == 0)
            splitPane.setDividerPosition(0, 1);
        else
            splitPane.setDividerPosition(0, .70);
        clickCount++;
    }
    public void saveFile(Tab tab, File file) {
        if (file == null) {
            System.out.println("no file selected");
            return;
        }

        Node content = tab.getContent();
        HTMLEditor editor = findHTMLEditor(content);

        if (editor == null) {
            System.out.println("could not find HTML editor");
            return;
        }

        try (FileWriter writer = new FileWriter(file)) {
            String text = extractPlainText(editor.getHtmlText());
            writer.write(text);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
    private HTMLEditor findHTMLEditor(Node node) {
        if (node instanceof HTMLEditor) {
            return (HTMLEditor) node;
        }
        if (node instanceof Parent) {
            for (Node child : ((Parent) node).getChildrenUnmodifiable()) {
                HTMLEditor editor = findHTMLEditor(child);
                if (editor != null) {
                    return editor;
                }
            }
        }
        return null;
    }
    private String extractPlainText(String htmlContent) {
        return htmlContent.replaceAll("<[^>]*>", "").trim();
    }
}