/**
 *
 *  @author Trauth Szymon  S30749
 *
 */

package zad1;


public class Client {
    public Client(String host, int port, String id) {
    }

    public void connect() {
    }

    public String send(String s) {
        return null;
    }
}
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;


public class Client {
    public static void main(String[] args) {

        try (
                SocketChannel socketChannel = SocketChannel.open();
        ) {
            socketChannel.connect(new InetSocketAddress("localhost", 7777));
            socketChannel.configureBlocking(false);

            socketChannel.write(ByteBuffer.wrap("Hello".getBytes( StandardCharsets.UTF_8)));

            socketChannel.socket().getOutputStream().flush();
            socketChannel.socket().close();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}