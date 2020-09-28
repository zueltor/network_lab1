package ru.nsu.g.mustafin.lab1.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Arrays;

public class Listener extends Thread {
    private InetAddress group;
    private MulticastSocket socket;
    private int port;

    public Listener(InetAddress g, int p) {
        group = g;
        try {
            socket = new MulticastSocket(p);
            socket.joinGroup(g);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
        port = p;
    }

    public void recv() throws IOException {
        byte[] buf = new byte[100];
        DatagramPacket recv = new DatagramPacket(buf, buf.length);
        System.out.println("recving");
        socket.receive(recv);
        System.out.println("recved");
        String message=new String(recv.getData()).trim()+" from " + recv.getSocketAddress();
        System.out.println(message);
    }


    @Override
    public void run() {
        while (!isInterrupted()) {
            try {
                recv();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
    }
}
