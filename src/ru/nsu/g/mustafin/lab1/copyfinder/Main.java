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
    private static final String defaultMcastAddressName = "225.0.0.1";

    public static void main(final String[] args) {
        final String mcastAddressName;
        if (args.length > 0) {
            if ("-h".equals(args[0])) {
                printHelp();
                return;
            }
            mcastAddressName = args[0];
        } else {
            mcastAddressName = defaultMcastAddressName;
        }
        final InetAddress mcastAddress;
        try {
            mcastAddress = InetAddress.getByName(mcastAddressName);
        } catch (final IOException e) {
            System.err.println("Unknown host");
            printHelp();
            return;
        }
        if (!mcastAddress.isMulticastAddress()) {
            System.err.printf("%s is not a multicast address\n", mcastAddress.getHostAddress());
            printHelp();
            return;
        }

        final List<NetworkInterface> allNetworkInterfaces;
        try {
            allNetworkInterfaces = getAvailableNetworkInterfaces();
        } catch (final SocketException e) {
            System.err.println("Could not get available network interfaces");
            return;
        }

        System.out.println("Available network interface(s):");
        printInterfaces(allNetworkInterfaces);

        final List<NetworkInterface> networkInterfaces = chooseInterfaces(allNetworkInterfaces, args);

        System.out.println("Chosen network interface(s): ");
        printInterfaces(networkInterfaces);

        final Sender sender;
        final Listener listener;
        try {
            sender = new Sender(mcastAddress, networkInterfaces, port, secretMessage);
        }catch(final IOException e){
            System.err.println("Error occurred while creating sender thread");
            return;
        }
        try {
            listener = new Listener(mcastAddress, networkInterfaces, port, secretMessage);
        }catch(final IOException e){
            System.err.println("Error occurred while creating listener thread");
            return;
        }

        sender.start();
        listener.start();
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

    public static void printInterfaces(final List<NetworkInterface> allNetworkInterfaces) {
        for (final var netif : allNetworkInterfaces) {
            System.out.println(netif);
        }
    }

    public static List<NetworkInterface> chooseInterfaces(final List<NetworkInterface> allNetworkInterfaces,
                                                          final String[] args) {
        List<NetworkInterface> networkInterfaces = new ArrayList<>();
        final String netifname;

        if (allNetworkInterfaces.size() == 1) {
            networkInterfaces = allNetworkInterfaces;
        } else {
            if (args.length < 2) {
                System.out.println("Choose network interface");
                final Scanner scanner = new Scanner(System.in);
                netifname = scanner.nextLine();
            } else {
                netifname = args[1];
            }
            boolean found_any = false;
            for (final var netif : allNetworkInterfaces) {
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
