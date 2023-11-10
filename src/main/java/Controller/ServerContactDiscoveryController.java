package Controller;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;


public class ServerContactDiscoveryController {

    public static class EchoServer extends Thread {

        private final DatagramSocket socket;
        private final byte[] buf = new byte[256];

        public EchoServer() throws SocketException {
            socket = new DatagramSocket(4445);
        }

        public static void sendIP(String message, InetAddress ip_address, int nport) {

            try{
                DatagramSocket socket = new DatagramSocket(1789);
                byte[] buf;  // max size of the buffer : message length < buffer

                buf = message.getBytes();
                DatagramPacket outPacket = new DatagramPacket(buf, buf.length, ip_address, nport); // Package content sent to senderAdress (senderPort)
                socket.send(outPacket); // those lines are sending the packet

                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            boolean running = true;

            while (running) {
                DatagramPacket packet
                        = new DatagramPacket(buf, buf.length);
                try {
                    socket.receive(packet);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("Received");

                InetAddress address = packet.getAddress();
                int port = packet.getPort();
                String received
                        = new String(packet.getData(), 0, packet.getLength());

                System.out.println("address :" + address);

                System.out.println("port : " + port);

                System.out.println("Received : " + received);


                if (received.equals("IP_address")) {
                    System.out.println("check");
                    sendIP("response_ip", address, port);
                }
                try {
                    socket.send(packet);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                //retirer la personne des gens connectés
                if (received.equals("end")) {
                    running = false;
                    continue;
                }
                try {
                    socket.send(packet);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            socket.close();
        }
    }

    public static void main(String[] args) throws IOException {
        Thread Server = new EchoServer();
        Server.start();
    }


}

