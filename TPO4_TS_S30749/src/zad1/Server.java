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
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Server
{
    private final String host;
    private final int port;
    private Selector selector;
    private ServerSocketChannel serverSocketChannel;
    private final StringBuffer ServerLog = new StringBuffer();
    private volatile boolean isRunning = false;
    private Thread serverThread;

    private final Map<SocketChannel, StringBuilder> clientLogs = new HashMap<>();
    private final Map<SocketChannel, String> clientIds = new HashMap<>();

    public Server(String host, int port)
    {
        this.host = host;
        this.port = port;

        try
        {
            selector = Selector.open();
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.bind(new InetSocketAddress(host, port));
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public void startServer()
    {
        isRunning = true;
        serverThread = new Thread(() ->
        {
            try
            {
                while (isRunning)
                {
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
                            channel.configureBlocking(false);
                            ByteBuffer buffer = ByteBuffer.allocate(2048);
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
                            String[] requests = clientRequest.split("\n");

                            if (!clientLogs.containsKey(channel)) {
                                clientLogs.put(channel, new StringBuilder());
                            }

                            for (String req : requests)
                            {
                                if (!req.trim().isEmpty())
                                {
                                    buffer.flip();

                                    String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS"));

                                    if (req.startsWith("login "))
                                    {
                                        String id = req.split(" ")[1].trim();
                                        clientIds.put(channel, id);
                                        clientLogs.put(channel, new StringBuilder());
                                        channel.write(ByteBuffer.wrap("logged in\n".getBytes(StandardCharsets.UTF_8)));
                                        clientLogs.get(channel)
                                                .append("=== ").append(id).append(" log start ===\n")
                                                .append("logged in\n");
                                        ServerLog.append(id).append(" logged in at ").append(time).append("\n");
                                    }
                                    else if (req.equals("bye and log transfer"))
                                    {
                                        clientLogs.get(channel).append("logged out\n");
                                        clientLogs.get(channel).append("=== ").append(clientIds.get(channel)).append(" log end ===\n");
                                        String log = (clientLogs.get(channel).toString());
                                        channel.write(ByteBuffer.wrap(log.getBytes(StandardCharsets.UTF_8)));
                                        ServerLog.append(clientIds.get(channel)).append(" logged out at ").append(time).append("\n");
                                        key.cancel();
                                        channel.close();

                                    } else if (req.equals("bye"))
                                    {
                                        clientLogs.get(channel).append("logged out\n");
                                        clientLogs.get(channel).append("=== ").append(clientIds.get(channel)).append(" log end ===\n\n");
                                        channel.write(ByteBuffer.wrap("logged out\n".getBytes(StandardCharsets.UTF_8)));
                                        String id = clientIds.get(channel);
                                        ServerLog.append(id).append(" logged out at ").append(time).append("\n");
                                        key.cancel();
                                        channel.close();

                                    } else
                                    {
                                        String[] parts = req.split(" ");
                                        if (parts.length == 2) {
                                            String result = Time.passed(parts[0], parts[1]);
                                            String response =  result + "\n";
                                            channel.write(ByteBuffer.wrap(response.getBytes(StandardCharsets.UTF_8)));
                                            String id = clientIds.get(channel);
                                            ServerLog.append(id).append(" request at ").append(time).append(": \"").append(req).append("\"\n");
                                            clientLogs.get(channel)
                                                    .append("Request: ").append(req).append("\n")
                                                    .append("Result:\n").append(result).append("\n");
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (IOException e)
            {
                ServerLog.append("Error starting server: ").append(e.getMessage()).append("\n");
                throw new RuntimeException(e);
            }
        });
        serverThread.start();
    }

    public void stopServer()
    {
        isRunning = false;
        try {
            selector.wakeup();
            if (serverThread != null) {
                serverThread.join();
            }
            serverSocketChannel.close();
            selector.close();
        } catch (IOException | InterruptedException e) {
            ServerLog.append("Error stopping server: ").append(e.getMessage()).append("\n");
        }
    }

    public String getServerLog()
    {
        return ServerLog.toString();
    }
}
