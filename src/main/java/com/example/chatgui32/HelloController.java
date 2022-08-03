package com.example.chatgui32;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class HelloController {
    DataOutputStream out;
    @FXML
    TextField messageField;
    @FXML
    TextArea textArea;

    @FXML
    TextArea userList;

    @FXML
    protected void sendHandler() throws IOException {
        String message = messageField.getText();
        messageField.clear();
        messageField.requestFocus();
        textArea.appendText(message+"\n");
        out.writeUTF(message);
    }

    @FXML
    protected void connect(){
        try {
            Socket socket = new Socket("127.0.0.1", 9445);
            out = new DataOutputStream(socket.getOutputStream());
            DataInputStream is = new DataInputStream(socket.getInputStream());
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {

                    try {
                        while (true) {
                            String response = is.readUTF();
                            System.out.println(response);
                            JSONParser jsonParser = new JSONParser();
                            JSONObject jsonObject = (JSONObject) jsonParser.parse(response);
                            if (jsonObject.get("users") != null) {
                                JSONArray onlineUsersJson = (JSONArray) jsonParser.parse(jsonObject.get("users").toString());
                                userList.clear();
                                for (int i = 0; i < onlineUsersJson.size(); i++) {
                                    userList.appendText(onlineUsersJson.get(i).toString() + "\n");
                                }
                            } else {
//                                textArea.appendText(response + "\n");
                                textArea.appendText(jsonObject.get("msg").toString() + "\n");
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

