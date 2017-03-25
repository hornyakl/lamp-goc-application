package com.docler.lamp.lampgocapplication.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.functions.Function;

public class JsonFunctions {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final TypeFactory typeFactory = TypeFactory.defaultInstance();

    public <E> Function<String, Map<String, E>> convertToMap(Class<E> valueClass) {
        MapType type = typeFactory.constructMapType(HashMap.class, String.class, valueClass);

        return (String jsonString) -> {
            return objectMapper.readValue(jsonString, type);
        };
    }

    public <E> Function<String, List<E>> convertToList(Class<E> valueClass) {
        CollectionType type = typeFactory.constructCollectionType(ArrayList.class, valueClass);

        return (String jsonString) -> {
            return objectMapper.readValue(jsonString, type);
        };
    }

    public <E> Function<String, E> convertToObject(Class<E> objectClass) {
        return (String jsonString) -> {
            return objectMapper.readValue(jsonString, objectClass);
        };
    }
}
