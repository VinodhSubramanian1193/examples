package com.vinteck.example.apiresponseprojection.controllers;

import com.vinteck.example.apiresponseprojection.decorators.Projected;
import com.vinteck.example.apiresponseprojection.domain.Address;
import com.vinteck.example.apiresponseprojection.domain.Company;
import com.vinteck.example.apiresponseprojection.domain.Employee;
import com.vinteck.example.apiresponseprojection.domain.Person;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
public class EmployeeController {

  @Projected(queryAtPosition = 0)
  @PostMapping(value = "/company", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity company(@RequestBody String query) {
    Company company = Company.builder()
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
    return new ResponseEntity(company, HttpStatus.OK);
  }

  @Projected(queryAtPosition = 0)
  @PostMapping(value = "/companies", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity companies(@RequestBody String query) {
    Company company = Company.builder()
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
    Company company2 = Company.builder()
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
    Company[] companies = new Company[]{company, company2};
    return new ResponseEntity(companies, HttpStatus.OK);
  }

  @Projected(queryAtPosition = 0)
  @PostMapping(value = "/companylist", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity companyList(@RequestBody String query) {
    Company company = Company.builder()
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
    List<Company> companyList = Arrays.asList(company);
    return new ResponseEntity(companyList, HttpStatus.OK);
  }

  @Projected(queryAtPosition = 0)
  @GetMapping("/exception")
  public ResponseEntity exception(@RequestParam String query){
    throw new RuntimeException("throwable message");
  }
}
