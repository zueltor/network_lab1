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
    public Listener(InetAddress g, MulticastSocket s, int p){
        group=g;
        socket=s;
        port=p;
    }
    public void recv() throws IOException {
        byte[] buf = new byte[100];
        DatagramPacket recv = new DatagramPacket(buf, buf.length);
        socket.receive(recv);
        System.out.println(new String(recv.getData()));
    }


    @Override
    public void run() {
        while(!isInterrupted()){
            try {
                recv();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
    }
}
