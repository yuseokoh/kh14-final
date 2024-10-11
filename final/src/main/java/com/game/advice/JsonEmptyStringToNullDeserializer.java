package com.game.advice;

import java.io.IOException;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JsonEmptyStringToNullDeserializer extends JsonDeserializer<String>{
	@Override
	public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {

	String value = p.getText();
	boolean empty = value == null || value.isEmpty();
	return empty ? null : value;
	
	}
}

