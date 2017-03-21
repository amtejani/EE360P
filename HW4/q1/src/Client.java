import java.io.IOException;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int numServer = sc.nextInt();

        sc.nextLine();
        Queue<InetSocketAddress> servers = new LinkedList<>();
        for (int i = 0; i < numServer; i++) {
            String[] input = sc.nextLine().split(":");
            String hostname = input[0];
            int port = Integer.parseInt(input[1]);
            servers.add(new InetSocketAddress(hostname, port));
        }

        Client client = null;
        try {
            client = new Client(servers);

            while (sc.hasNextLine()) {
                String cmd = sc.nextLine();
                String[] tokens = cmd.split(" ");

                if (tokens[0].equals("purchase") || tokens[0].equals("cancel")
                        || tokens[0].equals("search") || tokens[0].equals("list")) {
                    // send appropriate command to the server and display the
                    // appropriate responses form the server
                    client.sendCommand(cmd);
                } else {
                    System.out.println("ERROR: No such command");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(client != null)
                client.close();
        }
    }

    private Queue<InetSocketAddress> servers;
    private Socket currentServer;
    private Scanner din;
    private PrintStream pout;
    public static final int TIMEOUT = 100;
    public Client(Queue<InetSocketAddress> servers) throws IOException {
        this.servers = servers;
        this.currentServer = new Socket();
    }

    private void connectNext() throws IOException{
        while(!currentServer.isConnected()) {
            try {
                this.currentServer.connect(this.servers.peek(), TIMEOUT);
                this.din = new Scanner(currentServer.getInputStream());
                this.pout = new PrintStream(currentServer.getOutputStream());
            } catch (SocketTimeoutException e) {
                servers.remove();
                currentServer.close();
                currentServer = new Socket();
            }
        }
    }

    /**
     * send command to server
     * @param command command to be sent
     * @throws IOException
     */
    public void sendCommand(String command) throws IOException {
        connectNext();
        String retString;
        pout.println("client\n" + command);
        pout.flush();
        while (din.hasNextLine()) {
            retString = din.nextLine();
            if (retString.equals("done")) break;
            System.out.println(retString);
        }
        System.out.println("[DEBUG] done");
    }

    /**
     * close sockets
     */
    public void close() {
        try {
            currentServer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
