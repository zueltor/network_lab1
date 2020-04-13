package ru.nsu.g.avoronkov.clientserver.client;

import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        String host = "localhost";
        int port = 10881;

        try ( var socket = new Socket(InetAddress.getByName(host), port)) {
            var output  = new PrintStream(socket.getOutputStream());
            output.println("hello server!");
            var input = new Scanner(socket.getInputStream());
            while (input.hasNext()) {
                System.out.printf("Server said: %s\n", input.nextLine());
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
