package com.vinteck.example.apiresponseprojection.controllers;

import com.vinteck.example.apiresponseprojection.domain.Address;
import com.vinteck.example.apiresponseprojection.domain.Company;
import com.vinteck.example.apiresponseprojection.domain.Employee;
import com.vinteck.example.apiresponseprojection.domain.Person;
import com.vinteck.example.apiresponseprojection.services.QueryResolverService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.yaml.snakeyaml.resolver.Resolver;

import java.util.Arrays;

@Slf4j
@RestController
public class EmployeeController {

  @Autowired
  private QueryResolverService queryResolverService;

  @PostMapping(value = "/company/{companyId}", consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity getCompany(@PathVariable Integer companyId,
                                   @RequestBody String query) {
    log.info("Company requested for {}", companyId);
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
    String resolve = queryResolverService.resolve(query, company);
    return new ResponseEntity(resolve, HttpStatus.OK);
  }
}
