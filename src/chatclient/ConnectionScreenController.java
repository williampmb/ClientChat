/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatclient;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author William
 */
public class ConnectionScreenController implements Initializable {

    @FXML
    TextField tfServer;
    @FXML
    TextField tfName;
    @FXML
    Button btnConnect;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @FXML
    public void connect(ActionEvent e) {
        Connection setup = Connection.getInstance();
        setup.setIp(tfServer.getText());
        setup.setName(tfName.getText());
        Stage stage = (Stage) btnConnect.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void cancel(ActionEvent e) {
        Connection setup = Connection.getInstance();
        setup.setIp("");
        setup.setName("");
        Stage stage = (Stage) btnConnect.getScene().getWindow();
        stage.close();
    }

}
