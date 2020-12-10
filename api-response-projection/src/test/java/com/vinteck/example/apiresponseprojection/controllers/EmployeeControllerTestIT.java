package com.vinteck.example.apiresponseprojection.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.SneakyThrows;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Set;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class EmployeeControllerTestIT {

  @Autowired EmployeeController employeeController;
  private String query;

  @SneakyThrows
  @Test
  public void company() {
    query = "{name:name, noOfEmployees:num, employees:[{empId:Id}], address: {street: street, area: area, zipcode: zipcode}}";
    ResponseEntity responseEntity = employeeController.company(query);
    System.out.println(responseEntity.getStatusCode());
    System.out.println(responseEntity.getBody());
    ObjectMapper mapper = new ObjectMapper();
    Object o = mapper.readValue((String) responseEntity.getBody(), Object.class);
    String string = mapper.writeValueAsString(o);
    JsonElement jsonElement = JsonParser.parseString(string);
    assertTrue(jsonElement.isJsonObject());
    JsonObject asJsonObject = jsonElement.getAsJsonObject();
    Set<String> keySet = asJsonObject.keySet();
    assertTrue(keySet.containsAll(Arrays.asList("name", "num", "employees", "address")));
    JsonElement employeesElement = asJsonObject.get("employees");
    assertTrue(employeesElement.isJsonArray());
    JsonArray asJsonArray = employeesElement.getAsJsonArray();
    assertEquals(2, asJsonArray.size());
    JsonElement employeeArrayElement = asJsonArray.get(0);
    assertTrue(employeeArrayElement.isJsonObject());
    JsonObject employeeObject = employeeArrayElement.getAsJsonObject();
    Set<String> employeeKeySet = employeeObject.keySet();
    assertTrue(employeeKeySet.contains("Id"));
    assertFalse(employeeKeySet.contains("person"));
  }
}