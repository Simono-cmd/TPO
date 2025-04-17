/**
 *
 *  @author Trauth Szymon  S30749
 *
 */

package zad1;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

public class Server {
    private final String host;
    private final int port;
    private Selector selector;
    private ServerSocketChannel serverSocketChannel;
    private final StringBuilder ServerLog = new StringBuilder();
    private volatile boolean isRunning = false;
    private Thread serverThread;

    public Server(String host, int port) {
        this.host = host;
        this.port = port;

        try
        {
            selector = Selector.open();
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.bind(new InetSocketAddress(host, port));
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            ServerLog.append("Server initialized on ").append(host).append(" : ").append(port).append("\n");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void startServer() {
        isRunning=true;
        serverThread = new Thread(() -> {
           try{
               while (isRunning) {
                   selector.select();
                   Set<SelectionKey> selectedKeys = selector.selectedKeys();
                   Iterator<SelectionKey> iterator = selectedKeys.iterator();
                   while (iterator.hasNext()) {
                       SelectionKey key = iterator.next();
                       iterator.remove();

                       if (key.isAcceptable()) {
                            SocketChannel accept = serverSocketChannel.accept();
                            accept.configureBlocking(false);
                            accept.register(selector, SelectionKey.OP_READ);
                       }

                       if (key.isReadable()) {
                           SocketChannel channel = (SocketChannel) key.channel();
                           ByteBuffer buffer = ByteBuffer.allocate(1024);
                           SocketAddress address = channel.getLocalAddress();

                           int read = channel.read(buffer);
                           if (read == -1) {
                               key.cancel();
                               channel.close();
                               ServerLog.append("Connection closed by ").append(address).append("\n");
                               continue;
                           }
                           buffer.flip();
                           String clientRequest = StandardCharsets.UTF_8.decode(buffer).toString();
                           ServerLog.append("[").append(address).append("] Request: ").append(clientRequest).append("\n");

                           String[] strings = clientRequest.split(" ");
                           String s1 = strings[0];
                           String s2 = strings[1];

                           String response = "Result: \n"+Time.passed(s1, s2);
                           channel.write(ByteBuffer.wrap(response.getBytes(StandardCharsets.UTF_8)));





                       }




                   }

               }
           } catch (IOException e) {
               ServerLog.append("Error starting server: ").append(e.getMessage()).append("\n");
               throw new RuntimeException(e);
           }

        });
    }

    public void stopServer() {
    }

    public StringBuilder getServerLog() {
        return ServerLog;
    }
}



