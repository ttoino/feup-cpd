package pt.up.fe.cpd.proj2.common;

import pt.up.fe.cpd.proj2.common.message.Message;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.*;

public final class Sockets {
    private static final Map<SocketChannel, Queue<Message>> buffers = new WeakHashMap<>();

    public static Message read(SocketChannel channel) {
        var queue = buffers.computeIfAbsent(channel, c -> new LinkedList<>());
        var buffer = ByteBuffer.allocate(1024);

        if (queue.size() > 0) return queue.poll();

        try {
            var read = channel.read(buffer);

            if (read == -1) {
                channel.close();
                return null;
            }

            if (read == 0)
                return queue.poll();

            buffer.flip();

            String s = StandardCharsets.UTF_8.decode(buffer).toString();
            String[] parts = s.split("\n");

            for (var p : parts) {
                if (s.isBlank()) continue;
                var message = Message.deserialize(p);
                if (message != null) queue.add(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return queue.poll();
    }

    public static void write(SocketChannel channel, Message message) {
        try {
            ByteBuffer buffer = message.serialize();
            buffer = ByteBuffer.allocate(buffer.limit() + 1).put(buffer).put((byte) '\n');
            buffer.flip();
            channel.write(buffer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Sockets() {}
}
