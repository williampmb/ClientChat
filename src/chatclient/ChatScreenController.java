/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatclient;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.net.URL;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
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
    OutputStream os;
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
    MenuItem close;
    @FXML
    ScrollPane spChat;
    @FXML
    AnchorPane anchorChat;
    @FXML
    ListView lvPerson;

    @FXML
    private void sendMessage(ActionEvent event) {
        if (tfSend.getText().equals("")) {

        } else {
            sendMessage();
        }
    }
    
    @FXML
    private void close(ActionEvent event){
        
        System.exit(0);
    }
    
    @FXML
    private void aboutScreen(ActionEvent event){
         try {
            Parent parent = FXMLLoader.load(getClass().getResource("chatclient/About.fxml"));
            Stage stage = new Stage();
            Scene scene = new Scene(parent);
            stage.setScene(scene);
            stage.setTitle("About");
            stage.show();
            stage.setResizable(false);
            
        } catch (Exception ex) {
            System.out.println("Problem to open.!");
        }
    }

    private void sendMessage() {
        Date date = new Date();
        DateFormat format = new SimpleDateFormat("HHmm");
        String time = format.format(date);
        //FIXME: split ~:~ and format time is same ~:~
        //FIXME: change the tag of split: it brakes the message that comes  with : character
        message = "id:" + id + "/" + "name:" + name + "/msg:" + tfSend.getText() + "/time:" + time;

        try {

            int length = message.getBytes().length;
            byte[] lengthBytes = intToBytes(length);
            byte[] msgOutBytes = message.getBytes();
            byte[] fullBytes = concatenateBytes(lengthBytes, msgOutBytes);

            os.write(fullBytes);
            os.flush();

        } catch (IOException ex) {
            Logger.getLogger(ChatScreenController.class.getName()).log(Level.SEVERE, null, ex);
        }

        time = time.substring(0, 2) + ":" + time.substring(2, time.length());
        String buildMsg = name + "[" + time + "]: " + tfSend.getText() + "\r\n";
        Text msg = new Text(buildMsg);
        msg.setFill(Color.BLUE);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                tflowChat.getChildren().add(msg);
            }
        });

        tfSend.setText("");
        spChat.requestFocus();
        tfSend.requestFocus();

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

            connect(connection.getIp(), connection.getName());

        } catch (IOException ex) {
            Logger.getLogger(ChatScreenController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private Socket tryConnection(String address) {
        String[] tok;
        String ip = null;
        String portStr;
        int port = 0;

        Socket socket = null;
        try {
            tok = address.split(":");
            ip = tok[0];
            portStr = tok[1];
            port = Integer.valueOf(portStr);

            socket = new Socket(ip, port);

        } catch (ArrayIndexOutOfBoundsException aioobe) {
            ErrorMessage error = new ErrorMessage("Not a valid address.", this);
            error.show();
        } catch (NullPointerException npe) {
            ErrorMessage error = new ErrorMessage("Not a valid address.", this);
            error.show();
        } catch (Exception e) {
            ErrorMessage error = new ErrorMessage("Server offline or not found", this);
            error.show();
        }
        return socket;
    }

    private void connect(String ip, String myName) {
        connected = tryConnection(ip);
        if (connected == null) {
            return;
        }
        resetAllFields();
        name = myName;
        String msgIntro = "registration:-" + name;

        try {
            os = connected.getOutputStream();
            int length = msgIntro.getBytes().length;
            byte[] lengthBytes = intToBytes(length);
            byte[] msgOutBytes = msgIntro.getBytes();
            byte[] fullBytes = concatenateBytes(lengthBytes, msgOutBytes);
            os.write(fullBytes);
            os.flush();

        } catch (IOException ex) {
            Logger.getLogger(ChatScreenController.class.getName()).log(Level.SEVERE, null, ex);
        }

       

        ListenerHandler rh = new ListenerHandler(tflowChat, connected, lvPerson);
        rh.start();
        btnSend.setDisable(false);
        miSetupConnection.setDisable(true);

    }

    public static byte[] intToBytes(final int i) {
        ByteBuffer bb = ByteBuffer.allocate(4);
        bb.putInt(i);
        return bb.array();
    }

    public static byte[] concatenateBytes(byte[] lengthBytes, byte[] msgOutBytes) {
        byte[] result = new byte[lengthBytes.length + msgOutBytes.length];
        System.arraycopy(lengthBytes, 0, result, 0, lengthBytes.length);
        System.arraycopy(msgOutBytes, 0, result, lengthBytes.length, msgOutBytes.length);
        return result;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        btnSend.setDisable(true);
        miSetupConnection.setDisable(false);

        lvPerson.setItems(people);
        anchorChat.prefHeightProperty().bind(tflowChat.heightProperty());

        tfSend.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent key) {
                if (key.getCode().equals(KeyCode.ENTER) && !tfSend.getText().equals("")) {
                    sendMessage();
                }
            }
        });

        //Update the scroll vertical bar to bottom
        DoubleProperty hProperty = new SimpleDoubleProperty();
        hProperty.bind(tflowChat.heightProperty());
        hProperty.addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                spChat.setVvalue(spChat.getVmax());
            }
        });

    }

    private void resetAllFields() {
        tflowChat.getChildren().clear();
        people.clear();
    }

}
