package com.vinteck.example.apiresponseprojection.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.vinteck.example.apiresponseprojection.domain.Address;
import com.vinteck.example.apiresponseprojection.domain.Company;
import com.vinteck.example.apiresponseprojection.domain.Employee;
import com.vinteck.example.apiresponseprojection.domain.Person;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.util.CollectionUtils;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class QueryResolverServiceTest {

  @Mock
  private ObjectMapper objectMapper;
  @InjectMocks
  private QueryResolverService service;

  private String simpleQuery, objectQuery, arrayQuery, fullQuery, payload;
  private Company company, company2;

  @Before
  public void setUp() throws Exception {

    company = Company.builder()
        .name("XYZ")
        .noOfEmployees(10)
        .employees(Arrays.asList(
            Employee.builder()
                .empId(1)
                .person(Person.builder()
                    .firstName("bruno")
                    .lastName("mars")
                    .age(23)
                    .build())
                .build(),
            Employee.builder()
                .empId(2)
                .person(Person.builder()
                    .firstName("charlie")
                    .lastName("chap")
                    .age(25)
                    .build())
                .build()))
        .address(Address.builder()
            .street("abc")
            .area("qwe")
            .zipcode(123)
            .build())
        .build();

    company2 = Company.builder()
        .name("ABC")
        .noOfEmployees(10)
        .employees(Arrays.asList(
            Employee.builder()
                .empId(1)
                .person(Person.builder()
                    .firstName("bruno")
                    .lastName("mars")
                    .age(23)
                    .build())
                .build(),
            Employee.builder()
                .empId(2)
                .person(Person.builder()
                    .firstName("charlie")
                    .lastName("chap")
                    .age(25)
                    .build())
                .build()))
        .address(Address.builder()
            .street("abc")
            .area("qwe")
            .zipcode(123)
            .build())
        .build();
    payload = "{\"name\":\"XYZ\",\"noOfEmployees\":10,\"employees\":[{\"empId\":1,\"person\":{\"firstName\":\"bruno\",\"lastName\":\"mars\",\"age\":23}},{\"empId\":2,\"person\":{\"firstName\":\"charlie\",\"lastName\":\"chap\",\"age\":25}}],\"address\":{\"street\":\"abc\",\"area\":\"qwe\",\"zipcode\":123}}";
    when(objectMapper.writeValueAsString(eq(company)))
        .thenReturn(payload);
  }

  @Test(expected = IllegalArgumentException.class)
  public void resolve_withEmpty() {
    service.resolve(null, company);
  }

  @SneakyThrows
  @Test
  public void resolve_withSimpleQuery() {
    simpleQuery = "{name: name, noOfEmployees: num}";
    String resolvedResponse = service.resolve(simpleQuery, company);
    assertNotNull(resolvedResponse);
    JsonElement element = JsonParser.parseString(resolvedResponse);
    assertNotNull(element);
    assertTrue(element.isJsonObject());
    JsonObject asJsonObject = element.getAsJsonObject();
    Set<String> keySet = asJsonObject.keySet();
    assertFalse(CollectionUtils.isEmpty(keySet));
    assertTrue(keySet.containsAll(Arrays.asList("name", "num")));
    assertEquals("XYZ", asJsonObject.get("name").getAsString());
    assertEquals(10, asJsonObject.get("num").getAsInt());
  }

  @Test
  public void resolve_withObjectQuery() {
    objectQuery = "{name: name, noOfEmployees: num, address: {street: street, area: areaName}}";
    String resolvedResponse = service.resolve(objectQuery, company);
    assertNotNull(resolvedResponse);
    JsonElement element = JsonParser.parseString(resolvedResponse);
    assertNotNull(element);
    assertTrue(element.isJsonObject());
    JsonObject asJsonObject = element.getAsJsonObject();
    Set<String> keySet = asJsonObject.keySet();
    assertFalse(CollectionUtils.isEmpty(keySet));
    assertTrue(keySet.containsAll(Arrays.asList("name", "num", "address")));
    JsonElement jsonElement = asJsonObject.get("address");
    assertTrue(jsonElement.isJsonObject());
    JsonObject addressObject = jsonElement.getAsJsonObject();
    assertNotNull(addressObject);
    Set<String> addressKeySet = addressObject.keySet();
    assertFalse(CollectionUtils.isEmpty(addressKeySet));
    assertTrue(addressKeySet.containsAll(Arrays.asList("street", "areaName")));
    assertFalse(addressKeySet.contains("zipcode"));
    assertEquals("abc", addressObject.get("street").getAsString());
    assertEquals("qwe", addressObject.get("areaName").getAsString());
  }

  @Test
  public void resolve_withArrayQuery() {
    arrayQuery = "{name:name, noOfEmployees:num, employees:[{empId:Id}], address: {street: street, area: area, zipcode: zipcode}}";
    String resolvedResponse = service.resolve(arrayQuery, company);
    assertNotNull(resolvedResponse);
    JsonElement element = JsonParser.parseString(resolvedResponse);
    assertNotNull(element);
    assertTrue(element.isJsonObject());
    JsonObject asJsonObject = element.getAsJsonObject();
    Set<String> keySet = asJsonObject.keySet();
    assertFalse(CollectionUtils.isEmpty(keySet));
    assertTrue(keySet.containsAll(Arrays.asList("name", "num", "address", "employees")));
    JsonElement jsonElement = asJsonObject.get("employees");
    assertTrue(jsonElement.isJsonArray());
    JsonArray employeeJsonArray = jsonElement.getAsJsonArray();
    assertEquals(2, employeeJsonArray.size());
    JsonElement employee1JsonElement = employeeJsonArray.get(0);
    assertTrue(employee1JsonElement.isJsonObject());
    JsonObject employee1JsonObject = employee1JsonElement.getAsJsonObject();
    Set<String> employeeKeySet = employee1JsonObject.keySet();
    assertTrue(employeeKeySet.containsAll(Arrays.asList("Id")));
    assertFalse(employeeKeySet.contains("person"));
  }

  @Test
  public void resolve_withFullQuery() {
    fullQuery = "{name:name, noOfEmployees:num, employees:[{empId:id, person:{firstName:firstName, lastName:surname, age:age}}], address: {street: street, area: area, zipcode: zipcode}}";
    String resolvedResponse = service.resolve(fullQuery, company);
    assertNotNull(resolvedResponse);
    JsonElement element = JsonParser.parseString(resolvedResponse);
    assertNotNull(element);
    assertTrue(element.isJsonObject());
    JsonObject asJsonObject = element.getAsJsonObject();
    Set<String> keySet = asJsonObject.keySet();
    assertFalse(CollectionUtils.isEmpty(keySet));
    assertTrue(keySet.containsAll(Arrays.asList("name", "num", "address", "employees")));
    JsonElement jsonElement = asJsonObject.get("employees");
    assertTrue(jsonElement.isJsonArray());
    JsonArray employeeJsonArray = jsonElement.getAsJsonArray();
    assertEquals(2, employeeJsonArray.size());
    JsonElement employee1JsonElement = employeeJsonArray.get(0);
    assertTrue(employee1JsonElement.isJsonObject());
    JsonObject employee1JsonObject = employee1JsonElement.getAsJsonObject();
    Set<String> employeeKeySet = employee1JsonObject.keySet();
    assertTrue(employeeKeySet.containsAll(Arrays.asList("id", "person")));
    JsonElement personElement = employee1JsonObject.get("person");
    assertTrue(personElement.isJsonObject());
    JsonObject personObject = personElement.getAsJsonObject();
    Set<String> personKeySet = personObject.keySet();
    assertTrue(personKeySet.containsAll(Arrays.asList("firstName", "surname", "age")));
    assertEquals(23, personObject.get("age").getAsInt());
  }

  @SneakyThrows
  @Test
  public void resolve_withArrayJsonQuery() {
    String arrayOfJson = "[{name:companyName, noOfEmployees:num,employees:[{empId:id, person:{firstName:firstName}}], address: {street: street, area: area, zipcode: zipcode}}]";

    String companiesObjectString = "[{\"name\":\"XYZ\",\"noOfEmployees\":10,\"employees\":[{\"empId\":1,\"person\":{\"firstName\":\"bruno\",\"lastName\":\"mars\",\"age\":23}},{\"empId\":2,\"person\":{\"firstName\":\"charlie\",\"lastName\":\"chap\",\"age\":25}}],\"address\":{\"street\":\"abc\",\"area\":\"qwe\",\"zipcode\":123}},{\"name\":\"ABC\",\"noOfEmployees\":10,\"employees\":[{\"empId\":1,\"person\":{\"firstName\":\"bruno\",\"lastName\":\"mars\",\"age\":23}},{\"empId\":2,\"person\":{\"firstName\":\"charlie\",\"lastName\":\"chap\",\"age\":25}}],\"address\":{\"street\":\"abc\",\"area\":\"qwe\",\"zipcode\":123}}]";
    Company[] companies = new Company[]{company, company2};

    doAnswer(invocationOnMock -> {
      try (ByteArrayOutputStream out = invocationOnMock.getArgument(0)) {
        out.write(companiesObjectString.getBytes());
      }
      return null;
    }).when(objectMapper).writeValue(any(OutputStream.class), eq(companies));
    String resolvedResponse = service.resolve(arrayOfJson, companies);
    assertNotNull(resolvedResponse);
    JsonElement element = JsonParser.parseString(resolvedResponse);
    assertNotNull(element);
    assertTrue(element.isJsonArray());
    JsonArray asJsonArray = element.getAsJsonArray();
    assertEquals(2, asJsonArray.size());
    JsonElement jsonArrayElement = asJsonArray.get(0);
    assertTrue(jsonArrayElement.isJsonObject());
    JsonObject asJsonObject = jsonArrayElement.getAsJsonObject();
    Set<String> keySet = asJsonObject.keySet();
    assertFalse(CollectionUtils.isEmpty(keySet));
    assertTrue(keySet.containsAll(Arrays.asList("companyName", "num", "address", "employees")));
  }
}