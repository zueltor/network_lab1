package ru.nsu.g.avoronkov.clientserver.server;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        int port = 10881;
        System.err.printf("Staring server at :%d\n", port);
        try {
            var serverSocket = new ServerSocket(port);
            while (true) {
                try (var socket = serverSocket.accept()) {
                    String data = "";
                    var input = new Scanner(socket.getInputStream());
                    if (input.hasNext()) {
                        data = input.nextLine();
                    }
                    System.err.printf("Input = %s\n", data);
                    var output = new PrintStream(socket.getOutputStream());
                    output.println("Hello client!");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
