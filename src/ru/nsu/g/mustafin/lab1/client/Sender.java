package ru.nsu.g.mustafin.lab1.client;

import java.io.IOException;
import java.net.*;
import java.util.List;
import java.util.stream.Collectors;

public class Sender extends Thread {
    private MulticastSocket socket;
    private List<NetworkInterface> networkInterfaces;
    private DatagramPacket packet;

    public Sender(InetAddress g, int p) {
        try {
            socket = new MulticastSocket();
            networkInterfaces = NetworkInterface.networkInterfaces().filter(networkInterface -> {
                try {
                    return networkInterface.isUp() && !networkInterface.isLoopback() && networkInterface.supportsMulticast();
                } catch (SocketException e) {
                    e.printStackTrace();
                }
                return false;
            }).collect(Collectors.toList());
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
            System.out.println("sent");
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
