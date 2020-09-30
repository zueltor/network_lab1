package ru.nsu.g.mustafin.lab1.copyfinder;

import java.io.IOException;
import java.net.*;
import java.util.List;

public class Sender extends Thread {
    private final MulticastSocket socket;
    private final List<NetworkInterface> networkInterfaces;
    private final String secretMessage;
    private final static long SEND_DELAY = 2000;
    private final int port;
    private final InetAddress mcastAddress;

    public Sender(final InetAddress mcastAddress, final List<NetworkInterface> networkInterfaces, final MulticastSocket socket,
                  final int port, final String secretMessage) {
        this.networkInterfaces = networkInterfaces;
        this.socket = socket;
        this.port=port;
        this.mcastAddress=mcastAddress;
        this.secretMessage = secretMessage;
    }

    private void multicastSend() throws IOException {
        for (final var netif : this.networkInterfaces) {
            this.socket.setNetworkInterface(netif);
            final DatagramPacket packet = new DatagramPacket(this.secretMessage.getBytes(), this.secretMessage.length(), this.mcastAddress, this.port);
            this.socket.send(packet);
        }
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
