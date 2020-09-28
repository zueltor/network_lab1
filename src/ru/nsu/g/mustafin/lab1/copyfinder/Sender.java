package ru.nsu.g.mustafin.lab1.copyfinder;

import java.io.IOException;
import java.net.*;
import java.util.List;

public class Sender extends Thread {
    private MulticastSocket socket;
    private List<NetworkInterface> networkInterfaces;
    private DatagramPacket packet;

    public Sender(InetAddress g,List<NetworkInterface> networkInterfaces, int p) {
        try {
            this.networkInterfaces=networkInterfaces;
            socket = new MulticastSocket();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
        String msg = "Hello";
        packet = new DatagramPacket(msg.getBytes(), msg.length(), g, p);
    }

    public void send() throws IOException {
        for (var netif : networkInterfaces) {
            socket.setNetworkInterface(netif);
            socket.send(packet);
        }
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            try {
                send();
                sleep(2000);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                return;
            }
        }
    }
}
