package com.vinteck.example.apiresponseprojection.aspects;

import com.vinteck.example.apiresponseprojection.decorators.Projected;
import com.vinteck.example.apiresponseprojection.services.QueryResolverService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Order(1)
@Component
public class ProjectionAspect {

  @Autowired private QueryResolverService queryResolverService;

  @Around(value = "allControllerEndpoints() && @annotation(projected)")
  public ResponseEntity projectResponse(final ProceedingJoinPoint joinPoint, Projected projected) throws Throwable {

    log.debug("Projected args query position: {}", projected.queryAtPosition());
    Object[] joinPointArgs = joinPoint.getArgs();
    String query = (String) joinPointArgs[projected.queryAtPosition()];
    log.info("Projection enabled with query : {}", query);

    ResponseEntity result = null;
    try {
      result = (ResponseEntity)joinPoint.proceed();
    } catch (Throwable throwable) {
      throw throwable;
    } finally {
      if(result != null) {
        log.debug("ResponseObject : {}", result.getBody().toString());
        String resolve = queryResolverService.resolve(query, result.getBody());
        log.debug("Resolved obj : {}", resolve);
        result = new ResponseEntity(resolve, result.getHeaders(), result.getStatusCode());
        log.debug("final result {}", result);
      }
    }
    return result;
  }

  @Pointcut("execution(* com.vinteck.example.apiresponseprojection.controllers.*.*(..))")
  public void allControllerEndpoints(){
    // no-op
  }

}


