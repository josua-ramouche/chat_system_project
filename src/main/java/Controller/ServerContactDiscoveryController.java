package Controller;
import java.io.IOException;
import java.net.*;



public class ServerContactDiscoveryController {

    public static class EchoServer extends Thread {

        private final DatagramSocket socket;
        private final byte[] buf = new byte[256];

        public EchoServer() throws SocketException {
            socket = new DatagramSocket(4445);
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

                InetAddress address = packet.getAddress();
                int port = packet.getPort();
                packet = new DatagramPacket(buf, buf.length, address, port);
                String received
                        = new String(packet.getData(), 0, packet.getLength());

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


}

