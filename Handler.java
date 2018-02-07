package com.serverless;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
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
		}

		Integer returnCode = 200;

		if (requestType.equals("adduser")) {  	//Add a user

			Boolean result = insertUser(logger, emailAddress, serviceId, adminEmail, taxId, firstName, lastName);
			String responseBody = result == true ? "insert succeeded" : "insert failed";

			return ApiGatewayResponse.builder()
					.setStatusCode(returnCode)
					.setObjectBody(responseBody)
					.setHeaders(Collections.singletonMap("Access-Control-Allow-Origin", "*"))
					.build();

		}
		else if (requestType.equals("settermsandconditions")) {  	//TermsAndConditions update

			Boolean result = setTermsAndConditions(logger, emailAddress);
			String responseBody = result == true ? "settermsandconditions succeeded" : "settermsandconditions failed";

			return ApiGatewayResponse.builder()
					.setStatusCode(returnCode)
					.setObjectBody(responseBody)
					.setHeaders(Collections.singletonMap("Access-Control-Allow-Origin", "*"))
					.build();

		} else if (requestType.equals("updatedeactivatestatus")) {  	//Deactivation Status update

			Boolean result = setDeactivationStatus(logger, userId, deactivateStatus);

			String responseBody = result == true ? "user deactivation succeeded" : "user deactivation failed";

			return ApiGatewayResponse.builder()

					.setStatusCode(returnCode)

					.setObjectBody(responseBody)

					.setHeaders(Collections.singletonMap("Access-Control-Allow-Origin", "*"))

					.build();

		} else if (requestType.equals("userregistration")) {  	//User registration

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

		} else if (requestType.equals("resendpasscode")) {  	// if passcode is expired by 30 seconds, request for resend 
		
			try {
				apiGatewayResponse = resendPasscode(oktaUserId, factorId);
			} catch (Exception e) {				
				e.printStackTrace();
			}

			return apiGatewayResponse;

		}else if (requestType.equals("verifypasscode")) {  	// verify the passcode that has been received through resend API
		
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

		}else if (requestType.equals("deletetestuserinprod")) {  	//To delete the test user in prod

			try {
				apiGatewayResponse = deleteTestUserInProd(firstName, lastName);
			} catch (Exception e) {				
				e.printStackTrace();
			}

			return apiGatewayResponse;

		} else if (requestType.equals("activateoktauser")) {  	//Activate Okta user


			String result = null;
			try {
				result = activateOktaUser(oktaUserId, logger);
			} catch (Exception e) {
				e.printStackTrace();
			}
			

			return ApiGatewayResponse.builder()

					.setStatusCode(returnCode)

					.setObjectBody(result)

					.setHeaders(Collections.singletonMap("Access-Control-Allow-Origin", "*"))

					.build();

		} else if (requestType.equals("createtandc")) { // Do an INSERT

			String result = createTandCColumn(logger);
			String responseBody = result ;

			return ApiGatewayResponse.builder().setStatusCode(returnCode).setObjectBody(responseBody)
					.setHeaders(Collections.singletonMap("Access-Control-Allow-Origin", "*")).build();


		} else if (requestType.equals("updateroleandtaxid")) {  	//update userrole and taxid


			String responseBody = null;
			
			try {
				Boolean result = updateRoleAndTaxId(userRole, taxId, emailAddress, firstName, lastName, oktaUserId, logger);
				responseBody = result == true ? "user role and taxid update succeeded" : "user role and taxid update failed";
			} catch (Exception e) {
				e.printStackTrace();
			}
			

			return ApiGatewayResponse.builder()

					.setStatusCode(returnCode)

					.setObjectBody(responseBody)

					.setHeaders(Collections.singletonMap("Access-Control-Allow-Origin", "*"))

					.build();

		} else if (requestType.equals("oktauseridbyemail")) {  	
			try {
				apiGatewayResponse = getOktaUserIdByEmail(emailAddress);
			} catch (Exception e) {
				e.printStackTrace();
			}

			return apiGatewayResponse;

		} else if (requestType.equals("getcolumnnames")) {



			String result = getColumnNames(logger, tableName);

			String responseBody = result ;



			return ApiGatewayResponse.builder().setStatusCode(returnCode).setObjectBody(responseBody)

					.setHeaders(Collections.singletonMap("Access-Control-Allow-Origin", "*")).build();



		} else if (requestType.equals("createcolumn")) { // Do an INSERT



			String result = createColumnInTable(logger, tableName, columnName);

			String responseBody = result ;



			return ApiGatewayResponse.builder().setStatusCode(returnCode).setObjectBody(responseBody)

					.setHeaders(Collections.singletonMap("Access-Control-Allow-Origin", "*")).build();



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

		} else if (requestType.equals("deleteuserfromrds")) {  	//delete user from AWS RDS 

			String responseBody = null;
			
			try {
				responseBody = deleteUserRDS(emailAddress, logger);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return ApiGatewayResponse.builder()
					.setStatusCode(returnCode)
					.setObjectBody(responseBody)
					.setHeaders(Collections.singletonMap("Access-Control-Allow-Origin", "*"))
					.build();

		} else {    //Do a SELECT
			String responseBody = "";

			if (requestType.equals("getallusers")) {

				responseBody = allUserQuery(logger);

			} else if (requestType.equals("getuserbyemail")) {

				responseBody = getUserByEmailQuery(logger, emailAddress);

			} else if (requestType.equals("getuserslist")) {

				responseBody = getUsersList(logger);

			}  else {

				logger.log("ERROR: invalid request type: " + requestType);
			}

			return ApiGatewayResponse.builder()
					.setStatusCode(returnCode)
					.setObjectBody(responseBody)
					.setHeaders(Collections.singletonMap("Access-Control-Allow-Origin", "*"))
					.build();
		}
    }
    
    private String allUserQuery(LambdaLogger logger) {
    	     	
     	String jsonResult = "";

		String sql = "SELECT * FROM user LEFT OUTER JOIN service ON user.service_id = service.id";

		Connection connection = getConnection(CFUserConstants.CONNECTION_STRING, logger);

     	if (connection != null) {
     		logger.log("connected to MySQL DB");
     		
     		  Statement statement = null;
 			try {
 				statement = connection.createStatement();
 			} catch (SQLException e) {
 
 				logger.log("Error: " + e.getMessage());
 			}
               ResultSet resultSet = null;
               
            try {
    			resultSet = statement.executeQuery("Use CustomerFacing");
    	    } catch (SQLException e1) {
    
    			logger.log("Error: " + e1.getMessage());
    		}
               
 			try {
 				resultSet = statement.executeQuery(sql);
 			} catch (SQLException e1) {
 
 				logger.log("Error: " + e1.getMessage());
 			}
             	     
 			try {
 				
             	JSONArray json = convert(resultSet);  
             	 
             	jsonResult = json.toString();
             	
             	logger.log(jsonResult);
             	  
 			 } catch (SQLException e) {
 
 				logger.log("Error: " + e.getMessage());
 			 }   		
     		
     	} else {
     		logger.log("Error: Failed to make MySQL Connection!");
     	}
     	
     	return jsonResult;
     }

	private String getUserByEmailQuery(LambdaLogger logger, String emailAddress) {

		String jsonResult = "";

		String sql = "SELECT * from user where user.email_address = ? and user.deactivated = 0";

		Connection connection = getConnection(CFUserConstants.CONNECTION_STRING, logger);

		if (connection != null) {
			logger.log("connected to MySQL DB");

			PreparedStatement statement = null;
			try {
				statement = connection.prepareStatement(sql);
			} catch (SQLException e) {
				e.printStackTrace();
				logger.log("SQLException: " + e.getMessage());
			}
			try {
				statement.setString(1, emailAddress);
			} catch (SQLException e) {
				e.printStackTrace();
				logger.log("SQLException: " + e.getMessage());
			}

			ResultSet resultSet = null;

			try {
				resultSet = statement.executeQuery();
			} catch (SQLException e1) {

				logger.log("Error: " + e1.getMessage());
			}

			try {

				JSONArray json = convert(resultSet);

				jsonResult = json.toString();

				logger.log(jsonResult);

			} catch (SQLException e) {

				logger.log("Error: " + e.getMessage());
			}

		} else {
			logger.log("Error: Failed to make MySQL Connection!");
		}

		return jsonResult;
	}


     private Boolean insertUser(LambdaLogger logger, String emailAddress, String serviceId, String adminEmail, String taxId, String firstName, String lastName) {

		 final String verifyAdminRoleCommand = "SELECT service_id FROM user WHERE email_address = ?";

		 //Default Tax Id if one is not passed in
		 String taxIdNumber = "83573212009818";
		 if (taxId.length() > 0) taxIdNumber = taxId;

		 final String insertUserCommand = "INSERT INTO user (first_name, last_name, email_address, service_id, tax_id) " +
				 "VALUES (?, ?, ?, ?,'" + taxIdNumber + "')";

		 Boolean result = false;

		 Connection connection = getConnection(CFUserConstants.CONNECTION_STRING, logger);

		 if (connection != null) {
			 logger.log("connected to MySQL DB");

			 // First check the role of the Admin
			 PreparedStatement statement = null;
			 try {
				statement = connection.prepareStatement(verifyAdminRoleCommand);
			} catch (SQLException e) {
				e.printStackTrace();
				logger.log("SQLException: " + e.getMessage());
			}
			try {
				statement.setString(1, adminEmail);
			} catch (SQLException e) {
				e.printStackTrace();
				logger.log("SQLException: " + e.getMessage());
			}

			ResultSet resultSet = null;
		    String jsonResult = "";
            Integer serviceIdForAdmin = 0;

			try {
				logger.log("executing SQL statement: " + verifyAdminRoleCommand);

				resultSet = statement.executeQuery();
                if (resultSet == null) {
					logger.log("Error: no resultset from verifyAdminRoleCommand");
					return false;
				}

				try {

					JSONArray json = convert(resultSet);
					jsonResult = json.toString();

					//strip off brackets
					jsonResult = jsonResult.substring(1, jsonResult.length()-1);

					logger.log(jsonResult);
					serviceIdForAdmin = extractJSONInteger(jsonResult, "service_id");
					logger.log("serviceIdForAdmin: " + serviceIdForAdmin);

				} catch (SQLException e) {

					logger.log("Error: " + e.getMessage());
				}

			} catch (SQLException e1) {

				logger.log("Error: " + e1.getMessage());
			}

			//Do logic to validate service Ids
            if (serviceIdForAdmin.equals(2) || (serviceIdForAdmin.equals(5) && serviceId.equals("3"))
					||(serviceIdForAdmin.equals(3) && (serviceId.equals("3") || serviceId.equals("4")))) {

				logger.log("Execution allowed (serviceIdForAdmin: " + serviceIdForAdmin + ", serviceId: " + serviceId + ")");

                try {
                    String oktaCreateResult = createOktaUser(firstName, lastName, emailAddress, logger);
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.log("oktaCreateResult : Okta create user failed: "+e.getMessage());
                    return false;
                }
                statement = null;
				try {
					statement = connection.prepareStatement(insertUserCommand);
				} catch (SQLException e) {
					e.printStackTrace();
					logger.log("SQLException: " + e.getMessage());
				}
				try {
					statement.setString(1, firstName);
					statement.setString(2, lastName);
					statement.setString(3, emailAddress);
					statement.setString(4, serviceId);
				} catch (SQLException e) {
					e.printStackTrace();
					logger.log("SQLException: " + e.getMessage());
				}

				logger.log("executing SQL statement: " + insertUserCommand);

				try {
					statement.execute();
					result = true;
				} catch (SQLException e1) {

					logger.log("Error: " + e1.getMessage());
				}

			}

			 logger.log("InsertUser Result: " +  result);
			 return result;

			} else {

		 	logger.log("Execution not allowed");
		 	return false;
		 }


	 }

     private Connection getConnection(String url, LambdaLogger logger) {

		 String dbPassword =  System.getenv("db_password");
		 String dbUser =  System.getenv("db_user");
		 Connection connection = null;

		 try {
			 Class.forName("com.mysql.jdbc.Driver");
		 } catch (ClassNotFoundException e) {
			 logger.log("Error: Can't find MySQL JDBC Driver");
			 e.printStackTrace();
			 return connection;
		 }

		 logger.log("MySQL JDBC Driver Registered!");


		 try {
			 connection = DriverManager
					 .getConnection(url, dbUser, dbPassword);

		 } catch (SQLException e) {
			 logger.log("Error: Connection Failed!");
			 e.printStackTrace();
			 return connection;
		 }

		 return connection;
	 }

     public static JSONArray convert( ResultSet rs ) throws SQLException, JSONException
     	  {
     	    JSONArray json = new JSONArray();
     	    ResultSetMetaData rsmd = rs.getMetaData();
 
     	    while(rs.next()) {
     	      int numColumns = rsmd.getColumnCount();
     	      JSONObject obj = new JSONObject();
 
     	      for (int i=1; i<numColumns+1; i++) {
     	        String column_name = rsmd.getColumnName(i);
 
     	        if(rsmd.getColumnType(i)==java.sql.Types.ARRAY){
     	         obj.put(column_name, rs.getArray(column_name));
     	        }
     	        else if(rsmd.getColumnType(i)==java.sql.Types.BIGINT){
     	         obj.put(column_name, rs.getInt(column_name));
     	        }
     	        else if(rsmd.getColumnType(i)==java.sql.Types.BOOLEAN){
     	         obj.put(column_name, rs.getBoolean(column_name));
     	        }
     	        else if(rsmd.getColumnType(i)==java.sql.Types.BLOB){
     	         obj.put(column_name, rs.getBlob(column_name));
     	        }
     	        else if(rsmd.getColumnType(i)==java.sql.Types.DOUBLE){
     	         obj.put(column_name, rs.getDouble(column_name)); 
     	        }
     	        else if(rsmd.getColumnType(i)==java.sql.Types.FLOAT){
     	         obj.put(column_name, rs.getFloat(column_name));
     	        }
     	        else if(rsmd.getColumnType(i)==java.sql.Types.INTEGER){
     	         obj.put(column_name, rs.getInt(column_name));
     	        }
     	        else if(rsmd.getColumnType(i)==java.sql.Types.NVARCHAR){
     	         obj.put(column_name, rs.getNString(column_name));
     	        }
     	        else if(rsmd.getColumnType(i)==java.sql.Types.VARCHAR){
     	         obj.put(column_name, rs.getString(column_name));
     	        }
     	        else if(rsmd.getColumnType(i)==java.sql.Types.TINYINT){
     	         obj.put(column_name, rs.getInt(column_name));
     	        }
     	        else if(rsmd.getColumnType(i)==java.sql.Types.SMALLINT){
     	         obj.put(column_name, rs.getInt(column_name));
     	        }
     	        else if(rsmd.getColumnType(i)==java.sql.Types.DATE){
     	         obj.put(column_name, rs.getDate(column_name));
     	        }
     	        else if(rsmd.getColumnType(i)==java.sql.Types.TIMESTAMP){
     	        obj.put(column_name, rs.getTimestamp(column_name));   
     	        }
     	        else{
     	         obj.put(column_name, rs.getObject(column_name));
     	        }
     	      }
 
     	      json.put(obj);
     	    }
 
    	    return json;
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

	private Integer extractJSONInteger(String jsonData, String field) {

		JSONObject json = new JSONObject(jsonData);

		return (Integer)json.get(field);
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
    protected String createOktaUser(final String firstName, final String lastName, final String emailAddress, LambdaLogger logger) throws Exception {

        String result="";
        RestTemplate restTemplate = new RestTemplate();

        String oktaUrl = CFUserConstants.OKTA_DOMAIN_NAME+"/api/v1/users?activate=true";       
        //String oktaUrl = "https://cargillcustomer.okta-emea.com/api/v1/users?activate=true";
        String input ="{\"profile\": { \"firstName\":\""+ firstName + "\",\"lastName\":\""+ lastName +"\", \"email\":\""+ emailAddress +"\", \"locale\": \"pt_BR\",\"login\":\""+ emailAddress +"\" }, \"groupIds\": [ \""+CFUserConstants.BPAPPGRPId+"\", \""+CFUserConstants.BPUSERGRPID+"\" ]}";
        //String input ="{\"profile\": { \"firstName\":\""+ firstName + "\",\"lastName\":\""+ lastName +"\", \"email\":\""+ emailAddress +"\", \"locale\": \"pt_BR\",\"login\":\""+ emailAddress +"\" }, \"groupIds\": [ \"00g1arzxzm1exYnec0i7\", \"00g1arzcxu0pheGd60i7\" ]}";

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
            String err = "Rest Client Exception - createOktaUser: " + e.getMessage();
            logger.log(err);
            throw new Exception(err);
        }

        return result;
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
    
    

	private String extractJSONString(String jsonData, String field) {

		JSONObject json = new JSONObject(jsonData);

		return (String)json.get(field);
	}
	
	private Boolean setDeactivationStatus(LambdaLogger logger, String userId, String deactivateStatus) {
	     
			 final String setDeactivationStatusSQL = "UPDATE user u SET u.deactivated = "+deactivateStatus+" WHERE u.id = ?";


			 Boolean result = true;



			 Connection connection = getConnection(CFUserConstants.CONNECTION_STRING, logger);



			 if (connection != null) {

				 logger.log("connected to MySQL DB");

				 

				 PreparedStatement statement = null;

				 try {

					statement = connection.prepareStatement(setDeactivationStatusSQL);

				} catch (SQLException e) {

					e.printStackTrace();

					result = false;

					logger.log("SQLException: " + e.getMessage());

				}

				try {

					statement.setString(1, userId);
					statement.execute();

				} catch (SQLException e) {

					e.printStackTrace();

					result = false;

					logger.log("SQLException: " + e.getMessage());

				}

			 }

			 return result;

		}
	
    private Boolean setTermsAndConditions(LambdaLogger logger, String emailAddress) {
    	
		 final String setTermsAndConditionsSQL = "UPDATE user SET user.tandc = 'Y' where user.email_address = ?";

		 Boolean result = true;
		 createTandCColumn(logger);

		 Connection connection = getConnection(CFUserConstants.CONNECTION_STRING, logger);

		 if (connection != null) {
			 logger.log("connected to MySQL DB");
			 
			 PreparedStatement statement = null;
			 try {
				statement = connection.prepareStatement(setTermsAndConditionsSQL);
			} catch (SQLException e) {
				e.printStackTrace();
				result = false;
				logger.log("SQLException: " + e.getMessage());
			}
			try {
				statement.setString(1, emailAddress);
				statement.execute();
			} catch (SQLException e) {
				e.printStackTrace();
				result = false;
				logger.log("SQLException: " + e.getMessage());
			}
		 }
		 return result;
	}

	protected String getUsersList(LambdaLogger logger) {

		String jsonResult = "";

		String sql = "select u.id, u.first_name, u.last_name,  u.email_address, s.name, u.service_id, u.tax_id, u.deactivated\n"
				+

				"from user u, service s\n" +

				"where u.service_id = s.id\n" +

				"order by u.id";

		Connection connection = getConnection(CFUserConstants.CONNECTION_STRING, logger);

		if (connection != null) {

			logger.log("connected to MySQL DB");

			Statement statement = null;

			try {

				statement = connection.createStatement();

			} catch (SQLException e) {

				logger.log("Error: " + e.getMessage());

			}

			ResultSet resultSet = null;

			try {

				resultSet = statement.executeQuery("Use CustomerFacing");

			} catch (SQLException e1) {

				logger.log("Error: " + e1.getMessage());

			}

			try {

				resultSet = statement.executeQuery(sql);

			} catch (SQLException e1) {

				logger.log("Error: " + e1.getMessage());

			}
			
			try {

				JSONArray json = convert(resultSet);

				jsonResult = json.toString();

				logger.log(jsonResult);

			} catch (SQLException e) {

				logger.log("Error: " + e.getMessage());

			}

		} else {

			logger.log("Error: Failed to make MySQL Connection!");

		}

		return jsonResult;

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
        String oktaUrl = CFUserConstants.OKTA_DOMAIN_NAME+"/api/v1/users/"+userid+"/factors/"+factorid+"/lifecycle/activate"; 
        String input ="{ \"passCode\": \""+passCode+"\" }"; 
        logger.log("activateSMSFactor oktaUrl"+oktaUrl);
        logger.log("activateSMSFactor input: " + input);        
        HttpHeaders headers = buildOktaHeaders();
        HttpEntity<String> entity = new HttpEntity<String>(input, headers);
        ResponseEntity<String> response = null;
        try {
        	 restTemplate.setErrorHandler(new CustomResponseErrorHandler());
             response = restTemplate.exchange(oktaUrl, HttpMethod.POST, entity, String.class);          
           if (response != null) {            	
            	//System.out.println("API Response Body***:"+response.getBody() +"status code value::"+response.getStatusCodeValue() +" status code"+response.getStatusCode());    
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
    protected ApiGatewayResponse resendPasscode(final String userid, final String factorId) throws Exception {
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
    }
    
    
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
        String input ="{ \"passCode\": \""+passCode+"\" }";   
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

        //String oktaUrl = "https://cargillcustomer-qa.oktapreview.com/api/v1/users?activate=true";
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
    protected String activateOktaUser(final String oktauserid, LambdaLogger logger) throws Exception {

        String result="";
        RestTemplate restTemplate = new RestTemplate();

        //String oktaUrl = "https://cargillcustomer-qa.oktapreview.com/api/v1/users?activate=true";
        String oktaUrl = CFUserConstants.OKTA_DOMAIN_NAME+"/api/v1/users/"+oktauserid+"/lifecycle/activate?sendEmail=true";
        
        
        HttpHeaders headers = buildOktaHeaders();
        HttpEntity<String> entity = new HttpEntity<String>(headers);
        try {
            ResponseEntity<String> response = restTemplate.exchange(oktaUrl, HttpMethod.POST, entity, String.class);
            if (response != null) {
                result = response.toString();
            }
        }
        catch (Exception e) {
            String err = "Rest Client Exception - deactivateOktaUser: " + e.getMessage();
            logger.log(err);
            throw new Exception(err);
        }

        return result;
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
     * To create TandC columm if it does not exist
     * @param logger
     * @return
     * @throws Exception
     */
	private String createTandCColumn(LambdaLogger logger) {

		final String columnExists = "SELECT COUNT(*) as colexists \r\n" + "  FROM INFORMATION_SCHEMA.COLUMNS\r\n"

				+ "                    WHERE   table_schema ='CustomerFacing' and TABLE_NAME = 'user' AND \r\n"

				+ "                            COLUMN_NAME = 'tandc'";

		final String alterCommand = "alter table user Add column tandc varchar(10) DEFAULT 'N' ";

		String result = "";

		ResultSet resultSet = null;

		String jsonResult = "";

		Integer count = 0;

		Connection connection = getConnection(CFUserConstants.CONNECTION_STRING, logger);

		if (connection != null) {

			PreparedStatement statement = null;

			try {

				statement = connection.prepareStatement(columnExists);

				resultSet = statement.executeQuery();

				if (resultSet == null) {

					logger.log("Error: no resultset from columnExists");

				}

				JSONArray json = convert(resultSet);

				jsonResult = json.toString();

				// strip off brackets

				jsonResult = jsonResult.substring(1, jsonResult.length() - 1);

				logger.log(jsonResult);

				count = extractJSONInteger(jsonResult, "colexists");

				logger.log("count: " + count);

			} catch (SQLException e1) {

				logger.log("Error: " + e1.getMessage());

			}

			statement = null;

			if (count == 0) {

				result = "Alter Command executed";

				try {

					statement = connection.prepareStatement(alterCommand);

					statement.execute();

				} catch (SQLException e1) {

					logger.log("Error: " + e1.getMessage());

				}

			}

			else {

				result = "Column Already Exists";

			}

		} else {

			logger.log("Execution not allowed");

			result = "Error Occured";

		}

		return result;

	}
	
    /**
     * Return column names
     * @param logger
     * @return
     * @throws Exception
     */
    protected String getColumnNames(LambdaLogger logger, String tableName) {

		String jsonResult = "";
	
		String sql = "SELECT `COLUMN_NAME` FROM `INFORMATION_SCHEMA`.`COLUMNS` WHERE `TABLE_SCHEMA`='CustomerFacing' \r\n" + 
				"    AND `TABLE_NAME`= ?";
	
		 Connection connection = getConnection(CFUserConstants.CONNECTION_STRING, logger);
		 ResultSet resultSet = null;
		 
		 if (connection != null) {
			 logger.log("connected to MySQL DB");
			 
			 PreparedStatement statement = null;
			 try {
				statement = connection.prepareStatement(sql);
			} catch (SQLException e) {
				e.printStackTrace();
				logger.log("SQLException: " + e.getMessage());
			}
			try {
				statement.setString(1, tableName);
				resultSet = statement.executeQuery();
			} catch (SQLException e) {
				e.printStackTrace();
				logger.log("SQLException: " + e.getMessage());
			}
    	     
			try {			
		    	JSONArray json = convert(resultSet);  
		    	 
		    	jsonResult = json.toString();
		    	
		    	logger.log(jsonResult);
		    	  
			 } catch (SQLException e) {
		
				logger.log("Error: " + e.getMessage());
			 } 
		} 	
	 	return jsonResult;
	}
    
    /**
     * To create columm if it does not exist
     * @param logger
     * @return
     * @throws Exception
     */
	private String createColumnInTable(LambdaLogger logger, String tableName, String columnName) {

		final String columnExists = "SELECT COUNT(*) as colexists \r\n" + "  FROM INFORMATION_SCHEMA.COLUMNS\r\n"

				+ "                    WHERE   table_schema ='CustomerFacing' and TABLE_NAME = '" + tableName + "' AND \r\n"

				+ "                            COLUMN_NAME = '" + columnName + "'";

		final String alterCommand = "alter table " + tableName + " Add column " + columnName + " varchar(100) DEFAULT '' ";

		String result = "";

		ResultSet resultSet = null;

		String jsonResult = "";

		Integer count = 0;

		Connection connection = getConnection(CFUserConstants.CONNECTION_STRING, logger);

		if (connection != null) {

			PreparedStatement statement = null;

			try {

				statement = connection.prepareStatement(columnExists);

				resultSet = statement.executeQuery();

				if (resultSet == null) {

					logger.log("Error: no resultset from columnExists");

				}

				JSONArray json = convert(resultSet);

				jsonResult = json.toString();

				// strip off brackets

				jsonResult = jsonResult.substring(1, jsonResult.length() - 1);

				logger.log(jsonResult);

				count = extractJSONInteger(jsonResult, "colexists");

				logger.log("count: " + count);

			} catch (SQLException e1) {

				logger.log("Error: " + e1.getMessage());

			}

			statement = null;

			if (count == 0) {

				result = "Alter Command executed";

				try {

					statement = connection.prepareStatement(alterCommand);

					statement.execute();

				} catch (SQLException e1) {

					logger.log("Error: " + e1.getMessage());

				}

			}

			else {

				result = "Column Already Exists";

			}

		} else {

			logger.log("Execution not allowed");

			result = "Error Occured";

		}

		return result;

	}
    
    /**
     * Deletes the user from AWS RDS
     * @param logger
     * @return
     * @throws Exception
     */
	private String deleteUserRDS(String emailAddress, LambdaLogger logger) {

		final String userExists = "SELECT COUNT(*) as userexists \r\n" + "  FROM user \r\n"
				+ "                    WHERE user.email_address = '" + emailAddress.trim() + "'";

		final String deleteUserCommand = "DELETE FROM user WHERE user.email_address = '" + emailAddress.trim() + "'";

		String result = "";
		ResultSet resultSet = null;
		String jsonResult = "";
		Integer count = 0;

		Connection connection = getConnection(CFUserConstants.CONNECTION_STRING, logger);

		if (connection != null) {
			PreparedStatement statement = null;

			try {

				statement = connection.prepareStatement(userExists);
				resultSet = statement.executeQuery();

				if (resultSet == null) {
					logger.log("Error: no resultset from userExists");
				}

				JSONArray json = convert(resultSet);
				jsonResult = json.toString();

				// strip off brackets

				jsonResult = jsonResult.substring(1, jsonResult.length() - 1);
				logger.log(jsonResult);
				count = extractJSONInteger(jsonResult, "userexists");

				logger.log("count: " + count);

			} catch (SQLException e1) {
				logger.log("Error: " + e1.getMessage());
			}

			statement = null;

			if (count == 1) {
				result = "Delete user command executed";

				try {
					statement = connection.prepareStatement(deleteUserCommand);
					statement.execute();
				} catch (SQLException e1) {
					logger.log("Error: " + e1.getMessage());
				}
			} else {
				result = "User does not exist";
			}
		} else {

			logger.log("Execution not allowed");
			result = "Error Occured";
		}
		return result;
	}

	/**
     * To update user role and taxId
     * @param userRole
	 * @param taxId
	 * @param emailAddress
	 * @param logger
     * @return
     * @throws Exception
     */
	protected Boolean updateRoleAndTaxId(String userRole, String taxId, String emailAddress, String firstName, String lastName, String oktaUserId, LambdaLogger logger) {
		logger.log("updateRoleAndTaxId : " + taxId);	
		String taxIdNumber = "83573212009818";
		if (taxId.length() > 0) taxIdNumber = taxId;
		final String setUserRoleandTaxIdSQL = "UPDATE user SET user.first_name= '"+firstName+"'"+", user.last_name= '"+lastName+"'"+", user.tax_id = '" + taxIdNumber + "'" + ", user.service_id = "+userRole+" where user.email_address = ?";
		logger.log("updateRoleAndTaxId setUserRoleandTaxIdSQL : " + setUserRoleandTaxIdSQL);
		Boolean result = true;

		 Connection connection = getConnection(CFUserConstants.CONNECTION_STRING, logger);

		 if (connection != null) {
			 logger.log("connected to MySQL DB");
			 
			 PreparedStatement statement = null;
			 try {
				statement = connection.prepareStatement(setUserRoleandTaxIdSQL);
			} catch (SQLException e) {
				e.printStackTrace();
				result = false;
				logger.log("SQLException: " + e.getMessage());
			}
			try {
				statement.setString(1, emailAddress);
				statement.execute();
			} catch (SQLException e) {
				e.printStackTrace();
				result = false;
				logger.log("SQLException: " + e.getMessage());
			}
		}
		 try {
			   String oktaUpdateResult = updateOktaUser(firstName, lastName, oktaUserId);
			   logger.log("OktaUserId *********************  " + oktaUpdateResult); 
	     } catch (Exception e) {			
			e.printStackTrace();
	     }
		return result;
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
	
	
	/**
     * To delete  test user from prod
     *     
     */
    protected ApiGatewayResponse deleteTestUserInProd(String firstName, String lastName) throws Exception {
    	ApiGatewayResponse apiGatewayResponse = null;
        RestTemplate restTemplate = new RestTemplate();      
        String oktaUrl = CFUserConstants.OKTA_DOMAIN_NAME+"/api/v1/users?search=profile.firstName eq "+"\""+firstName+"\""+" and profile.lastName eq "+"\""+lastName+"\"";
        logger.log("deleteTestUserInProd oktaUrl"+oktaUrl);                
        HttpHeaders headers = buildOktaHeaders();
        HttpEntity<String> entity = new HttpEntity<String>(headers);
        try {
        	restTemplate.setErrorHandler(new CustomResponseErrorHandler());
        	// To get the okta user id's for the given first and last name 
            ResponseEntity<String> response = restTemplate.exchange(oktaUrl, HttpMethod.GET, entity, String.class);
            if (response != null) {
            	apiGatewayResponse = setResponse(response.getStatusCode().toString(), response.getBody());
            }
           if(response!=null) {
          List<String> userList= getUserList(response.getBody());
		  if(null!=userList) {
          logger.log("userList size::"+userList.size());
          }
          	for(String userId : userList) {
          		apiGatewayResponse  = deactivateOktaUser(userId);        	  
        	  if(apiGatewayResponse!=null && apiGatewayResponse.getStatusCode()==200) {
        		  deleteOktaUser(userId); 
        		  //returning the getuserlist in response
        		  apiGatewayResponse = setResponse(response.getStatusCode().toString(), response.getBody());
              }
          	}
        }
        }
        catch (Exception e) {
        	apiGatewayResponse = setResponse(getStatusCode(e), e.getMessage());
        }
        return apiGatewayResponse;
    }
    
   
    
   
    /**
     * To get the user list that matches the given criteria (firstname, lastname, email (ends with @email.com)
     * @param jsonResponse
     * @return
     */
	private List<String> getUserList(String jsonResponse) {
		List<String> userIdList = new ArrayList<String>();

		logger.log("====jsonResponse=====:" + jsonResponse);
		try {
			JSONArray jsonArray = new JSONArray(jsonResponse);
			// iterate over the JSONArray
			for (int index = 0; index < jsonArray.length(); index++) {
				JSONObject userIdJsonAttr = (JSONObject) jsonArray.get(index);
				if (userIdJsonAttr != null) {
					String userid = userIdJsonAttr.getString("id");
					logger.log("===userid===: " + userid);
					JSONObject profileJsonAttr = (JSONObject) userIdJsonAttr.getJSONObject("profile");
					if (profileJsonAttr != null) {
						String emailJsonAttr = profileJsonAttr.getString("email");
						if (null != emailJsonAttr && emailJsonAttr.endsWith("@email.com"))
							userIdList.add(userid);
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return userIdList;
	}

}