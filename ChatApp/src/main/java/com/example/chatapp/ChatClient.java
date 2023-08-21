package com.example.chatapp;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.*;

public class ChatClient extends Application {

    private static final DatagramSocket socket;

    static {
        try {
            // init tới cổng có sẵn
            socket = new DatagramSocket();
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }

    private static final InetAddress address;

    static {
        try {
            address = InetAddress.getByName("localhost");
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    private static final String identifier = "Tuyen";

    // Gửi đến server
    private static final int SERVER_PORT = 8000;

    private static final TextArea messageArea = new TextArea();

    private static final TextField inputBox = new TextField();


    public static void main(String[] args) throws IOException {

        // thread nhận tin nhắn
        ClientThread clientThread = new ClientThread(socket, messageArea);
        clientThread.start();

        // gửi tin tới server
        byte[] uuid = ("init;" + identifier).getBytes();
        DatagramPacket initialize = new DatagramPacket(uuid, uuid.length, address, SERVER_PORT);
        socket.send(initialize);

        launch();

    }

    @Override
    public void start(Stage primaryStage) {

        messageArea.setMaxWidth(500);
        messageArea.setEditable(false);


        inputBox.setMaxWidth(500);
        inputBox.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                // message để gửi
                String temp = identifier + ";" + inputBox.getText();
                // update messages trên màn
                messageArea.setText(messageArea.getText() + inputBox.getText() + "\n");
                // chuyển từ kiểu String thành bytes
                byte[] msg = temp.getBytes();
                inputBox.setText("");

                // Tạo packet và send
                DatagramPacket send = new DatagramPacket(msg, msg.length, address, SERVER_PORT);
                try {
                    socket.send(send);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        // hiển thị trên màn hình
        Scene scene = new Scene(new VBox(35, messageArea, inputBox), 550, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}