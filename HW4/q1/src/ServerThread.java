/**
 * Handles TCP requests
 * Ali Tejani, amt3639
 * Travis McClure, tam2983
 */

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ServerThread extends Thread {
    private Server server;
    private Socket s;
    private Scanner din;
    private PrintWriter pout;
    public ServerThread(Socket s, Server server) throws IOException {
        this.server = server;
        this.s = s;
        this.din = new Scanner(s.getInputStream());
        this.pout = new PrintWriter(s.getOutputStream());
    }

    public void run() {
        String command;
        try {
            // while client is connected
            while (din.hasNextLine()) {
                String messageType = din.nextLine();
                if(messageType.equals("client")) {
                    // read client input
                    command = din.nextLine();
                    // request CS
                    server.requestCS(command);
                    // get response
//                    String response = server.inventory.getCommand(command);

                    // release CS
                    String response = server.releaseCS();
                    // send response
                    pout.println(response);
                    // done flag, for multiple lines
                    pout.println("done");
                    pout.flush();
                } else if (messageType.equals("server")) {
                    // receive request and send okay, or receive okay, or receive release
                    command = din.nextLine();
                    server.handleMessage(command);
                }
            }
            s.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
