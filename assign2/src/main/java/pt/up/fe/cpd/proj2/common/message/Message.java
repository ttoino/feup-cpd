package pt.up.fe.cpd.proj2.common.message;

import pt.up.fe.cpd.proj2.common.Base64;

import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.stream.Collectors;

public abstract class Message {
    protected final ByteBuffer serialize(Object... objects) {
        var type = Base64.encode(this.getClass()
                .getSimpleName()
                .replace("Message", ""));
        var s = type + ":" + Arrays.stream(objects)
                .map(Object::toString)
                .map(Base64::encode)
                .collect(Collectors.joining(":"));
        return ByteBuffer.wrap(s.getBytes(StandardCharsets.UTF_8));
    }

    public abstract void deserialize(String[] parts);

    public abstract ByteBuffer serialize();

    public static Message deserialize(ByteBuffer buffer) {
        String s = StandardCharsets.UTF_8.decode(buffer).toString();
        String[] parts = Arrays.stream(s.split(":")).map(Base64::decode).toArray(String[]::new);

        try {
            Class<?> clazz = Class.forName("pt.up.fe.cpd.proj2.common.message." + parts[0] + "Message");
            Message message = (Message) clazz.getDeclaredConstructor().newInstance();
            message.deserialize(parts);
            return message;
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException |
                 IllegalAccessException | InvocationTargetException e) {
            throw new InvalidMessageException("Invalid message type", e);
        }
    }
}
