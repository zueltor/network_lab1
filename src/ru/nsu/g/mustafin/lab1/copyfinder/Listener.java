package ru.nsu.g.mustafin.lab1.copyfinder;

import java.io.IOException;
import java.net.*;
import java.util.List;

public class Listener extends Thread {
    private InetAddress group;
    private MulticastSocket socket;
    private int port;

    public Listener(InetAddress g,List<NetworkInterface> networkInterfaces, int p) {
        group = g;
        try {
            socket = new MulticastSocket(p);
            InetSocketAddress inetSocketAddress=new InetSocketAddress(g,p);
            for(var netif:networkInterfaces){
                socket.joinGroup(inetSocketAddress,netif);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
        //socket.joinGroup(g);
        port = p;
    }

    public void recv() throws IOException {
        byte[] buf = new byte[100];
        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        socket.receive(packet);
        String message=new String(packet.getData()).trim()+" from " + packet.getSocketAddress();
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
