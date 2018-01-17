package io.nerfnet.packetstack.data;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Giovanni on 17/01/2018.
 * <p>
 * Represents a data holder. Can only contain data elements of the same type.
 * Due to this, every time a new and unknown object enters the pipeline, a new piece
 * of MultiData gets created and stored in the {@link DataCacheGroup} of the pipeline's ending.
 */
public class MultiData {

    private final String mapId;
    private final String key;
    private final CopyOnWriteArrayList<Data> dataList = new CopyOnWriteArrayList<>();

    public MultiData(String mapId, String key) {
        this.mapId = mapId;
        this.key = key;
    }

    public MultiData add(Data data, boolean allowDuplicates) {
        if (!allowDuplicates && exists(data)) return this;
        dataList.add(data);
        return this;
    }

    private boolean exists(Data data) {
        for (Data allData : dataList) {
            if (allData.getId().equalsIgnoreCase(data.getId()))
                return true;
        }
        return false;
    }

    public String getKey() {
        return key;
    }

    public String getMapId() {
        return mapId;
    }
}
