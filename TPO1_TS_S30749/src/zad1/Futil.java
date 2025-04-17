package zad1;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;


public class Futil {

    private static final Charset inputCharset = Charset.forName("Cp1250");
    private static final Charset outputCharset = StandardCharsets.UTF_8;

    public static void processDir(String dirName, String resultFileName) {
        try {
            FileChannel.open(Paths.get(resultFileName), StandardOpenOption.WRITE, StandardOpenOption.CREATE).truncate(0).close();

            Files.walkFileTree(Paths.get(dirName), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
                {
                    try (FileChannel readChannel = FileChannel.open(file, StandardOpenOption.READ);
                         FileChannel writeChannel = FileChannel.open(Paths.get(resultFileName), StandardOpenOption.APPEND))
                    {
                        ByteBuffer buffer = ByteBuffer.allocate((int) readChannel.size());
                        readChannel.read(buffer);
                        buffer.flip();

                        CharsetDecoder decoder = inputCharset.newDecoder();
                        CharBuffer charBuffer = decoder.decode(buffer);

                        CharsetEncoder encoder = outputCharset.newEncoder();
                        ByteBuffer encoded = encoder.encode(charBuffer);

                        writeChannel.write(encoded);
                        buffer.clear();

                    }
                    return super.visitFile(file, attrs);
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
