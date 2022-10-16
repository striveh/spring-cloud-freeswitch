package com.striveh.callcenter.common.base.controller;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;


public class CustomJsonDateDeserializer extends JsonDeserializer<Timestamp> {
  
    @Override  
    public Timestamp deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = jp.getText();
        try {
            return new Timestamp(format.parse(date).getTime());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}  