package com.vinteck.example.apiresponseprojection.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Slf4j
@Service
public class QueryResolverService {

  @Autowired
  private ObjectMapper objectMapper;

  public <T> JsonElement resolve(String query, T payload) {
    log.info("Starting query resolver for response type:{}.", payload.getClass().getSimpleName());
    if (StringUtils.isEmpty(query))
      throw new IllegalArgumentException("Query to map should not be null");
    if (!(payload instanceof Object))
      throw new IllegalArgumentException("Payload to be processed is not a java object");
    String payloadString = null;
    try {
      if(payload.getClass().isArray()) {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        objectMapper.writeValue(out, payload);
        final byte[] data = out.toByteArray();
        payloadString = new String(data);
      } else {
        payloadString = objectMapper.writeValueAsString(payload);
      }
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Not a well formed json", e);
    } catch (IOException e) {
      e.printStackTrace();
    }

    JsonElement payloadElement = JsonParser.parseString(payloadString);
    JsonElement queryElement = JsonParser.parseString(query);

    JsonElement response = parseJsonElements(queryElement, payloadElement);
    log.info("Query resolved successfully");
    return response;
  }

  private JsonElement parseJsonElements(JsonElement queryElement, JsonElement payloadElement) {
    JsonObject response = new JsonObject();
    if(queryElement.isJsonObject() && payloadElement.isJsonObject()) {
      JsonObject asJsonObject = queryElement.getAsJsonObject();
      JsonObject payloadJsonObject = payloadElement.getAsJsonObject();
      asJsonObject.entrySet().stream().forEach(element -> {
        String key1 = element.getKey();
        if(!payloadJsonObject.has(key1)) throw new RuntimeException("Not a valid query. please check");

        JsonElement payloadJsonSubElement = payloadJsonObject.get(key1);
        if(payloadJsonSubElement.isJsonPrimitive()){
          //setting the key as the value to support alias for primitive types
          response.addProperty(element.getValue().getAsString(), payloadJsonSubElement.getAsString());
        } else if (payloadJsonSubElement.isJsonObject()){
          response.add(element.getKey(),
              parseJsonElements(element.getValue(), payloadJsonSubElement));
        } else if (payloadJsonSubElement.isJsonArray()){
          response.add(element.getKey(),
              parseJsonArray(element.getValue(), payloadJsonSubElement));
        }
      });
    } else if(queryElement.isJsonArray() && payloadElement.isJsonArray()){
      return parseJsonArray(queryElement, payloadElement);
    } else {
      throw new RuntimeException("Not a valid query or payload response");
    }
    return response;
  }

  private JsonElement parseJsonArray(JsonElement queryJsonElement, JsonElement payloadJsonElement) {
      if(!queryJsonElement.isJsonArray()) throw new RuntimeException("Not a valid query. please check");
      JsonArray asJsonArray = payloadJsonElement.getAsJsonArray();
      JsonArray jsonObjects = new JsonArray();
      for (JsonElement arrayElement: asJsonArray) {
        jsonObjects.add(parseJsonElements(queryJsonElement.getAsJsonArray().get(0), arrayElement));
      }
    return jsonObjects;
  }
}
