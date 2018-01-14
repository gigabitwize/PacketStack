package io.vawke.packetstack;

/**
 * Created by Giovanni on 14/01/2018.
 */
public class ServerMain {

    private static StackService service;

    public static void main(String[] args) {
        service = new StackService();
        service.start();
    }
}
