/**
 * Handles UDP requests
 * Ali Tejani, amt3639
 * Travis McClure, tam2983
 */

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class UDPHandlerThread extends Thread {
    private byte[] returnBuffer;
    private Inventory inventory;
    private DatagramPacket datapacket,returnpacket;
    private DatagramSocket datasocket;
    private String command;
    public UDPHandlerThread(Inventory inventory, DatagramSocket datagramSocket, String command, DatagramPacket datagramPacket) throws SocketException {
        this.inventory = inventory;
        this.datasocket = datagramSocket;
        this.command = command;
        this.datapacket = datagramPacket;
    }

    public void run() {
        try {
            // get appropriate response
            String response = inventory.getCommand(command);
            // send response to client
            returnBuffer = response.getBytes();
            returnpacket = new DatagramPacket(returnBuffer,returnBuffer.length,datapacket.getAddress(),datapacket.getPort());
            datasocket.send(returnpacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
