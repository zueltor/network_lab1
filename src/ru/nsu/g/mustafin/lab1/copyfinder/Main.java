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

    public static void main(String[] args) {
        String mcastAddressName;
        if (args.length > 0) {
            if (args[0].equals("-h")) {
                printHelp();
                return;
            }
            mcastAddressName = args[0];
        } else {
            mcastAddressName = defaultMcastAddressName;
        }
        InetAddress mcastAddress;
        try {
            mcastAddress = InetAddress.getByName(mcastAddressName);
        } catch (IOException e) {
            e.printStackTrace();
            printHelp();
            return;
        }
        if (!mcastAddress.isMulticastAddress()) {
            System.err.printf("%s is not a multicast address\n", mcastAddress.getHostAddress());
            return;
        }

        List<NetworkInterface> allNetworkInterfaces;
        try {
            allNetworkInterfaces = getAvailableNetworkInterfaces();
        } catch (SocketException e) {
            e.printStackTrace();
            return;
        }

        System.out.println("Available network interface(s):");
        printInterfaces(allNetworkInterfaces);

        List<NetworkInterface> networkInterfaces = chooseInterfaces(allNetworkInterfaces, args);

        System.out.println("Chosen network interface(s): ");
        printInterfaces(networkInterfaces);

        Sender sender = new Sender(mcastAddress, networkInterfaces, port, secretMessage);
        sender.start();

        Listener listener = new Listener(mcastAddress, networkInterfaces, port, secretMessage);
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

    private static void printHelp() {
        System.out.println("Parameters (optional): <mcastaddress> <hint>\n" +
                "<mcastaddress> - IPv4 or IPv6 multicast address\n" +
                "<hint> - network interface name, used if there are multiple network interfaces");
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
