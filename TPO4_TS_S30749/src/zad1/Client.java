/**
 *
 *  @author Trauth Szymon  S30749
 *
 */


package zad1;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class Client
{
    private final String host;
    private final int port;
    private final String id;
    private SocketChannel clientChannel;

    public Client(String host, int port, String id) {
        this.host = host;
        this.port = port;
        this.id = id;
    }

    public void connect()
    {
        try
        {
            clientChannel = SocketChannel.open();
            clientChannel.configureBlocking(false);
            clientChannel.connect(new InetSocketAddress(host, port));
            while (!clientChannel.finishConnect()) {}
        }
        catch (IOException e)
        {
            throw new RuntimeException("Error connecting to " + host + ":" + port, e);
        }
    }

    public String send(String req)
    {
        try {
            ByteBuffer writeBuffer = ByteBuffer.wrap((req + '\n').getBytes(StandardCharsets.UTF_8));
            while (writeBuffer.hasRemaining())
            {
                clientChannel.write(writeBuffer);
            }

            ByteBuffer readBuffer = ByteBuffer.allocate(1024);
            int bytesRead = 0;
            while (bytesRead == 0)
            {
                bytesRead = clientChannel.read(readBuffer);
                if (bytesRead == -1)
                {
                    throw new RuntimeException("Connection closed");
                }
            }
            readBuffer.flip();
            return StandardCharsets.UTF_8.decode(readBuffer).toString().trim();
        } catch (IOException e)
        {
            throw new RuntimeException("Error communicating with the server: ", e);
        }
    }

    public String getId()
    {
        return id;
    }
}
