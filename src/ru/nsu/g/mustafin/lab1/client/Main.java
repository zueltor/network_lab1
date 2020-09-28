package ru.nsu.g.mustafin.lab1.client;

import java.io.IOException;
import java.net.*;

public class Main {
    public static void main(String[] args) throws IOException {
        InetAddress mcastaddress = InetAddress.getByName("228.5.6.7");
        if(!mcastaddress.isMulticastAddress()){
            System.err.printf("%s is not a multicast address\n",mcastaddress.getHostAddress());
            return;
        }

        Sender sender=new Sender(mcastaddress,6789);
        sender.start();

        Listener listener=new Listener(mcastaddress,6789);
        listener.start();
        try {
            Thread.sleep(100000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("woke up, gg");
        sender.interrupt();
        //listener.interrupt();
    }
}
