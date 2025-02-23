package com.example.project;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventTarget;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.scene.web.HTMLEditor;

import java.awt.*;
import java.io.File;
import javax.swing.JFileChooser;

public class Controller {
    @FXML
            private TabPane tabPane;
    @FXML
            private SplitPane splitPane;
    @FXML
            private Button SBCloseBtn;
    @FXML
            private AnchorPane rightPane;

    public void initialize() {
        rightPane.layoutXProperty().addListener((obs, oldX, newX) -> {
            SBCloseBtn.setLayoutX(newX.doubleValue() - 30.0);
        });
    }

    Stage stage = new Stage();
    int clickCount = 0;

    public void newFile() {
        BorderPane root = new BorderPane();
        TextField txtField = new TextField();
        Button submitBtn = new Button("submit");

        submitBtn.setOnMouseClicked(event -> {
            String fileName = txtField.getText();
            if (!fileName.isEmpty()) {
                Tab tab = new Tab(fileName);
                HTMLEditor editor = new HTMLEditor();

                editor.setPrefSize(600.0, 700.0);
                tab.setContent(editor);

                Platform.runLater(() -> tabPane.getTabs().add(tab));
            }
            stage.close();
        });

        root.setCenter(txtField);
        root.setBottom(submitBtn);

        stage.setScene(new Scene(root));
        stage.show();
    }
    public void openFile(ActionEvent event) {
        String buttonText = ((Button) event.getSource()).getText();

        try {
            JFileChooser fileChooser = new JFileChooser();

            if (buttonText.equals("open file")) {
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            } else if (buttonText.equals("open folder")) {
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            }
            fileChooser.setCurrentDirectory(new File("C:/"));

            fileChooser.setBackground(Color.white);

            int result = fileChooser.showOpenDialog(null);

            System.out.println("result: " + result);

            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = new File(fileChooser.getSelectedFile().getAbsolutePath());
                System.out.println("file path: " + selectedFile);

                if (!Desktop.isDesktopSupported()) {
                    System.out.println("file not supported");
                } else {
                    if (buttonText.equals("open file")) {
                        Tab tab = new Tab(selectedFile.getName());
                        HTMLEditor editor = new HTMLEditor();

                        editor.setPrefSize(600.0, 700.0);

                        tab.setContent(editor);

                        Platform.runLater(() -> tabPane.getTabs().add(tab));
                    } else if (buttonText.equals("open folder")) {
                        Desktop desktop = Desktop.getDesktop();
                        desktop.open(selectedFile);
                    }
                }
            }else if (result == JFileChooser.CANCEL_OPTION) {
                System.out.println("cancelled");
            }
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }
    public void SBClose() {
        if (clickCount % 2 == 0)
            splitPane.setDividerPosition(0, 1);
        else
            splitPane.setDividerPosition(0, .70);
        clickCount++;
    }
}