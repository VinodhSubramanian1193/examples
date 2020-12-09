package com.vinteck.example.apiresponseprojection.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Company {
  private String name;
  private Integer noOfEmployees;
  private List<Employee> employees;
  private Address address;
}
