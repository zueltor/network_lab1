package ru.nsu.g.mustafin.lab1.copyfinder;

import java.io.IOException;
import java.net.*;
import java.util.HashMap;

public class Listener extends Thread {
    private final MulticastSocket socket;
    private final static long PRINT_DELAY = 5000;
    private final long COPY_DEAD_TIMEOUT = 4000;
    private final HashMap<SocketAddress, Long> copiesOnline;
    private final String secretMessage;
    private boolean toPrintCopiesList = false;

    public Listener(final MulticastSocket socket, final String secretMessage) {
        this.secretMessage = secretMessage;
        this.socket = socket;
        this.copiesOnline = new HashMap<>();
    }

    private void multicastReceive(final long timeout) throws IOException {
        this.socket.setSoTimeout((int) timeout);
        final byte[] buf = new byte[100];
        final DatagramPacket packet = new DatagramPacket(buf, buf.length);
        this.socket.receive(packet);
        final String message = new String(packet.getData()).trim();
        if (message.equals(this.secretMessage)) {
            final SocketAddress socketAddress = packet.getSocketAddress();
            final var prev_value = this.copiesOnline.put(socketAddress, System.currentTimeMillis());
            if (prev_value == null) {
                this.toPrintCopiesList = true;
            }
        }
    }

    @Override
    public void run() {
        long current_time = System.currentTimeMillis();
        long next_print_time = PRINT_DELAY + current_time;
        long receive_timeout;
        while (true) {
            try {
                current_time = System.currentTimeMillis();
                if (next_print_time - current_time <= 0 || this.toPrintCopiesList) {
                    this.printCopiesList();
                    current_time = System.currentTimeMillis();
                    next_print_time = PRINT_DELAY + current_time;
                } else {
                    receive_timeout = next_print_time - current_time;
                    this.multicastReceive(receive_timeout);
                }
            } catch (final SocketTimeoutException ignored) {
            } catch (final IOException e) {
                return;
            }
        }
    }

    public void printCopiesList() {
        final var old_size = this.copiesOnline.size();
        this.copiesOnline.entrySet().removeIf(copy -> System.currentTimeMillis() - copy.getValue() >= this.COPY_DEAD_TIMEOUT);
        final var new_size = this.copiesOnline.size();
        if (new_size != old_size || this.toPrintCopiesList) {
            if (this.copiesOnline.size() > 0) {
                System.out.println("Copies online: " + this.copiesOnline.size());
            }
            for (final var copy : this.copiesOnline.entrySet()) {
                System.out.println(copy.getKey());
            }
            this.toPrintCopiesList = false;
        }
    }
}
