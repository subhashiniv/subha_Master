package com.cargill.webservices;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.cargill.service.IAppService;

public class AppsController {
  
  private Logger logger = LoggerFactory.getLogger(AppsController.class);

  @Autowired
  private IAppService appService;
  
//  @RequestMapping(
//      value = "/paymentList",
//      method = RequestMethod.GET,
//      produces = DSCConstands.APPLICATION_JSON, consumes=DSCConstands.APPLICATION_JSON)
//  public List<Payment> getPaymentList(@RequestParam("contractId") int contractId,@RequestParam("contractCategory") String contractCategory,@RequestParam("userid") String userid) {
//    logger.info(":::::::::: getPaymentList :::::::::::::::");
//    return regionFactoryService.getPaymentList(contractCategory).getPaymentList(contractId,userid);
//  } 

}
