/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatclient;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *
 * @author William
 */
public class ChatClient extends Application {
    public static Stage primaryStage;
    @Override
    public void start(Stage stage) throws Exception {
        System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        primaryStage = stage;
        Parent root = FXMLLoader.load(getClass().getResource("ChatScreen.fxml"));
        
        Scene scene = new Scene(root);
        stage.setResizable(false);
        stage.setScene(scene);
        //stage.initOwner(primaryStage);
      //  stage.initModality(Modality.WINDOW_MODAL);
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
        
    }
    
}
