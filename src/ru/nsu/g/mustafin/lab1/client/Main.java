package ru.nsu.g.mustafin.lab1.client;

import java.io.IOException;
import java.net.*;

public class Main {
    public static void main(String[] args) throws IOException {
        String msg = "Hello";
        InetAddress group = InetAddress.getByName("228.5.6.7");
        MulticastSocket s = new MulticastSocket(6789);
        s.joinGroup(group);
        Sender sender=new Sender(group,s,6789);
        sender.start();

        Listener listener=new Listener(group,s,6789);
        listener.start();
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        s.leaveGroup(group);
        System.out.println("woke up, gg");
        sender.interrupt();
        listener.interrupt();
    }
}
