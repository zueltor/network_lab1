package ru.nsu.g.mustafin.lab1.copyfinder;

import java.io.IOException;
import java.net.*;
import java.util.List;

public class Sender extends Thread {
    private final MulticastSocket socket;
    private final List<NetworkInterface> networkInterfaces;
    private final DatagramPacket packet;
    private final static long SEND_DELAY = 2000;

    public Sender(final InetAddress mcastaddress, final List<NetworkInterface> networkInterfaces, final MulticastSocket socket,
                  final int port, final String secretMessage) {
        this.networkInterfaces = networkInterfaces;
        this.socket = socket;
        this.packet = new DatagramPacket(secretMessage.getBytes(), secretMessage.length(), mcastaddress, port);
    }

    private void multicastSend() throws IOException {
        //for (final var netif : this.networkInterfaces) {
          //  this.socket.setNetworkInterface(netif);
            this.socket.send(this.packet);
        //}
    }

    @Override
    public void run() {
        while (true) {
            try {
                this.multicastSend();
                sleep(SEND_DELAY);
            } catch (final InterruptedException | IOException e) {
                return;
            }
        }
    }
}
