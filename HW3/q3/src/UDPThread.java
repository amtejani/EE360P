import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.HashMap;

/**
 * Created by Ali on 2/25/2017.
 */
public class UDPThread extends Thread {
    private byte[] buffer, returnBuffer;
    private Inventory inventory;
    private DatagramPacket datapacket, returnpacket;
    private DatagramSocket datasocket;
    public UDPThread(Inventory inventory, int udpPort) throws SocketException {
        this.inventory = inventory;
        datasocket = new DatagramSocket(udpPort);
        this.buffer = new byte[1024];
    }

    public void run() {
        try {
            while (true) {
                datapacket = new DatagramPacket(buffer, buffer.length);
                datasocket.receive(datapacket);
                // TODO parse command
                String response = inventory.getCommand(String.valueOf(buffer));
                returnBuffer = response.getBytes();
                returnpacket = new DatagramPacket(returnBuffer,returnBuffer.length,datapacket.getAddress(),datapacket.getPort());
                datasocket.send(returnpacket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}