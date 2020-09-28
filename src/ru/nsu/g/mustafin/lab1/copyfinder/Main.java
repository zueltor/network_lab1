package ru.nsu.g.mustafin.lab1.copyfinder;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) throws IOException {
        InetAddress mcastaddress = InetAddress.getByName("228.5.6.7");
        if (!mcastaddress.isMulticastAddress()) {
            System.err.printf("%s is not a multicast address\n", mcastaddress.getHostAddress());
            return;
        }

        List<NetworkInterface> allNetworkInterfaces = NetworkInterface.networkInterfaces().filter(networkInterface -> {
            try {
                return networkInterface.isUp()
                        && !networkInterface.isLoopback()
                        && networkInterface.supportsMulticast();
            } catch (SocketException e) {
                e.printStackTrace();
            }
            return false;
        }).collect(Collectors.toList());

        for (var netif : allNetworkInterfaces) {
            System.out.println(netif);
        }

        List<NetworkInterface> networkInterfaces = new ArrayList<>();
        String netifname;

        if (args.length == 0) {
            System.out.println("Choose network interface");
            Scanner scanner = new Scanner(System.in);
            netifname = scanner.nextLine();
        } else {
            netifname = args[0];
        }

        boolean found_any = false;
        for (var netif : allNetworkInterfaces) {
            if (netif.getName().contains(netifname)) {
                found_any = true;
                networkInterfaces.add(netif);
                break;
            }
        }

        if (!found_any) {
            networkInterfaces = allNetworkInterfaces;
        }

        System.out.println("Chosen network interface(s): ");
        for (var netif : networkInterfaces) {
            System.out.println(netif);
        }

        Sender sender = new Sender(mcastaddress, networkInterfaces, 6789);
        sender.start();

        Listener listener = new Listener(mcastaddress, networkInterfaces, 6789);
        listener.start();
        try {
            Thread.sleep(100000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        sender.interrupt();
        //listener.interrupt();
    }
}
