package ru.nsu.g.mustafin.lab1.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class Sender extends Thread{
    private InetAddress group;
    private MulticastSocket socket;
    private int port;
    public Sender(InetAddress g, MulticastSocket s, int p){
        group=g;
        socket=s;
        port=p;
    }
    public void send() throws IOException {
        String msg = "Hello";
        socket.send(new DatagramPacket(msg.getBytes(), msg.length(),
                group, port));
        System.out.println("sent "+msg);
    }


    @Override
    public void run() {
        while(!isInterrupted()){
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
