package ru.nsu.g.mustafin.lab1.copyfinder;

import java.io.IOException;
import java.net.*;
import java.util.List;

public class Sender extends Thread {
    private MulticastSocket socket;
    private List<NetworkInterface> networkInterfaces;
    private DatagramPacket packet;
    private final long SEND_DELAY = 2000;

    public Sender(InetAddress mcastaddress, List<NetworkInterface> networkInterfaces, int port, String secretMessage) {
        try {
            this.networkInterfaces = networkInterfaces;
            socket = new MulticastSocket();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
        packet = new DatagramPacket(secretMessage.getBytes(), secretMessage.length(), mcastaddress, port);
    }

    public void multicastSend() throws IOException {
        for (var netif : networkInterfaces) {
            socket.setNetworkInterface(netif);
            socket.send(packet);
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                this.multicastSend();
                sleep(SEND_DELAY);
            } catch (InterruptedException ex) {
                return;
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
    }
}
