package jheister.idearemoteexecute;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class HelloWorld {
    public static void main(String[] args) {
        System.out.println("Successfully remote executed: " + HelloWorld.class.getName() + ".main()");
        System.out.println("Hello, " + (args.length > 0 ? args[0] : "World"));
        System.out.println("Running on host: " + getHostname());
        System.out.println("As user: " + getUserName());
        System.out.println("With JVM: " + javaExec());
    }

    private static String getHostname() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return "unknown (" + e.getMessage() + ")";
        }
    }

    private static String getUserName() {
        return System.getProperty("user.name");
    }

    private static String javaExec() {
        return ManagementFactory.getRuntimeMXBean().getVmName();
    }
}
