package net.thesimpleteam.pluginapi.utils;

import java.io.*;

public class SerializationUtils {

    /**
     * Serializes an object to a byte array
     * @param object the object to serialize
     * @return A byte array or an empty byte array if an error occurred or if the class is not serializable
     */
    public static byte[] serialize(Object object) {
        try(ByteArrayOutputStream os = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(os)) {
            oos.writeObject(object);
            return os.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    /**
     * Deserializes an object from a byte array
     * @param bytes the byte array to deserialize
     * @param clazz the class of the object
     * @return The deserialized object
     * @param <T> The type of the object
     */
    public static <T> T deserialize(byte[] bytes, Class<T> clazz) {
        try(var input = new ByteArrayInputStream(bytes); ObjectInputStream ois = new ObjectInputStream(input)) {
            return clazz.cast(ois.readObject());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

}