/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatclient;

/**
 *
 * @author William
 */
public class Connection {

    private static Connection instance = null;
    private String ip;
    private String name;

    private Connection() {
    }

    public static Connection getInstance() {
        if (instance == null) {
            instance = new Connection();
        }

        return instance;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
