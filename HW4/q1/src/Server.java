import java.io.IOException;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class Server {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int myID = sc.nextInt();
        int numServer = sc.nextInt();
        String inventoryPath = sc.next();
        sc.nextLine();

        System.out.println("[DEBUG] my id: " + myID);
        System.out.println("[DEBUG] numServer: " + numServer);
        System.out.println("[DEBUG] inventory path: " + inventoryPath);

        Set<String> servers = new HashSet<>();
        int thisPort = 0;
        for (int i = 0; i < numServer; i++) {
            // parse inputs to get the ips and ports of servers
            String str = sc.nextLine();
            System.out.println("address for server " + i + ": " + str);
//            String[] input = str.split(":");
//            String hostname = input[0];
//            int port = Integer.parseInt(input[1]);
            if(i == myID) {
                String[] input = str.split(":");
                thisPort = Integer.parseInt(input[1]);
            } else {
                servers.add(str);
            }
        }

        // parse the inventory file
        Inventory inventory = new Inventory(inventoryPath);
        try {
            Server thisServer = new Server(servers, inventory, myID);
            ServerSocket server = new ServerSocket(thisPort);
            Socket s;
            while ((s = server.accept()) != null) {
                // create thread for new connection
                Thread t = new ServerThread(s, thisServer);
                t.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // TODO: start server socket to communicate with clients and other servers

        // TODO: parse the inventory file

        // TODO: handle request from client
    }

    private Set<InetSocketAddress> servers;
    private int id;
    public Inventory inventory;
    public static final int TIMEOUT = 100;
    public Server(Set<String> servers, Inventory inventory, int id) {
        for(String s: servers) {
            String[] input = s.split(":");
            String hostname = input[0];
            int port = Integer.parseInt(input[1]);
            this.servers.add(new InetSocketAddress(hostname, port));
        }
        this.inventory = inventory;
        this.id = id;
    }

    private void sendMessage(String message) {
        for(InetSocketAddress other: servers) {
            Socket s = new Socket();
            try {
                s.connect(other, Server.TIMEOUT);
                PrintStream pout = new PrintStream(s.getOutputStream());
                pout.println("server\n" + message);
                pout.flush();
            } catch (SocketTimeoutException e) {
                servers.remove(other);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void requestCS() {
        // update clock
        // TODO: print lamports clock and command to port
//        sendMessage();
        // TODO: wait for acknowledgements
        // add to q
        // set num acks to 0
        // n = num servers
//        while ((q.peek().pid != myId) || (numAcks < n))
//            myWait();
    }

    public synchronized void releaseCS() {
        // TODO: remove from q, process command
    }

    public synchronized void handleMessage(String messageType) {
        // TODO: get timestamp and update clock
        switch (messageType) {
            case "request": // TODO: add to q, send okay
            case "okay": // TODO: inc num okays
            case "release": // TODO: remove from q
        }
    }
}
