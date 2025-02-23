package com.example.project;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    public static void main(String[] args) { launch(); }

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            Parent loader = FXMLLoader.load(getClass().getResource("/com/example/project/interface.fxml"));
            Scene scene = new Scene(loader);

            primaryStage.setTitle("kevin");
            primaryStage.setResizable(false);

            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}