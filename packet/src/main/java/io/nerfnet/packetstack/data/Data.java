package io.nerfnet.packetstack.data;

/**
 * Created by Giovanni on 17/01/2018.
 */
public class Data {

    private final String id;
    private final byte[] data;

    public Data(String id, byte[] data) {
        this.id = id;
        this.data = data;
    }

    public byte[] getObjectBytes() {
        return data;
    }

    public String getId() {
        return id;
    }
}
