package ru.nsu.g.mustafin.lab1.copyfinder;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Main {

    private static final String secretMessage = "mde18201";
    private static final int port = 6789;
    private static final String defaultMcastAddressName = "228.5.6.7";

    public static void main(String[] args) throws IOException {
        String mcastAddressName;
        if (args.length > 0) {
            mcastAddressName = args[0];
        } else {
            mcastAddressName = defaultMcastAddressName;
        }
        InetAddress mcastAddress = InetAddress.getByName(mcastAddressName);
        if (!mcastAddress.isMulticastAddress()) {
            System.err.printf("%s is not a multicast address\n", mcastAddress.getHostAddress());
            return;
        }

        List<NetworkInterface> allNetworkInterfaces = getAvailableNetworkInterfaces();

        System.out.println("Available network interface(s):");
        printInterfaces(allNetworkInterfaces);

        List<NetworkInterface> networkInterfaces = chooseInterfaces(allNetworkInterfaces, args);

        System.out.println("Chosen network interface(s): ");
        printInterfaces(networkInterfaces);

        Sender sender = new Sender(mcastAddress, networkInterfaces, 6789, secretMessage);
        sender.start();

        Listener listener = new Listener(mcastAddress, networkInterfaces, 6789, secretMessage);
        listener.start();
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        sender.interrupt();
        listener.interrupt();
        try {
            sender.join();
            listener.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static List<NetworkInterface> getAvailableNetworkInterfaces() throws SocketException {
        return NetworkInterface.networkInterfaces().filter(networkInterface -> {
            try {
                return networkInterface.isUp()
                        && !networkInterface.isLoopback()
                        && networkInterface.supportsMulticast();
            } catch (SocketException e) {
                e.printStackTrace();
            }
            return false;
        }).collect(Collectors.toList());
    }

    public static void printInterfaces(List<NetworkInterface> allNetworkInterfaces) {
        for (var netif : allNetworkInterfaces) {
            System.out.println(netif);
        }
    }

    public static List<NetworkInterface> chooseInterfaces(List<NetworkInterface> allNetworkInterfaces, String[] args) {
        List<NetworkInterface> networkInterfaces = new ArrayList<>();
        String netifname;

        if (allNetworkInterfaces.size() == 1) {
            networkInterfaces = allNetworkInterfaces;
        } else {
            if (args.length < 2) {
                System.out.println("Choose network interface");
                Scanner scanner = new Scanner(System.in);
                netifname = scanner.nextLine();
            } else {
                netifname = args[1];
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
        }
        return networkInterfaces;
    }
}
