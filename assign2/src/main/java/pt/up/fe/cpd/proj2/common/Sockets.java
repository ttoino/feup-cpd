package pt.up.fe.cpd.proj2.common;

import pt.up.fe.cpd.proj2.common.message.Message;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.*;

public final class Sockets {
    private static final Map<SocketChannel, Queue<Message>> queues = new WeakHashMap<>();
    private static final Map<SocketChannel, ByteBuffer> buffers = new WeakHashMap<>();
    private static final Map<SocketChannel, String> partialReads = new WeakHashMap<>();

    public static Message read(SocketChannel channel) {
        var queue = queues.computeIfAbsent(channel, c -> new LinkedList<>());
        var buffer = buffers.computeIfAbsent(channel, c -> ByteBuffer.allocate(1024));

        if (queue.size() > 0) return queue.poll();

        try {
            buffer.clear();
            var read = channel.read(buffer);

            if (read == -1) {
                channel.close();
                return null;
            }

            if (read == 0)
                return queue.poll();

            buffer.flip();

            String s = partialReads.getOrDefault(channel, "") + StandardCharsets.UTF_8.decode(buffer);
            String[] parts = s.split("\n");

            if (!s.endsWith("\n")) {
                partialReads.put(channel, parts[parts.length - 1]);
                parts = Arrays.copyOf(parts, parts.length - 1);
            } else {
                partialReads.remove(channel);
            }

            for (var p : parts) {
                if (p.isBlank()) continue;
                var message = Message.deserialize(p);
                if (message != null) queue.add(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return queue.poll();
    }

    public static void write(SocketChannel channel, Message message) {
        if (channel == null || message == null || !channel.isOpen()) return;

        try {
            ByteBuffer buffer = message.serialize();
            buffer = ByteBuffer.allocate(buffer.limit() + 1).put(buffer).put((byte) '\n');
            buffer.flip();
            channel.write(buffer);
        } catch (Exception e) {
            try {
                channel.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private Sockets() {}
}
