package ru.nsu.g.mustafin.lab1.copyfinder;

import java.io.IOException;
import java.net.*;
import java.util.HashMap;
import java.util.List;

public class Listener extends Thread {
    private MulticastSocket socket;
    private DatagramPacket packet;
    private final long PRINT_DELAY = 5000;
    private final long COPY_DEAD_TIMEOUT = 4000;
    private HashMap<SocketAddress, Long> copiesOnline;
    private final String secretMessage;
    private boolean toPrintCopiesList = false;

    public Listener(InetAddress mcastaddress, List<NetworkInterface> networkInterfaces, int port, String secretMessage) {
        this.secretMessage = secretMessage;
        try {
            socket = new MulticastSocket(port);
            InetSocketAddress inetSocketAddress = new InetSocketAddress(mcastaddress, port);
            for (var netif : networkInterfaces) {
                socket.joinGroup(inetSocketAddress, netif);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
        byte[] buf = new byte[100];
        packet = new DatagramPacket(buf, buf.length);
        copiesOnline = new HashMap<>();
    }

    public void multicastReceive(long timeout) throws IOException {
        socket.setSoTimeout((int) timeout);
        socket.receive(packet);
        String message = new String(packet.getData()).trim();
        if (message.equals(secretMessage)) {
            SocketAddress socketAddress = packet.getSocketAddress();
            var prev_value = copiesOnline.put(socketAddress, System.currentTimeMillis());
            if (prev_value == null) {
                toPrintCopiesList = true;
            }
        }
    }


    @Override
    public void run() {
        long current_time = System.currentTimeMillis();
        long next_print_time = PRINT_DELAY + current_time;
        long receive_timeout;
        while (!isInterrupted()) {
            try {
                current_time = System.currentTimeMillis();
                if (next_print_time - current_time <= 0 || toPrintCopiesList) {
                    printCopiesList();
                    current_time = System.currentTimeMillis();
                    next_print_time = PRINT_DELAY + current_time;
                } else {
                    receive_timeout = next_print_time - current_time;
                    this.multicastReceive(receive_timeout);
                }
            } catch (SocketTimeoutException ignored) {
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
    }

    public void printCopiesList() {
        var old_size = copiesOnline.size();
        copiesOnline.entrySet().removeIf(copy -> System.currentTimeMillis() - copy.getValue() >= COPY_DEAD_TIMEOUT);
        var new_size = copiesOnline.size();
        if (new_size != old_size || toPrintCopiesList) {
            if (copiesOnline.size() > 0) {
                System.out.println("Copies online: " + copiesOnline.size());
            }
            for (var copy : copiesOnline.entrySet()) {
                System.out.println(copy.getKey());
            }
            toPrintCopiesList = false;
        }
    }
}
