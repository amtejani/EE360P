import java.io.IOException;
import java.io.PrintStream;
import java.net.*;
import java.util.Scanner;

public class Client {
    private final String UDP = "UDP";
    private final String TCP = "TCP";


    public static void main(String[] args) {
        String hostAddress;
        int tcpPort;
        int udpPort;

        if (args.length != 3) {
            System.out.println("ERROR: Provide 3 arguments");
            System.out.println("\t(1) <hostAddress>: the address of the server");
            System.out.println("\t(2) <tcpPort>: the port number for TCP connection");
            System.out.println("\t(3) <udpPort>: the port number for UDP connection");
            System.exit(-1);
        }

        hostAddress = args[0];
        tcpPort = Integer.parseInt(args[1]);
        udpPort = Integer.parseInt(args[2]);

        Client client = null;
        try {
            client = new Client(hostAddress, tcpPort, udpPort);
            Scanner sc = new Scanner(System.in);
            while (sc.hasNextLine()) {
                String cmd = sc.nextLine();
                String[] tokens = cmd.split(" ");

                if (tokens[0].equals("setmode")) {
                    // set the mode of communication for sending commands to the server
                    // and display the name of the protocol that will be used in future
                    client.setMode(tokens[1]);
                } else if (tokens[0].equals("purchase") || tokens[0].equals("cancel")) {
                    // send appropriate command to the server and display the
                    // appropriate responses form the server
                    client.sendCommand(cmd);
                } else if (tokens[0].equals("search") || tokens[0].equals("list")) {
                    // send appropriate command to the server and display the
                    // appropriate responses form the server
                    client.sendCommand(cmd);
                } else {
                    System.out.println("ERROR: No such command");
                }
            }
        } catch (IOException e) {
            System.err.println(e);
        } finally {
            if(client != null)
                client.close();
        }
    }

    private int tcpPort;
    private int udpPort;
    private String connectionType;
    private String hostName;
    private InetAddress hostIP;
    private DatagramPacket sendUDP, receiveUDP;
    private byte[] buffer;
    private byte[] receiveBuffer;
    private DatagramSocket udpSocket;
    private Socket tcpSocket;
    private Scanner din;
    private PrintStream pout;

    public Client(String hostName, int tcpPort, int udpPort) throws IOException {
        this.hostName = hostName;
        this.hostIP = InetAddress.getByName(hostName);
        this.tcpPort = tcpPort;
        this.udpPort = udpPort;
        this.receiveBuffer = new byte[1024];
        this.connectionType = TCP;
        this.tcpSocket = new Socket(this.hostName, this.tcpPort);
        this.udpSocket = new DatagramSocket();
        this.din = new Scanner(tcpSocket.getInputStream());
        this.pout = new PrintStream(tcpSocket.getOutputStream());
    }

    public void setMode(String mode) {
        if (mode.equals("T"))
            connectionType = TCP;
        else if (mode.equals("U"))
            connectionType = UDP;
        System.out.println("Using " + connectionType + " connection.");
    }

    public void sendCommand(String command) throws IOException {
        buffer = new byte[command.length()];
        buffer = command.getBytes();

        String retString = "";
        if (connectionType.equals(UDP)) {
            sendUDP = new DatagramPacket(buffer, buffer.length, hostIP, udpPort);
            udpSocket.send(sendUDP);
            receiveUDP = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            udpSocket.receive(receiveUDP);
            retString = new String(receiveUDP.getData(), 0, receiveUDP.getLength());
            System.out.println(retString);
            // print receipt
        } else if (connectionType.equals(TCP)) {
            pout.println(command);
            pout.flush();
            while(din.hasNextLine()) {
                retString = din.nextLine();
                System.out.println(retString);
            }
        }
    }

    public void close() {
        try {
            tcpSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        udpSocket.close();
    }
}
