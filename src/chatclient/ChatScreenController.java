/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatclient;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.StringPropertyBase;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *
 * @author William
 */
public class ChatScreenController implements Initializable {

    Socket connected;
    DataOutputStream dos;
    static int id;
    static String name = "William";
    static String message = "";
    static Task task;
    public static ObservableList<String> people = FXCollections.observableArrayList();

    @FXML
    Label lChat;
    @FXML
    TextField tfSend;
    @FXML
    Button btnSend;
    @FXML
    TextFlow tflowChat = new TextFlow();
    @FXML
    MenuItem miSetupConnection;
    @FXML
    ScrollPane spChat;
    @FXML
    AnchorPane anchorChat;
    @FXML
    ListView lvPerson;

    @FXML
    private void sendMessage(ActionEvent event) {
        Date date = new Date();
        DateFormat format = new SimpleDateFormat("HH:mm");
        String time = format.format(date);
        message = "id:" + id + "/" + "name:" + name + "/msg:" + tfSend.getText() + "/time:" + time;
        try {
            dos.writeUTF(message);
            System.out.println(message);
            dos.flush();
        } catch (IOException ex) {
            Logger.getLogger(ChatScreenController.class.getName()).log(Level.SEVERE, null, ex);
        }
        String buildMsg = name + "[" + time + "]: " + tfSend.getText() + "\r\n";
        Text msg = new Text(buildMsg);
        msg.setFill(Color.BLUE);
        tflowChat.getChildren().add(msg);

    }

    @FXML
    private void connect(ActionEvent event) {
        connected = tryConnection("localhost");

        ListenerHandler rh = new ListenerHandler(tflowChat, connected, lvPerson);
        rh.start();
        message = "registration:-" + name;

        try {
            dos = new DataOutputStream(connected.getOutputStream());
            dos.writeUTF(message);
            System.out.println(message);
            dos.flush();

        } catch (IOException ex) {
            Logger.getLogger(ChatScreenController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @FXML
    private void setConnection(ActionEvent event) {
        Parent connectionScreen;
        try {

            connectionScreen = FXMLLoader.load(getClass().getResource("ConnectionScreen.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Setteup Connection");
            stage.setScene(new Scene(connectionScreen));
            stage.setResizable(false);
            stage.initOwner(ChatClient.primaryStage);
            stage.initModality(Modality.WINDOW_MODAL);

            stage.showAndWait();

            Connection connection = Connection.getInstance();
            if (!connection.getIp().equals("")) {
                System.out.println("connectining");
                connect(connection.getIp(), connection.getName());
            } else {
                System.out.println("do Nothing");
            }

        } catch (IOException ex) {
            Logger.getLogger(ChatScreenController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private Socket tryConnection(String ip) {
        Socket socket = null;
        try {
            socket = new Socket(ip, 9000);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return socket;
    }

    private void connect(String ip, String myName) {
        connected = tryConnection(ip);

        ListenerHandler rh = new ListenerHandler(tflowChat, connected, lvPerson);
        rh.start();
        name = myName;
        message = "registration:-" + name;

        try {
            dos = new DataOutputStream(connected.getOutputStream());
            dos.writeUTF(message);
            System.out.println(message);
            dos.flush();

        } catch (IOException ex) {
            Logger.getLogger(ChatScreenController.class.getName()).log(Level.SEVERE, null, ex);
        }
        btnSend.setDisable(false);
        miSetupConnection.setDisable(true);

    }
//    public static void changeList(ObservableList online){
//          task = new Task() {
//            @Override
//            protected Object call() throws Exception {
//                people = online;
//                return null;
//            }
//        };
//    }
  
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        btnSend.setDisable(true);
        miSetupConnection.setDisable(false);
        
//        spChat.setFitToWidth(true);
       
      lvPerson.setItems(people);
//        anchorChat.prefHeightProperty().bind(tflowChat.heightProperty());
        
        
    }

}
