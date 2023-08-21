package com.example.chatapp;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

public class ChatServer {

    private static byte[] incoming = new byte[256];
    private static final int PORT = 8000;

    private static DatagramSocket socket;

    static {
        try {
            socket = new DatagramSocket(PORT);
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }

    private static ArrayList<Integer> users = new ArrayList<>();

    private static final InetAddress address;

    static {
        try {
            address = InetAddress.getByName("localhost");
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }


    public static void main(String[] args) {

        System.out.println("Server started on port " + PORT);

        while (true) {
            // packet
            DatagramPacket packet = new DatagramPacket(incoming, incoming.length);
            try {
                // nhận packet
                socket.receive(packet);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // tạo string để gửi
            String message = new String(packet.getData(), 0, packet.getLength());
            System.out.println("Server received: " + message);


            if (message.contains("init;")) {
                users.add(packet.getPort());
            }
            // gửi
            else {
                // nhận cỏng từ packet
                int userPort = packet.getPort();
                // đổi từ kiểu String thành kiểu bytes
                byte[] byteMessage = message.getBytes();

                // gửi tới tất cả user khác (trừ người đã gửi tin)
                for (int forward_port : users) {
                    if (forward_port != userPort) {
                        DatagramPacket forward = new DatagramPacket(byteMessage, byteMessage.length, address, forward_port);
                        try {
                            socket.send(forward);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }


        }
    }
}