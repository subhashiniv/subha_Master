package com.cargill.webservices;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import javax.ws.rs.FormParam;

import com.cargill.service.IUserService;
import com.cargill.util.OneCargillConstants;

@RestController
@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class,HibernateJpaAutoConfiguration.class})
@RequestMapping(value = "/api/v1/users")
public class UserController {

  private Logger logger = LoggerFactory.getLogger(UserController.class);

  @Autowired
  private IUserService userService;
  
  /**
   * Method to insert contract details
   * 
   * @param contract
   * @param contractCategory
   */
 /* @RequestMapping(value = "/User", method = RequestMethod.POST, produces = OneCargillConstants.APPLICATION_JSON, consumes = OneCargillConstants.APPLICATION_JSON)
  public void insertContract(@RequestBody UserProfileWrapper userProfileWrapper) {
    logger.info(":::::::::: insertContract :::::::::::::::"+userProfileWrapper);
   // regionFactoryService.insertContract(contract, contractCategory).insertContract(contract,userid);
  }*/
  
  @RequestMapping(value = "/password", method = RequestMethod.GET, produces = OneCargillConstants.APPLICATION_JSON)
	public String getAuth() {

		//String input = "{ \"token\": \""+token+"\" }";
		System.out.println("getAuth******");
		String oktaUrl = "https://cargillcustomer-qa.oktapreview.com/api/v1/policies?type=PASSWORD";
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new  HttpHeaders();
		headers.add("Accept", "application/json");
		headers.add("Content-Type", "application/json");
		headers.add("Authorization", "SSWS 00CF4t8ih3kaVZN6kPcz5xTbr07LFdSIpcTd0XCcNC");
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		ResponseEntity<String> response = restTemplate.exchange(oktaUrl, HttpMethod.GET, entity, String.class);
		return response.getBody().toString();
	}
  
  @RequestMapping(value = "/authn", method = RequestMethod.POST, produces = OneCargillConstants.APPLICATION_JSON, consumes = "application/x-www-form-urlencoded")
	public String authenticate(@FormParam("client_id") String clientid, @FormParam("username") String username, 
            @FormParam("password") String password) {

		//String input = "{ \"token\": \""+token+"\" }";
		System.out.println("authenticate******"+"clientid"+clientid+"username"+username+"password::"+password);	
		HttpHeaders headers = new  HttpHeaders();
		headers.add("Accept", "application/json");
		headers.add("Content-Type", "application/x-www-form-urlencoded");
		//headers.add("Authorization", "SSWS 00CF4t8ih3kaVZN6kPcz5xTbr07LFdSIpcTd0XCcNC");
		MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();   	    	
    	map.add("client_id", clientid);
    	map.add("username", username);
    	map.add("password", password);
    	map.add("client_secret", "HOlNHwWPahyo_W-nZjNwP4qRQinyRreHqmloOTvK");
    	map.add("scope", "openid email profile");
    	map.add("grant_type", "password");
    	String oktaUrl = "https://cargillcustomer-qa.oktapreview.com/oauth2/v1/token";
		RestTemplate restTemplate = new RestTemplate();
    	HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
		ResponseEntity<String> response = restTemplate.exchange(oktaUrl, HttpMethod.GET, request, String.class);
		return response.getBody().toString();
	}
  
 /* HttpHeaders headers = new HttpHeaders();
  headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

  MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
  map.add("email", "first.last@example.com");

  HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

  ResponseEntity<String> response = restTemplate.postForEntity( url, request , String.class );*/

  

}
