package driver;

import Interface.BroadCaster;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Utility {
    public static String SERVER_DEFAULT_IP = "127.0.0.1";
    public static int SERVER_DEFAULT_PORT = 8080;

    public static String SESSION_IP = SERVER_DEFAULT_IP;
    public static int SESSION_PORT = SERVER_DEFAULT_PORT;

    public static String name = "Anonymous";
    public static BroadCaster broadCaster;
    public static void setDefaultSessionAddr(){
        SESSION_IP = SERVER_DEFAULT_IP;
        SESSION_PORT = SERVER_DEFAULT_PORT;
    }
    public static void setDefaultName(String defaultName){
        name = defaultName;
    }

    public static BufferedImage deserializeImage(byte[] imgByte){
        try(ByteArrayInputStream in = new ByteArrayInputStream(imgByte)){
            return ImageIO.read(in);
        }catch (IOException e){
            System.out.println("Error on deserializing imageBytes");
            return null;
        }
    }

    public static byte[] serializeImage(BufferedImage img){
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()){
            ImageIO.write(img, "png", baos);
            baos.flush();
            return baos.toByteArray();
        } catch (IOException e) {
            System.out.println("Error on serializing bufferedImage");
            return null;
        }
    }

}
