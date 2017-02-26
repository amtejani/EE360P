import java.io.IOException;
import java.io.PrintStream;
import java.net.*;
import java.util.HashMap;
import java.util.Scanner;

public class Server {
    public static void main(String[] args) {
        int tcpPort;
        int udpPort;
        if (args.length != 3) {
            System.out.println("ERROR: Provide 3 arguments");
            System.out.println("\t(1) <tcpPort>: the port number for TCP connection");
            System.out.println("\t(2) <udpPort>: the port number for UDP connection");
            System.out.println("\t(3) <file>: the file of inventory");

            System.exit(-1);
        }
        tcpPort = Integer.parseInt(args[0]);
        udpPort = Integer.parseInt(args[1]);
        String fileName = args[2];

        // parse the inventory file
        Inventory inventory = new Inventory(fileName);

        // handle request from clients
        try {

            // start thread with udpSocket
            Thread udp = new UDPThread(inventory, udpPort);
            udp.start();
            ServerSocket tcpServer = new ServerSocket(tcpPort);
            Socket s;
            while((s = tcpServer.accept()) != null) {
                Thread t = new TCPThread(inventory, s);
                t.start();
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
