package com.alan.rpc.serializer;

import javax.sql.rowset.serial.SerialException;

public interface CommonSerializer {

    static final int KRYO_SERIALIZER = 2;

    byte[] serialize(Object obj) throws SerialException;

    Object deserialize(byte[] bytes, Class<?> clazz);

    int getCode();

    static CommonSerializer getByCode(int code) {
        switch (code) {
            case 1:
                return new JsonSerializer();
            case 2:
                return new KryoSerializer();
            default:
                return null;
        }
    }
}
