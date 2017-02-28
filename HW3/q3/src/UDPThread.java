/**
 * Server thread to receive UDP packets
 * Ali Tejani, amt3639
 * Travis McClure, tam2983
 */

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class UDPThread extends Thread {
    private byte[] buffer;
    private Inventory inventory;
    private DatagramPacket datapacket;
    private DatagramSocket datasocket;
    public UDPThread(Inventory inventory, int udpPort) throws SocketException {
        this.inventory = inventory;
        datasocket = new DatagramSocket(udpPort);
        this.buffer = new byte[1024];
    }

    public void run() {
        try {
            // while server is open
            while (true) {
                // accept packet
                this.buffer = new byte[1024];
                datapacket = new DatagramPacket(buffer, buffer.length);
                datasocket.receive(datapacket);
                // handle packet
                Thread t = new UDPHandlerThread(inventory,datasocket,new String(buffer).trim(),datapacket);
                t.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
