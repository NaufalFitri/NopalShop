package org.Nopal.nopalShop.Data;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

public class Storing {

    private static final Logger log = LogManager.getLogger(Storing.class);

    public static String serialize(Object item) {

        String encodedObject;

        try{
            
            ByteArrayOutputStream io = new ByteArrayOutputStream();
            BukkitObjectOutputStream os = new BukkitObjectOutputStream(io);
            os.writeObject(item);
            os.flush();

            byte[] serializedObject = io.toByteArray();

            encodedObject = new String(Base64.getEncoder().encode(serializedObject));

            return encodedObject;
            

        } catch (IOException ex) {
            log.error(String.valueOf(ex));
        }
        return "null";
    }

    public static Object deserialize(String encoded) {

        try {
            
            byte[] serializedObject;
            serializedObject = Base64.getDecoder().decode(encoded);
            ByteArrayInputStream in = new ByteArrayInputStream(serializedObject);

            BukkitObjectInputStream is = new BukkitObjectInputStream(in);

            return is.readObject();
        } catch (IOException | ClassNotFoundException ex) {
            log.error(String.valueOf(ex));
        }

        return null;
    }

}
