/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatclient;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 *
 * @author William
 */
public class ListenerHandler extends Thread {

    Socket socket;
    String message;
    String printAtChat = "";
    boolean validId = false;
    TextFlow chat;
    Task task;
    private final ListView lvPerson;
    //TODO: enumaration with split tags

    ListenerHandler(TextFlow chat, Socket socket, ListView lvPerson) {
        this.chat = chat;
        this.socket = socket;
        this.lvPerson = lvPerson;
        setDaemon(true);
    }

    @Override
    public void run() {
        try {

            while (true) {
                InputStream is = socket.getInputStream();
                message = processRead(is);
                String[] tag = message.split(":-");

                switch (tag[0]) {
                    case "registration":
                        String id = getTag(tag[1], "your id");
                        ChatScreenController.id = Integer.valueOf(id);
                        break;
                    case "message":
                        Text update = new Text(tag[1] + "\r\n");

                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                chat.getChildren().add(update);
                            }
                        });

                        break;
                    case "online":

                        String people = tag[1];

                        String[] single = people.split("--");

                        ObservableList<String> p2 = FXCollections.observableArrayList();
                        for (String s : single) {
                            p2.add(s);

                        }

                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                ChatScreenController.people.clear();
                                lvPerson.setItems(p2);
                            }
                        });
                        break;
                    default:
                        System.out.println("Tag not recognized");
                        break;
                }
            }
        } catch (Exception e) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    ErrorMessage error = new ErrorMessage("Lost Connection.", this);
                    error.show();
                }
            });
        }
    }
//FIXME: change the tag of split: it brakes the message that comes  with : character

    private String getTag(String msg, String tag) {
        String[] tags = msg.split("/");
        for (int i = 0; i < tags.length; i++) {
            String[] flag = tags[i].split(":");
            if (flag[0].equals(tag)) {
                return flag[1];
            }
        }
        return null;
    }

    private static String processRead(InputStream is) throws IOException {

        byte[] bufferSize = new byte[4];
        int byteSize = is.read(bufferSize);

        ByteBuffer wrapped = ByteBuffer.wrap(bufferSize);
        int size = wrapped.getInt();

        byte[] bufferMsg = new byte[size];
        int byteMsg = is.read(bufferMsg);
        String msgIn = new String(bufferMsg, 0, byteMsg);

        return msgIn;
    }

}
