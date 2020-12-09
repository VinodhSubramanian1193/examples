package com.vinteck.example.apiresponseprojection.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Person {
  private String firstName;
  private String lastName;
  private Integer age;
}
