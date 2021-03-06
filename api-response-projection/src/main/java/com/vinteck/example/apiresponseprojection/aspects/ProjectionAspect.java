package com.vinteck.example.apiresponseprojection.aspects;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.vinteck.example.apiresponseprojection.decorators.Projected;
import com.vinteck.example.apiresponseprojection.services.QueryResolverService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Order(1)
@Component
public class ProjectionAspect {

  @Autowired private QueryResolverService queryResolverService;

  @Around(value = "allControllerEndpoints() && @annotation(projected)")
  public ResponseEntity projectResponseEntity(final ProceedingJoinPoint joinPoint, Projected projected) throws Throwable {

    ResponseEntity result = null;
    try {
      result = (ResponseEntity)joinPoint.proceed();
    } catch (Throwable throwable) {
      throw throwable;
    } finally {
      if(result != null && result.getStatusCode().is2xxSuccessful()) {
        log.debug("Projected args query position: {}", projected.queryAtPosition());
        Object[] joinPointArgs = joinPoint.getArgs();
        String query = (String) joinPointArgs[projected.queryAtPosition()];
        log.info("Projection enabled with query : {}", query);

        log.debug("ResponseObject : {}", result.getBody().toString());
        HttpHeaders headers = new HttpHeaders();
        headers.addAll(result.getHeaders());
        headers.setContentType(MediaType.APPLICATION_JSON);
        try {
          JsonElement resolve = queryResolverService.resolve(query, result.getBody());
          Gson gson = new Gson();
          log.debug("Resolved obj : {}", resolve);
          result = new ResponseEntity(gson.toJson(resolve), headers, result.getStatusCode());
          log.debug("final result {}", result);
        } catch (Exception e) {
          log.error("Could not resolve using query request", e);
          result = new ResponseEntity(e.getLocalizedMessage(), headers, result.getStatusCode());
        }
      }
    }
    return result;
  }

  @Pointcut("execution(* com.vinteck.example.apiresponseprojection.controllers.*.*(..))")
  public void allControllerEndpoints(){
    // no-op
  }

}


