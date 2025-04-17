package zad1;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;


public class Futil {

    public static void processDir(String dirName, String resultFileName) {
        try {
            FileChannel.open(Paths.get(resultFileName), StandardOpenOption.WRITE).truncate(0).close();

            Files.walkFileTree(Paths.get(dirName), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
                {
                    try (FileChannel readChannel = FileChannel.open(file, StandardOpenOption.READ);
                         FileChannel writeChannel = FileChannel.open(file, StandardOpenOption.APPEND))
                    {
                        ByteBuffer buffer = ByteBuffer.allocate((int) readChannel.size());
                        readChannel.read(buffer);
                        writeChannel.write(buffer);
                        buffer.flip();
                    }
                    return super.visitFile(file, attrs);
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
