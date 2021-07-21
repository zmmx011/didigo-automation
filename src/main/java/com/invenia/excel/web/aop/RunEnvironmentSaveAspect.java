package com.invenia.excel.web.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

@Aspect
public class RunEnvironmentSaveAspect {
  @Before(value = "execution(* com.invenia.excel.web.controller.BatchJobController.run*(..))")
  public void saveEnvironment(JoinPoint joinPoint) {
    System.out.println(joinPoint.getArgs()[0]);
  }
}
