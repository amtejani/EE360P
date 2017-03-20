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
                    // TODO: request CS
                    // read client input
                    command = din.nextLine();
                    // get response
                    String response = server.inventory.getCommand(command);
                    // send response
                    pout.println(response);
                    // done flag, for multiple lines
                    pout.println("done");
                    pout.flush();
                    // TODO: release CS
                } else if (messageType.equals("server")) {
                    // TODO: receive request and send okay, or receive okay, or receive release

                }
            }
            s.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
