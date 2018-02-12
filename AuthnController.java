package com.cargill.webservices;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cargill.service.IAuthnService;

@RestController
@EnableAutoConfiguration
@RequestMapping(value = "/DSCPortal")
public class AuthnController {

  private Logger logger = LoggerFactory.getLogger(AuthnController.class);

  @Autowired
  private IAuthnService authnService;

 
  

}
