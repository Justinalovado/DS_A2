package driver;

import Interface.BroadCaster;

public class Announcer {
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
}
