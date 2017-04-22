package com.docler.lamp.lampgocapplication.utils.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

import java.util.List;

import io.reactivex.functions.Function;

class JsonToList<T> implements Function<String, List<T>> {

    private final ObjectMapper objectMapper;
    private final CollectionType type;

    public JsonToList(ObjectMapper objectMapper, CollectionType type) {
        this.objectMapper = objectMapper;
        this.type = type;
    }

    @Override
    public List<T> apply(String jsonString) throws Exception {
        return objectMapper.readValue(jsonString, type);
    }
}