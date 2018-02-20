package com.serverless;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.impl.client.HttpClients;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class Handler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {	
	
	private LambdaLogger logger = null;	
    private Map<String,String> map = new HashMap<String,String>();	
	
    
	@Override
    public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {

    	String emailAddress = "";
    	String serviceId = "";
		String requestType = "";
		String adminEmail = "";
        String taxId = "";
        String firstName = "";
        String lastName = "";
        String email = "";
        String locale = "";
        String login = "";
        String userId = "";
        String password="";
        String answer ="";
		String question = "";
		String oktaUserId = "";
		String countryCode = "";
		String phoneNumber = "";
		String userPassCode = "";
		String userToken = "";
		String factorId = "";
		String activateFactorId = "";
		String deactivateStatus = "";
		String userRole = "";
		String tableName = "";
		String columnName = "";
		String groupId = "";
		
		logger = context.getLogger();
		ApiGatewayResponse apiGatewayResponse = null;

		if (input != null) {
			String headerInfo = input.get("headers").toString();	
			
		

			if (headerInfo != null) {
				buildRequestHeaders(headerInfo);
				emailAddress = getHeaderValue("email-address");
				serviceId = getHeaderValue("service-id");
				requestType = getHeaderValue("request-type");
				adminEmail = getHeaderValue("admin-email");
				taxId = getHeaderValue("tax-id");
				userId = getHeaderValue("user-id");
				deactivateStatus = getHeaderValue("deactivate-status"); 
				userRole = getHeaderValue("user-role");		
				// Okta headers
                login = getHeaderValue("login");
                locale = getHeaderValue("locale");
                email = getHeaderValue("email");
                firstName = getHeaderValue("first-name");
                lastName = getHeaderValue("last-name");
                password = getHeaderValue("password");
				answer = getHeaderValue("answer");
				question = getHeaderValue("question");
				oktaUserId = getHeaderValue("oktauserid");
				countryCode = getHeaderValue("countrycode");
				phoneNumber = getHeaderValue("phonenumber");
				userPassCode = getHeaderValue("userpasscode");
				userToken = getHeaderValue("usertoken");
				factorId = getHeaderValue("factorid");
				activateFactorId = getHeaderValue("activatefactorid");
				tableName = getHeaderValue("tablename");
				columnName = getHeaderValue("columnname");
				groupId = getHeaderValue("groupid");
				

                logger.log("Admin email: " +  adminEmail);
                logger.log("Email Address: " +  emailAddress);
                logger.log("ServiceId: " + serviceId);
                logger.log("TaxId: " + taxId);
                logger.log("requestType: " +  requestType);
				logger.log("userId: " +  userId);
				logger.log("user-role: " +  userRole);
				

                logger.log("login: " +  login);
                logger.log("locale: " +  locale);
                logger.log("email: " + email);
                logger.log("firstName: " + firstName);
                logger.log("lastName: " +  lastName);
                logger.log("password: " + password);
                logger.log("question: " +  question);
                logger.log("answer: " +  answer);
                logger.log("oktaUserId: " + oktaUserId);
                logger.log("countryCode: " +  countryCode);
                logger.log("phoneNumber: " + phoneNumber);
                logger.log("userPassCode: " +  userPassCode);
                logger.log("usertoken: " +  userToken);
                logger.log("factorId: " +  factorId);
                logger.log("activateFactorId: " +  activateFactorId);
				logger.log("deactivate-status: " +  deactivateStatus);
				logger.log("tableName: " +  tableName);
				logger.log("columnName: " +  columnName);
				logger.log("ppty fileName ***: " + System.getenv("propertyfile"));
			}
			
			if (input.get("queryStringParameters") != null) {
				String params = input.get("queryStringParameters").toString();
				groupId = extractField("groupid", params);
			}
		}

		Integer returnCode = 200;

		if (requestType.equals("adduser")) { // Add a user
			try {
				apiGatewayResponse = createOktaUser(firstName, lastName, emailAddress, locale, groupId);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return apiGatewayResponse;

		}
		  else if (requestType.equals("userregistration")) {  	//User registration

			try {
				apiGatewayResponse = createOktaUserRegistration(password,question,answer,oktaUserId);
			} catch (Exception e) {			
				e.printStackTrace();
			}				
			return apiGatewayResponse;

		} else if (requestType.equals("enrollsmsfactor")) {  	//Enroll mobile number

			try {
				apiGatewayResponse = enrollOktaSMSFactor(countryCode, phoneNumber, oktaUserId);
			} catch (Exception e) {				
				e.printStackTrace();
			}

			return apiGatewayResponse;

		}else if (requestType.equals("activatesmsfactorpasscode")) {  	//activate the SMS factor for first time during user registration
		
			try {
				apiGatewayResponse = activateSMSFactor(oktaUserId, factorId, userPassCode);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return apiGatewayResponse;

		}
		/*else if (requestType.equals("resendpasscode")) {  	// if passcode is expired by 30 seconds, request for resend 
		
			try {
				apiGatewayResponse = resendPasscode(oktaUserId, factorId);
			} catch (Exception e) {				
				e.printStackTrace();
			}

			return apiGatewayResponse;

		}*/else if (requestType.equals("verifypasscode")) {  	// verify the passcode that has been received through resend API
		
			try {
				apiGatewayResponse = verifyPassCode(oktaUserId,activateFactorId, userPassCode);
			} catch (Exception e) {
				e.printStackTrace();
			}

			return apiGatewayResponse;

		} else if (requestType.equals("getactiveappnames")) {  	//To get the application names that are assigned to user
			
			try {
				apiGatewayResponse = getActiveApplicationNames(oktaUserId);
			} catch (Exception e) {				
				e.printStackTrace();
			}

			return apiGatewayResponse;

		} else if (requestType.equals("getuseridfromtoken")) {  	// To get the user id from the token received in welcome/activation mail			
			try {
				apiGatewayResponse = getUserIdByToken(userToken);
			} catch (Exception e) {
				e.printStackTrace();
			}

			return apiGatewayResponse;

		} else if (requestType.equals("enrolledmobilenumber")) {  	// To get the enrolled mobile number that has been registered through enroll sms factor API			
			try {
				apiGatewayResponse = getUserEnrolledFactor(oktaUserId);
			} catch (Exception e) {
				e.printStackTrace();
			}

			return apiGatewayResponse;

		} else if (requestType.equals("resetpasswordemail")) {  	// To reset user's password			
			try {
				apiGatewayResponse = sendResetPasswordEmail(emailAddress);
			} catch (Exception e) {
				e.printStackTrace();
			}

			return apiGatewayResponse;

		}else if (requestType.equals("passwordpolicydetails")) {  	//To get the default password policy set in the okta domain

			try {
				apiGatewayResponse = getPasswordPolicy();
			} catch (Exception e) {
				e.printStackTrace();
			}

			return apiGatewayResponse;

		}else if (requestType.equals("verifysecurityanswer")) {  	//To verify  security answer

			try {
				apiGatewayResponse = verifySecurityAnswer(userToken, answer);
			} catch (Exception e) {
				e.printStackTrace();
			}

			return apiGatewayResponse;

		}else if (requestType.equals("verifypassword")) {  	//To verifyPassword

			try {
				apiGatewayResponse = verifyPassword(userToken, password);
			} catch (Exception e) {
				e.printStackTrace();
			}

			return apiGatewayResponse;

		}else if (requestType.equals("deletesmsfactor")) {  	//To delete the sms factor enrolled

			try {
				apiGatewayResponse =  deleteUserSMSFactor(oktaUserId, factorId);
			} catch (Exception e) {
				e.printStackTrace();
			}

			return apiGatewayResponse;

		} else if (requestType.equals("updatemobilenumber")) {  	//To update the user's mobile number

			try {
				apiGatewayResponse = updateMobileNumber(oktaUserId, countryCode, phoneNumber);
			} catch (Exception e) {				
				e.printStackTrace();
			}

			return apiGatewayResponse;

		} else if (requestType.equals("deactivateoktauser")) {  	//Deactivation Status update

			try {
				apiGatewayResponse = deactivateOktaUser(oktaUserId);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return apiGatewayResponse;

		}else if (requestType.equals("deleteoktauser")) {  	//To delete the test user in prod

			try {
				apiGatewayResponse = deleteOktaUser(oktaUserId);
			} catch (Exception e) {				
				e.printStackTrace();
			}

			return apiGatewayResponse;

		} else if (requestType.equals("activateoktauser")) {  	//Activate Okta user

			try {
				apiGatewayResponse = activateOktaUser(oktaUserId);
			} catch (Exception e) {
				e.printStackTrace();
			}			

			return apiGatewayResponse;

		}  else if (requestType.equals("oktauseridbyemail")) {  	
			try {
				apiGatewayResponse = getOktaUserIdByEmail(emailAddress);
			} catch (Exception e) {
				e.printStackTrace();
			}

			return apiGatewayResponse;

		} else if (requestType.equals("getoktauserrole")) {  	
			try {
				apiGatewayResponse = getOktaUserRole(oktaUserId);
			} catch (Exception e) {
				e.printStackTrace();
			}

			return apiGatewayResponse;

		}else if (requestType.equals("getuserlistingroup")) {  	
			try {
				apiGatewayResponse = getUserListInGroup(groupId);
			} catch (Exception e) {
				e.printStackTrace();
			}

			return apiGatewayResponse;

		} else if (requestType.equals("updateoktauser")) {  	//update a user in Okta
			String responseBody = "";
			try {
				responseBody = updateOktaUser(firstName, lastName, oktaUserId);
			} catch (Exception e) {			
				e.printStackTrace();
			}
			return ApiGatewayResponse.builder()
					.setStatusCode(returnCode)
					.setObjectBody(responseBody)
					.setHeaders(Collections.singletonMap("Access-Control-Allow-Origin", "*"))
					.build();

		}   else {    //Do a SELECT			
			return ApiGatewayResponse.builder()
					.setStatusCode(returnCode)
					.setObjectBody(" No results found")
					.setHeaders(Collections.singletonMap("Access-Control-Allow-Origin", "*"))
					.build();
		}
    }
    
  	private String extractField(String field, String info) {

		String fieldData = "";

		Integer startPosition = info.indexOf(field);
		if (startPosition > 0) {

			startPosition = startPosition + field.length() + 1;  //skip field and = sign

			Integer endPosition = info.indexOf(',', startPosition); //find next comma

			//if no comma for end position, look for close curly
			if (endPosition <= startPosition) endPosition = info.indexOf('}', startPosition);

			if (endPosition > startPosition)
				fieldData = info.substring(startPosition, endPosition);
		}

		return fieldData;
	}


    /**
     * Create Okta User
     * @param firstName
     * @param lastName
     * @param email
     * @param logger
     * @return
    }
     *
     */
    protected ApiGatewayResponse createOktaUser(final String firstName, final String lastName, final String emailAddress, final String locale, final String groupId) throws Exception {

    	ApiGatewayResponse apiGatewayResponse = null;
    	RestTemplate restTemplate = new RestTemplate();        

        String oktaUrl = CFUserConstants.OKTA_DOMAIN_NAME+"/api/v1/users?activate=true";       
      
        String input ="{\"profile\": { \"firstName\":\""+ firstName + "\",\"lastName\":\""+ lastName +"\", \"email\":\""+ emailAddress +"\", \"locale\": \""+locale+"\",\"login\":\""+ emailAddress +"\" }, \"groupIds\":[\""+groupId+"\"]}";

        logger.log("input: " + input);
        
        HttpHeaders headers = buildOktaHeaders();
        HttpEntity<String> entity = new HttpEntity<String>(input, headers);
        try {
        	restTemplate.setErrorHandler(new CustomResponseErrorHandler());
            ResponseEntity<String> response = restTemplate.exchange(oktaUrl, HttpMethod.POST, entity, String.class);
            if (response != null) {
            	apiGatewayResponse = setResponse(response.getStatusCode().toString(), response.getBody());
            }
        }
        catch (Exception e) {
        	apiGatewayResponse = setResponse(getStatusCode(e), e.getMessage());
        }
        return apiGatewayResponse;
    }

    /**
     * Build headers for Okta
     * @return
     */
    private HttpHeaders buildOktaHeaders(){
        HttpHeaders headers = new HttpHeaders();
        //OKTA DEV        
        headers.add(CFUserConstants.CONTENT_TYPE, CFUserConstants.APPLICATION_JSON);
        headers.add(CFUserConstants.ACCEPT, CFUserConstants.APPLICATION_JSON);
        headers.add(CFUserConstants.AUTHORIZATION, CFUserConstants.OKTA_API_TOKEN);
        //headers.add("token", CFUserConstants.OKTA_TOKEN);
        
      //OKTA QA
       /* headers.add("Content-Type", "application/json");
        headers.add("Accept", "application/json");
        headers.add("Authorization", oktaApiToken);
        headers.add("token", "00REwcGFhRIVwgChDd2Xc1erNDoIWbh0pFoTkxVK_s");*/

//        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
//        headers.add("Authorization", "SSWS 00-UOboUf7HlIKspzzkwqgWi1jyjs3wTG2Jjast7d2");
        return headers;
    }
    
    

	


    /**
     * To create the user account in okta as part of user registration process.
     * @param password
     * @param question
     * @param answer
     * @param userid
     * @param logger
     * @return
     * @throws Exception
     */
	protected ApiGatewayResponse createOktaUserRegistration(final String password, final String question, final String answer, final String userid) throws Exception {
    	ApiGatewayResponse apiGatewayResponse = null;
    	RestTemplate restTemplate = new RestTemplate();
        String oktaUrl = CFUserConstants.OKTA_DOMAIN_NAME+"/api/v1/users/"+userid;
        String input ="{\"credentials\": { \"password\" : { \"value\": \""+decodeString(password)+"\" },  \"recovery_question\": { \"question\": \""+question+"\", \"answer\": \""+decodeString(answer)+"\" }}}";
        logger.log("creatrOktaUserregistration oktaUrl: " + oktaUrl);                
        HttpHeaders headers = buildOktaHeaders();
        HttpEntity<String> entity = new HttpEntity<String>(input, headers);
        try { 
        	restTemplate.setErrorHandler(new CustomResponseErrorHandler());
            ResponseEntity<String> response = restTemplate.exchange(oktaUrl, HttpMethod.POST, entity, String.class);           
            if (response != null) {
            	apiGatewayResponse = setResponse(response.getStatusCode().toString(), response.getBody());
            }
            
        }
        catch (Exception e) {
        	apiGatewayResponse = setResponse(getStatusCode(e), e.getMessage());
        }
        return apiGatewayResponse;
    }

    /**
     * Updte Okta User first name and last name
     * @param firstName
     * @param lastName
     * @param email
     * @param logger
     * @return
    }
     *
     */
    protected String updateOktaUser(final String firstName, final String lastName, final String id) throws Exception {

        String result="";
        RestTemplate restTemplate = new RestTemplate();
        String oktaUrl = CFUserConstants.OKTA_DOMAIN_NAME+"/api/v1/users/" + id;
        String input ="{\"profile\": { \"firstName\":\""+ firstName + "\",\"lastName\":\""+ lastName +"\" }}";
        logger.log("input: " + input);
        
        HttpHeaders headers = buildOktaHeaders();
        HttpEntity<String> entity = new HttpEntity<String>(input, headers);
        try {
            ResponseEntity<String> response = restTemplate.exchange(oktaUrl, HttpMethod.POST, entity, String.class);
            if (response != null) {
                result = response.toString();
            }
        }
        catch (Exception e) {
            String err = "Rest Client Exception - updateOktaUser: " + e.getMessage();
            logger.log(err);
            throw new Exception(err);
        }

        return result;
    }
	
	/**
	 * To decode the input parameter received
	 * @param input
	 * @return
	 */
	private String decodeString(String input) {
		byte[] base64decodedBytes = Base64.getDecoder().decode(input);
		String decodedValue = "";
		try {
			decodedValue = new String(base64decodedBytes, "utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return decodedValue;
	}
    
    /**
     * To register the user's mobile number in okta as part of user registration process.
     * 
     * Note: This method will be invoked during first time mobile number enrollment only, for the consecutive calls,
     *  the resend and verify passcode API will be invoked.
     *  
     * @param countryCode
     * @param phoneNumber
     * @param userid
     * @param logger
     * @return
     * @throws Exception
     */
    protected ApiGatewayResponse enrollOktaSMSFactor(final String countryCode, final String phoneNumber, final String userid) throws Exception {
    	ApiGatewayResponse apiGatewayResponse = null;
        RestTemplate restTemplate = new RestTemplate();
        String oktaUrl = CFUserConstants.OKTA_DOMAIN_NAME+"/api/v1/users/"+userid+"/factors?updatePhone=true"; 
        String input ="{\"factorType\": \"sms\",  \"provider\": \"OKTA\", \"profile\": { \"phoneNumber\" :  \""+"+"+countryCode+" "+phoneNumber+"\"}}";
        logger.log("enrollOktaSMSFactor oktaUrl"+oktaUrl);
        logger.log("enrollOktaSMSFactor input: " + input);        
        HttpHeaders headers = buildOktaHeaders();
        HttpEntity<String> entity = new HttpEntity<String>(input, headers);
        try {
        	restTemplate.setErrorHandler(new CustomResponseErrorHandler());
            ResponseEntity<String> response = restTemplate.exchange(oktaUrl, HttpMethod.POST, entity, String.class);
            if (response != null) {
            	apiGatewayResponse = setResponse(response.getStatusCode().toString(), response.getBody());
            }
        }
        catch (Exception e) {
        	apiGatewayResponse = setResponse(getStatusCode(e), e.getMessage());
        }
        return apiGatewayResponse;
    }
    
    /**
     * To verify the passcode that has been received via enroll sms factor API
     * 
     * Note: This method will be invoked during first time mobile number enrollment only, for the consecutive calls,
     *  the resend and verify passcode API will be invoked.
     * 
     * @param userid
     * @param factorid
     * @param passCode
     * @param logger
     * @return
     * @throws Exception
     */
   protected ApiGatewayResponse activateSMSFactor(final  String userid, final String factorid, final String passCode) throws Exception {
    	ApiGatewayResponse apiGatewayResponse = null;
    	RestTemplate restTemplate = new RestTemplate();
    	String input = "";
        String oktaUrl = CFUserConstants.OKTA_DOMAIN_NAME+"/api/v1/users/"+userid+"/factors/"+factorid+"/lifecycle/activate";		
		input = "{ \"passCode\": \"" + passCode + "\" }";		
        logger.log("activateSMSFactor oktaUrl"+oktaUrl);
        logger.log("activateSMSFactor input: " + input);        
        HttpHeaders headers = buildOktaHeaders();
        HttpEntity<String> entity = new HttpEntity<String>(input, headers);
        ResponseEntity<String> response = null;
        try {
        	 restTemplate.setErrorHandler(new CustomResponseErrorHandler());
             response = restTemplate.exchange(oktaUrl, HttpMethod.POST, entity, String.class);          
           if (response != null) {
            	apiGatewayResponse = setResponse(response.getStatusCode().toString(), response.getBody());
           }
        }
      
        catch (Exception e) {    	
        	apiGatewayResponse = setResponse(getStatusCode(e), e.getMessage());
        }
        return apiGatewayResponse;
    }
    
    /**
     * Created a generic method to handle the response for okta API calls
     * @param statusCode
     * @param responseBody
     * @return
     */
    private ApiGatewayResponse setResponse(String statusCode, String responseBody) {
    return ApiGatewayResponse.builder()
    		
			.setStatusCode(Integer.valueOf(statusCode))

			.setObjectBody(responseBody)

			.setHeaders(Collections.singletonMap("Access-Control-Allow-Origin", "*"))

			.build();
    }
    
     /**
      * Class to handle Errors that are thrown by okta API calls
      * @author s445100
      *
      */
    public class CustomResponseErrorHandler implements ResponseErrorHandler {
    	
	@Override
	public void handleError(ClientHttpResponse clienthttpresponse) throws IOException {
		if (clienthttpresponse.getStatusCode() == HttpStatus.FORBIDDEN) {
		logger.log(clienthttpresponse.getStatusCode().toString());
		}
	}

	@Override
	public boolean hasError(ClientHttpResponse clienthttpresponse) throws IOException {
		if (clienthttpresponse.getStatusCode() != HttpStatus.OK) {
			if (clienthttpresponse.getStatusCode() == HttpStatus.FORBIDDEN) {				
				return true;
			}
		}
		return false;
	}

}
    
    /**
     * To resend the passcode to the user
     * @param userid
     * @param factorId
     * @param logger
     * @return
     * @throws Exception
     */
    /*protected ApiGatewayResponse resendPasscode(final String userid, final String factorId) throws Exception {
    	ApiGatewayResponse apiGatewayResponse = null;
        RestTemplate restTemplate = new RestTemplate();
        String oktaUrl = CFUserConstants.OKTA_DOMAIN_NAME+"/api/v1/users/"+userid+"/factors/"+factorId+"/resend";         
        logger.log("resendPasscode oktaUrl"+oktaUrl);       
        HttpHeaders headers = buildOktaHeaders();
        HttpEntity<String> entity = new HttpEntity<String>(headers);
        ResponseEntity<String> response = null;
        try {
        	restTemplate.setErrorHandler(new CustomResponseErrorHandler());
            response = restTemplate.exchange(oktaUrl, HttpMethod.POST, entity, String.class);
            if (response != null) {
            	apiGatewayResponse = setResponse(response.getStatusCode().toString(), response.getBody());
            }
        }
        catch (Exception e) {
        	apiGatewayResponse = setResponse(getStatusCode(e), e.getMessage());
        }
        return apiGatewayResponse;
    }*/
    
    
    /**
     * To verify the passcode that has been received through the resend okta API call
     * @param userid
     * @param resendFactorId
     * @param passCode
     * @param logger
     * @return
     * @throws Exception
     */
    protected ApiGatewayResponse verifyPassCode(final String userid, final String resendFactorId, final String passCode) throws Exception {
    	ApiGatewayResponse apiGatewayResponse = null;
        RestTemplate restTemplate = new RestTemplate(); 
        String oktaUrl = CFUserConstants.OKTA_DOMAIN_NAME+"/api/v1/users/"+userid+"/factors/"+resendFactorId+"/verify";
        String input= ""; 
        if (null == passCode || passCode.equals("")) {
			input = "{}";
		} else {
			input ="{ \"passCode\": \""+passCode+"\" }";  
		}
        logger.log("verifyPasscode oktaUrl"+oktaUrl);
        logger.log("verifyPasscode input"+oktaUrl);
        HttpHeaders headers = buildOktaHeaders();
        HttpEntity<String> entity = new HttpEntity<String>(input, headers);
        try {       
        	restTemplate.setErrorHandler(new CustomResponseErrorHandler());
        	ResponseEntity<String> response = restTemplate.exchange(oktaUrl, HttpMethod.POST, entity, String.class);
            if (response != null) {
            	apiGatewayResponse = setResponse(response.getStatusCode().toString(), response.getBody());
            }
        }
        catch (Exception e) {
        	apiGatewayResponse = setResponse(getStatusCode(e), e.getMessage());
        }
        return apiGatewayResponse;
    }    
   
    /**
     * To get the active application names that are assigned to user
     * @param userid
     * @param logger
     * @return
     * @throws Exception
     */
    protected ApiGatewayResponse getActiveApplicationNames(final String userid) throws Exception {
    	ApiGatewayResponse apiGatewayResponse = null;
        RestTemplate restTemplate = new RestTemplate();        
        String oktaUrl = CFUserConstants.OKTA_DOMAIN_NAME+"/api/v1/apps?filter=user.id eq \""+ userid+"\"&expand=user/"+userid;  
        logger.log("getActiveApplicationNames oktaUrl"+oktaUrl);        
        HttpHeaders headers = buildOktaHeaders();
        HttpEntity<String> entity = new HttpEntity<String>(headers);
        try {
        	restTemplate.setErrorHandler(new CustomResponseErrorHandler());
            ResponseEntity<String> response = restTemplate.exchange(oktaUrl, HttpMethod.GET, entity, String.class);
            if (response != null) {
            	apiGatewayResponse = setResponse(response.getStatusCode().toString(), response.getBody());
            }
        }
        catch (Exception e) {
        	apiGatewayResponse = setResponse(getStatusCode(e), e.getMessage());
        }
        return apiGatewayResponse;
    }
    
    
    /**
     * To get the user id using token. This token has been already received in welcome/activation email.
     * @param token
     * @param logger
     * @return
     * @throws Exception
     */
    protected ApiGatewayResponse getUserIdByToken(final String token) throws Exception {
    	ApiGatewayResponse apiGatewayResponse = null;
          
        String oktaUrl = CFUserConstants.OKTA_DOMAIN_NAME+"/api/v1/authn/recovery/token";  
        String input ="{ \"recoveryToken\": \""+token+"\" }"; 
        logger.log("getUserIdFromToken oktaUrl"+oktaUrl);    
        logger.log("input ::"+input);
        HttpHeaders headers = buildOktaHeaders();
        HttpEntity<String> entity = new HttpEntity<String>(input, headers);
        try {
        	ClientHttpRequestFactory requestFactory = new     
        		      HttpComponentsClientHttpRequestFactory(HttpClients.createDefault());
        	RestTemplate restTemplate = new RestTemplate(requestFactory); 
        	restTemplate.setErrorHandler(new CustomResponseErrorHandler());
        	ResponseEntity<String> response = restTemplate.exchange(oktaUrl, HttpMethod.POST, entity, String.class);
        	//ResponseEntity<String> response = restTemplate.postForObject(oktaUrl, entityString.class)
            if (response != null) {;
            	apiGatewayResponse = setResponse(response.getStatusCode().toString(), response.getBody());
            }
        } catch (HttpClientErrorException e) {
        	apiGatewayResponse = setResponse(getStatusCode(e), e.getMessage());
        } catch (HttpServerErrorException e) {
        	apiGatewayResponse = setResponse(getStatusCode(e), e.getMessage());
        } catch (Exception e) {
        	apiGatewayResponse = setResponse(getStatusCode(e), e.getMessage());
        }
        return apiGatewayResponse;
    }
    
    /**
     * To get the active application names that are assigned to user
     * @param userid
     * @param logger
     * @return
     * @throws Exception
     */
    protected ApiGatewayResponse getUserEnrolledFactor(final String userid) throws Exception {
    	ApiGatewayResponse apiGatewayResponse = null;
        RestTemplate restTemplate = new RestTemplate();   
        String oktaUrl = CFUserConstants.OKTA_DOMAIN_NAME+"/api/v1/users/"+userid+"/factors"   ;       
        logger.log("getUserMobileNumber oktaUrl"+oktaUrl);        
        HttpHeaders headers = buildOktaHeaders();
        HttpEntity<String> entity = new HttpEntity<String>(headers);
        try {
        	restTemplate.setErrorHandler(new CustomResponseErrorHandler());
            ResponseEntity<String> response = restTemplate.exchange(oktaUrl, HttpMethod.GET, entity, String.class);
            if (response != null) {
            	apiGatewayResponse = setResponse(response.getStatusCode().toString(), response.getBody());
            }
        }
        catch (Exception e) {
        	apiGatewayResponse = setResponse(getStatusCode(e), e.getMessage());
        }
        return apiGatewayResponse;
    }
    
    /**
     * To delete the sms factor that has been enrolled by user
     * @param userid
     * @param logger
     * @return
     * @throws Exception
     */
    protected ApiGatewayResponse deleteUserSMSFactor(final String userid, String factorId) throws Exception {
    	ApiGatewayResponse apiGatewayResponse = null;
        RestTemplate restTemplate = new RestTemplate();   
        String oktaUrl = CFUserConstants.OKTA_DOMAIN_NAME+"/api/v1/users/"+userid+"/factors/"+factorId ;       
        logger.log("deleteUserSMSFactor oktaUrl"+oktaUrl);        
        HttpHeaders headers = buildOktaHeaders();
        HttpEntity<String> entity = new HttpEntity<String>(headers);
        try {
        	restTemplate.setErrorHandler(new CustomResponseErrorHandler());
            ResponseEntity<String> response = restTemplate.exchange(oktaUrl, HttpMethod.DELETE, entity, String.class);
            if (response != null) {
            	apiGatewayResponse = setResponse(response.getStatusCode().toString(), response.getBody());
            }
        }
        catch (Exception e) {
        	apiGatewayResponse = setResponse(getStatusCode(e), e.getMessage());
        }
        return apiGatewayResponse;
    }
    
    /**
     * To enroll for sms factor with new mobile number
     * @param userid
     * @param logger
     * @return
     * @throws Exception
     */
    protected ApiGatewayResponse updateMobileNumber(final String userid, String countryCode, String phoneNumber) throws Exception {
    	ApiGatewayResponse apiGatewayResponse = null;
        RestTemplate restTemplate = new RestTemplate();   
        String oktaUrl = CFUserConstants.OKTA_DOMAIN_NAME+"/api/v1/users/"+userid+"/factors/?updatePhone=true"; 
        String input ="{\"factorType\": \"sms\",  \"provider\": \"OKTA\", \"profile\": { \"phoneNumber\" :  \""+"+"+countryCode+" "+phoneNumber+"\"}}";
        logger.log("updateMobileNumber oktaUrl"+oktaUrl);
        logger.log("updateMobileNumber oktaUrl"+input); 
        HttpHeaders headers = buildOktaHeaders();
        HttpEntity<String> entity = new HttpEntity<String>(input, headers);
        try {
        	restTemplate.setErrorHandler(new CustomResponseErrorHandler());
            ResponseEntity<String> response = restTemplate.exchange(oktaUrl, HttpMethod.POST, entity, String.class);
            if (response != null) {
            	apiGatewayResponse = setResponse(response.getStatusCode().toString(), response.getBody());
            }
        }
        catch (Exception e) {
        	apiGatewayResponse = setResponse(getStatusCode(e), e.getMessage());
        }
        return apiGatewayResponse;
    }
    
    
    /**     
     * To send the resetpasswordEmail to the user
     * @param userid
     * @return
     * @throws Exception
     */
    protected ApiGatewayResponse sendResetPasswordEmail(final String emailAddress) throws Exception {
    	ApiGatewayResponse apiGatewayResponse = null;
        RestTemplate restTemplate = new RestTemplate(); 
        String oktaUrl = CFUserConstants.OKTA_DOMAIN_NAME+"/api/v1/authn/recovery/password"; 
        String input ="{ \"username\": \""+emailAddress+"\","+"\"factorType\""+":\"EMAIL\" }"; 
        logger.log("sendResetPasswordEmail oktaUrl"+oktaUrl);
        logger.log("sendResetPasswordEmail input"+input);  
        HttpHeaders headers = buildOktaHeaders();
        HttpEntity<String> entity = new HttpEntity<String>(input, headers);
        try {
        	restTemplate.setErrorHandler(new CustomResponseErrorHandler());
            ResponseEntity<String> response = restTemplate.exchange(oktaUrl, HttpMethod.POST, entity, String.class);
            if (response != null) {
            	apiGatewayResponse = setResponse(response.getStatusCode().toString(), response.getBody());
            }
        }
        catch (Exception e) {
        	apiGatewayResponse = setResponse(getStatusCode(e), e.getMessage());
        }
        return apiGatewayResponse;
    }
    
    
    /**
     * To deactivate the user from okta portal
     * @param firstName
     * @param lastName
     * @param email
     * @param logger
     * @return
    }
     *
     */
    protected ApiGatewayResponse deactivateOktaUser(final String oktauserid) throws Exception {
    	ApiGatewayResponse apiGatewayResponse = null;
    	RestTemplate restTemplate = new RestTemplate();	
        //String oktaUrl = "https://cargillcustomer-qa.oktapreview.com/api/v1/users?activate=true";
        String oktaUrl = CFUserConstants.OKTA_DOMAIN_NAME+"/api/v1/users/"+oktauserid+"/lifecycle/deactivate";      
        HttpHeaders headers = buildOktaHeaders();
        HttpEntity<String> entity = new HttpEntity<String>(headers);
        try {
            ResponseEntity<String> response = restTemplate.exchange(oktaUrl, HttpMethod.POST, entity, String.class);
            if (response != null) {
            	apiGatewayResponse = setResponse(response.getStatusCode().toString(), response.getBody());
            }
        }
        catch (Exception e) {
        	apiGatewayResponse = setResponse(getStatusCode(e), e.getMessage());
        }

        return apiGatewayResponse;
    }
    
    /**
     * To delete user from okta portal
     * @param oktauserid
     * @return
     * @throws Exception
     */
    protected ApiGatewayResponse deleteOktaUser(final String oktauserid) throws Exception {

    	ApiGatewayResponse apiGatewayResponse = null;
        RestTemplate restTemplate = new RestTemplate();

        String oktaUrl = CFUserConstants.OKTA_DOMAIN_NAME+"/api/v1/users/"+oktauserid;
        
        
        HttpHeaders headers = buildOktaHeaders();
        HttpEntity<String> entity = new HttpEntity<String>(headers);
        try {
            ResponseEntity<String> response = restTemplate.exchange(oktaUrl, HttpMethod.DELETE, entity, String.class);
            if (response != null) {
            	apiGatewayResponse = setResponse(response.getStatusCode().toString(), response.getBody());
            }
        }
        catch (Exception e) {
        	apiGatewayResponse = setResponse(getStatusCode(e), e.getMessage());
        }

        return apiGatewayResponse;
    }
    

    
    
    /**
     * Activate Okta User
     * @param firstName
     * @param lastName
     * @param email
     * @param logger
     * @return
    }
     *
     */
    protected ApiGatewayResponse activateOktaUser(final String oktauserid) throws Exception {
        
        ApiGatewayResponse apiGatewayResponse = null;
        RestTemplate restTemplate = new RestTemplate();

        String oktaUrl = CFUserConstants.OKTA_DOMAIN_NAME+"/api/v1/users/"+oktauserid+"/lifecycle/activate?sendEmail=true";        
        
        HttpHeaders headers = buildOktaHeaders();
        HttpEntity<String> entity = new HttpEntity<String>(headers);
        try {
            ResponseEntity<String> response = restTemplate.exchange(oktaUrl, HttpMethod.POST, entity, String.class);
            if (response != null) {
            	apiGatewayResponse = setResponse(response.getStatusCode().toString(), response.getBody());
            }
        }
        catch (Exception e) {
        	apiGatewayResponse = setResponse(getStatusCode(e), e.getMessage());
        }

        return apiGatewayResponse;
    }

    
    /**
     * To get the password policy from the okta domain
     * @param logger
     * @return
     * @throws Exception
     */
    protected ApiGatewayResponse getPasswordPolicy() throws Exception {
    	ApiGatewayResponse apiGatewayResponse = null;       
        RestTemplate restTemplate = new RestTemplate();
        String oktaUrl = CFUserConstants.OKTA_DOMAIN_NAME+"/api/v1/policies?type=PASSWORD";          
        logger.log("getPasswordPolicy oktaUrl"+oktaUrl);       
        HttpHeaders headers = buildOktaHeaders();
        HttpEntity<String> entity = new HttpEntity<String>(headers);
        try {
        	restTemplate.setErrorHandler(new CustomResponseErrorHandler());
        	ResponseEntity<String> response = restTemplate.exchange(oktaUrl, HttpMethod.GET, entity, String.class);
            if (response != null) {
            	apiGatewayResponse = setResponse(response.getStatusCode().toString(), response.getBody());
            }
        }
        catch (Exception e) {
        	apiGatewayResponse = setResponse(getStatusCode(e), e.getMessage());
        }
        return apiGatewayResponse;
    }  
    
    /**
     * To reset the user's password
     * @param logger
     * @return
     * @throws Exception
     */
    protected ApiGatewayResponse verifySecurityAnswer(final String stateToken, String resetAnswer) throws Exception {
    	ApiGatewayResponse apiGatewayResponse = null;       
        RestTemplate restTemplate = new RestTemplate();
        String oktaUrl = CFUserConstants.OKTA_DOMAIN_NAME+"/api/v1/authn/recovery/answer";   
        String input ="{ \"stateToken\" :  \""+stateToken+"\", \"answer\" :  \""+resetAnswer+"\" }";
        logger.log("stateToken oktaUrl"+oktaUrl);       
        HttpHeaders headers = buildOktaHeaders();
        HttpEntity<String> entity = new HttpEntity<String>(input, headers);
        try {
        	restTemplate.setErrorHandler(new CustomResponseErrorHandler());
        	ResponseEntity<String> response = restTemplate.exchange(oktaUrl, HttpMethod.POST, entity, String.class);
            if (response != null) {
            	apiGatewayResponse = setResponse(response.getStatusCode().toString(), response.getBody());
            }
        }
        catch (Exception e) {
        	apiGatewayResponse = setResponse(getStatusCode(e), e.getMessage());
        }
        return apiGatewayResponse;
    } 
    
    
    /**
     * To reset the user's password
     * @param logger
     * @return
     * @throws Exception
     */
    protected ApiGatewayResponse verifyPassword(final String stateToken, String resetPassword) throws Exception {
    	ApiGatewayResponse apiGatewayResponse = null;       
        RestTemplate restTemplate = new RestTemplate();
        String oktaUrl = CFUserConstants.OKTA_DOMAIN_NAME+"/api/v1/authn/credentials/reset_password";   
        String input ="{ \"stateToken\" :  \""+stateToken+"\", \"newPassword\" :  \""+resetPassword+"\" }";
        logger.log("verifyPassword oktaUrl"+oktaUrl);        
        HttpHeaders headers = buildOktaHeaders();
        HttpEntity<String> entity = new HttpEntity<String>(input, headers);
        try {
        	restTemplate.setErrorHandler(new CustomResponseErrorHandler());
        	ResponseEntity<String> response = restTemplate.exchange(oktaUrl, HttpMethod.POST, entity, String.class);
            if (response != null) {
            	apiGatewayResponse = setResponse(response.getStatusCode().toString(), response.getBody());
            }
        }
        catch (Exception e) {
        	apiGatewayResponse = setResponse(getStatusCode(e), e.getMessage());
        }
        return apiGatewayResponse;
    }
    
   
	
	/**
	 * Get Okta User Id
	 * @param email
	 * @param logger
	 * @return
    }
	 *
	 */
	protected ApiGatewayResponse getOktaUserIdByEmail(final String emailAddress) throws Exception {
		ApiGatewayResponse apiGatewayResponse = null;
		RestTemplate restTemplate = new RestTemplate(); 
		String oktaUrl = CFUserConstants.OKTA_DOMAIN_NAME+"/api/v1/users/"+emailAddress;    
		logger.log("getOktaUserIdByEmail oktaUrl"+oktaUrl);        
		HttpHeaders headers = buildOktaHeaders();
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		try {
			restTemplate.setErrorHandler(new CustomResponseErrorHandler());
			ResponseEntity<String> response = restTemplate.exchange(oktaUrl, HttpMethod.GET, entity, String.class);
			if (response != null) {
				apiGatewayResponse = setResponse(response.getStatusCode().toString(), response.getBody());
			}
		}
		catch (Exception e) {
			apiGatewayResponse = setResponse(getStatusCode(e), e.getMessage());
		}
		return apiGatewayResponse;

	}
	
	

	/**
	 * Get Okta Role based on Okta User Id
	 * @param oktaUserId
	 * @param logger
	 * @return
    }
	 *
	 */
	protected ApiGatewayResponse getOktaUserRole(final String oktaUserId) throws Exception {
		ApiGatewayResponse apiGatewayResponse = null;
		RestTemplate restTemplate = new RestTemplate(); 
		String oktaUrl = CFUserConstants.OKTA_DOMAIN_NAME+"/api/v1/users/"+oktaUserId+"/roles";    
		logger.log("getOktaUserRole oktaUrl"+oktaUrl);        
		HttpHeaders headers = buildOktaHeaders();
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		try {
			restTemplate.setErrorHandler(new CustomResponseErrorHandler());
			ResponseEntity<String> response = restTemplate.exchange(oktaUrl, HttpMethod.GET, entity, String.class);
			if (response != null) {
				apiGatewayResponse = setResponse(response.getStatusCode().toString(), response.getBody());
			}
		}
		catch (Exception e) {
			apiGatewayResponse = setResponse(getStatusCode(e), e.getMessage());
		}
		return apiGatewayResponse;

	}
	
	/**
	 * Get User list assigned to a group
	 * @param groupId
	 * @return
	 * @throws Exception
	 */
	protected ApiGatewayResponse getUserListInGroup(final String groupId) throws Exception {
		ApiGatewayResponse apiGatewayResponse = null;
		RestTemplate restTemplate = new RestTemplate(); 
		String oktaUrl = CFUserConstants.OKTA_DOMAIN_NAME+"/api/v1/groups/"+groupId+"/users";    
		logger.log("getUserListInGroup oktaUrl"+oktaUrl);        
		HttpHeaders headers = buildOktaHeaders();
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		try {
			restTemplate.setErrorHandler(new CustomResponseErrorHandler());
			ResponseEntity<String> response = restTemplate.exchange(oktaUrl, HttpMethod.GET, entity, String.class);
			if (response != null) {
				apiGatewayResponse = setResponse(response.getStatusCode().toString(), response.getBody());
			}
		}
		catch (Exception e) {
			apiGatewayResponse = setResponse(getStatusCode(e), e.getMessage());
		}
		return apiGatewayResponse;

	}

	/**
	 * 
	 * @param headerInfo
	 */
	private void buildRequestHeaders(String headerInfo) {
		logger.log("headerInfo" + headerInfo);
		headerInfo = headerInfo.replace("{", "");
		headerInfo = headerInfo.replace("}", "");
		String key = "";
		String value = "";
		String[] headerDetails = headerInfo.split(",");
		if (headerDetails.length > 0) {
			for (String entry : headerDetails) {
				String[] keyValue = entry.split("=");
				if (keyValue != null && !keyValue.equals("") && keyValue.length >= 2) {
					if (keyValue[0] != null && !keyValue[0].equals("")) {
						key = keyValue[0].trim();
					}
					if (keyValue[1] != null && !keyValue[1].equals("")) {
						value = keyValue[1].trim();
					}

					map.put(key, value);
				}
			}
		}
	}

	/**
	 * To get the header value
	 * @param headerkeyName
	 * @return
	 */
	private String getHeaderValue(String headerkeyName) {
		String headervalue = "";
		if (map.containsKey(headerkeyName)) {
			headervalue = map.get(headerkeyName);			
		}
		return headervalue;
	}
	
/**
 * To get the status code for the Exception
 * @return
 */
	private String getStatusCode(Exception e) {
		String statusCode = CFUserConstants.ERROR_CODE;
		if (e.getMessage().length() >= 3) {
			try {
				statusCode = e.getMessage().substring(0, 3);
			} catch (Exception e2) {
				// LOG.error("Unable to parse error code from exception message: " + e2);
				statusCode = CFUserConstants.ERROR_CODE;
			}
		}
		return statusCode;
	}	
   
}