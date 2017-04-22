package com.docler.lamp.lampgocapplication.utils.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;

import java.util.Map;

import io.reactivex.functions.Function;

class JsonToMap<T> implements Function<String, Map<String, T>> {

    private final ObjectMapper objectMapper;
    private final MapType type;

    public JsonToMap(ObjectMapper objectMapper, MapType type) {
        this.objectMapper = objectMapper;
        this.type = type;
    }

    public Map<String, T> apply(String jsonString) throws Exception {
        return objectMapper.readValue(jsonString, type);
    }
}
