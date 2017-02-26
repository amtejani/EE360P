import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by Ali on 2/25/2017.
 */
public class TCPThread extends Thread {
    private Inventory inventory;
    private Socket s;
    private Scanner din;
    private PrintWriter pout;
    public TCPThread(Inventory inventory, Socket s) throws IOException {
        this.inventory = inventory;
        this.s = s;
        this.din = new Scanner(s.getInputStream());
        this.pout = new PrintWriter(s.getOutputStream());
    }

    public void run() {
        String command;
        try {
            while (din.hasNextLine()) {
                command = din.nextLine();
                String response = inventory.getCommand(command);

    //            System.out.println("in: " + command);
    //            System.out.println("out: " + response);

                pout.println(response);
                pout.println("done");
                pout.flush();
            }
            s.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
