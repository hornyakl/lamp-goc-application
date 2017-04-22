package com.docler.lamp.lampgocapplication.utils.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.functions.Function;

public class JsonFunctionsFactory {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final TypeFactory typeFactory = TypeFactory.defaultInstance();

    public <E> Function<String, Map<String, E>> createJsonToMap(Class<E> valueClass){
        final MapType type = typeFactory.constructMapType(HashMap.class, String.class, valueClass);

        return new JsonToMap<>(objectMapper, type);
    }

    public <E> Function<String, List<E>> createJsonToList(Class<E> valueClass) {
        final CollectionType type = typeFactory.constructCollectionType(ArrayList.class, valueClass);

        return new JsonToList<>(objectMapper, type);
    }

    public <E> Function<String, E> createJsonToObject(final Class<E> objectClass) {
        return new JsonToObject<>(objectMapper, objectClass);
    }
}
