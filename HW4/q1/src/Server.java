import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

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
        for (int i = 0; i < numServer; i++) {
            // TODO: parse inputs to get the ips and ports of servers
            String str = sc.nextLine();
            System.out.println("address for server " + i + ": " + str);
            String[] input = str.split(":");
            String hostname = input[0];
            int port = Integer.parseInt(input[1]);
        }

        // parse the inventory file
        Inventory inventory = new Inventory(inventoryPath);
        try {
            while (true) {
                ServerSocket server = new ServerSocket();
                Socket s;
                while ((s = server.accept()) != null) {
                    // create thread for new connection
                    Thread t = new ServerThread(inventory, s);
                    t.start();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // TODO: start server socket to communicate with clients and other servers

        // TODO: parse the inventory file

        // TODO: handle request from client
    }
}
