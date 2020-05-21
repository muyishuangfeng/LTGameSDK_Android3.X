package com.gnetop.ltgame.core.net.conver;

import com.gnetop.ltgame.core.base.BaseEntry;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class JSONFactory {
    private static final Type type = new TypeToken<BaseEntry<String>>() {
    }.getType();

    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(type, new StringResponseDeserializer())
            .create();

    private JSONFactory() {

    }

    public static String toJson(Object o) {
        return gson.toJson(o);
    }

    public static <T> T fromJson(String string, Class<T> tClass) {
        return gson.fromJson(string, tClass);
    }

    public static <T> T fromJson(String json, Type typeOfT) {
        return gson.fromJson(json, typeOfT);
    }
}
