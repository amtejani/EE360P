/**
 * Ali Tejani, amt3639
 * Travis McClure, tam2983
 */
 
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.*;

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
        for (int i = 1; i <= numServer; i++) {
            // parse inputs to get the ips and ports of servers
            String str = sc.nextLine();
            System.out.println("address for server " + i + ": " + str);
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
    }

    private Set<String> messages;
    private Queue<Timestamp> q;
    private Set<InetSocketAddress> servers;
    private int id;
    private LamportClock clk;
    private boolean waiting;
    private int numOkays;
    public Inventory inventory;
    public static final int TIMEOUT = 100;
    public Server(Set<String> servers, Inventory inventory, int id) {
        this.servers = new HashSet<>();
        for(String s: servers) {
            String[] input = s.split(":");
            String hostname = input[0];
            int port = Integer.parseInt(input[1]);
            this.servers.add(new InetSocketAddress(hostname, port));
        }
        this.messages = new HashSet<>();
        this.q = new PriorityQueue<>();
        this.inventory = inventory;
        this.id = id;
        this.clk = new LamportClock();
        this.waiting = false;
    }

    private void sendMessage(String messageType, int pid, int c, String message) {
//        displayQueue();
//        StringBuilder str = new StringBuilder("server\n");
        StringBuilder str = new StringBuilder();
        str.append(messageType); str.append(":");
        str.append(pid); str.append(":");
        str.append(c); str.append(":");
        str.append(message);
        messages.add(str.toString());
        StringBuilder strSend = new StringBuilder("server\n");
        strSend.append(str);
        for(InetSocketAddress other: servers) {
            Socket s = new Socket();
            try {
                s.connect(other, Server.TIMEOUT);
                PrintStream pout = new PrintStream(s.getOutputStream());
                pout.println(strSend.toString());
//                System.out.println("SEND: " + str.toString());
                pout.flush();
            } catch (SocketTimeoutException e) {
                servers.remove(other);
                try {
                    s.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                s = new Socket();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void requestCS(String message) {
        // update clock
        clk.tick();
        int c = clk.getValue();
        // wait for last command
        while(waiting)  {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // print lamports clock and command to port
        sendMessage("request",id,c,message);
        // add to q
        Timestamp t = new Timestamp(id, c, message);
        q.add(t);
        // set num acks to 0
        numOkays = 0;
        // wait for acknowledgements
        while ((q.peek().pid != id) || (numOkays < servers.size())) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized String releaseCS() {
        // remove from q
        Timestamp t = q.remove();
        waiting = false;
        notifyAll();
        sendMessage("release", t.pid, t.logicalClock, t.message);
        // process command
        return inventory.getCommand(t.message);

    }

    public synchronized void handleMessage(String message) {
//        System.out.println("RECEIVE: " + message);
        String[] msg = message.split(":");
        String messageType = msg[0];
        // get timestamp and update clock
        clk.receiveAction(Integer.parseInt(msg[1]), Integer.parseInt(msg[2]));
        boolean send = messages.add(message);
        Timestamp t = new Timestamp(message);
        if (send) {
            sendMessage(messageType, t.pid, t.logicalClock, t.message);
            switch (messageType) {
                case "request":
                    // add to q, send okay
                    q.add(t);
                    sendMessage("okay", id, clk.getValue(), "okay");
                    break;
                case "okay":
                    numOkays++;
                    break;
                case "release":
                    // remove from q
                    int src = Integer.parseInt(message.split(":")[1]);
                    Iterator<Timestamp> it = q.iterator();
                    while (it.hasNext()) {
                        Timestamp timeStamp = it.next();
                        if (timeStamp.pid == src) {
                            inventory.getCommand(timeStamp.message);
                            it.remove();
                        }
                    }
            }
            notifyAll();
        }
    }

    private void displayQueue() {
        for (Timestamp t : q) {
            System.out.println(t);
        }
        System.out.println();
    }
}
