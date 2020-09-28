package ru.nsu.g.mustafin.lab1.hz;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.charset.StandardCharsets;

public class Main2 {
    public static void main(String[] args) throws IOException {
        String msg = "Hello";
        InetAddress group = InetAddress.getByName("228.5.6.7");
        MulticastSocket s = new MulticastSocket(6789);
        s.joinGroup(group);
        byte[] msgBytes = msg.getBytes(StandardCharsets.UTF_8);
        DatagramPacket hi = new DatagramPacket(msgBytes, msgBytes.length,
                group, 6789);
        s.send(hi);
        // get their responses!
        byte[] buf = new byte[1000];
        DatagramPacket recv = new DatagramPacket(buf, buf.length);
        s.receive(recv);
        s.leaveGroup(group);
    }

}
