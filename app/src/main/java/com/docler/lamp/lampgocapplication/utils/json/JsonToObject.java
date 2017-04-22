package com.docler.lamp.lampgocapplication.utils.json;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.reactivex.functions.Function;

class JsonToObject<T> implements Function<String, T> {

    private final ObjectMapper objectMapper;
    private final Class<T> objectClass;

    public JsonToObject(ObjectMapper objectMapper, Class<T> objectClass) {
        this.objectMapper = objectMapper;
        this.objectClass = objectClass;
    }

    @Override
    public T apply(String jsonString) throws Exception {
        return objectMapper.readValue(jsonString, objectClass);
    }
}
