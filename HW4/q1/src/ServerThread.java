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
    private Inventory inventory;
    private Socket s;
    private Scanner din;
    private PrintWriter pout;
    public ServerThread(Inventory inventory, Socket s) throws IOException {
        this.inventory = inventory;
        this.s = s;
        this.din = new Scanner(s.getInputStream());
        this.pout = new PrintWriter(s.getOutputStream());
    }

    public void run() {
        String command;
        try {
            // while client is connected
            while (din.hasNextLine()) {
                // read client input
                command = din.nextLine();
                // get response
                String response = inventory.getCommand(command);
                // send response
                pout.println(response);
                // done flag, for multiple lines
                pout.println("done");
                pout.flush();
            }
            s.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
