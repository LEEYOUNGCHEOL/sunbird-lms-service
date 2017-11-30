import static org.junit.Assert.assertEquals;
import static play.test.Helpers.GET;
import static play.test.Helpers.route;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.Mock;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.request.HeaderParam;

import com.fasterxml.jackson.databind.JsonNode;

import play.libs.Json;
import play.mvc.Http;
import play.mvc.Http.RequestBuilder;
import play.mvc.Result;
import play.test.FakeApplication;
import play.test.Helpers;

//@RunWith(MockitoJUnitRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class IntegrationTest {

	  public static FakeApplication app;
	  @Mock
	  private Http.Request request;
      private static Map<String,String[]> headerMap;
	  
	  @BeforeClass
	  public static void startApp() {
	      app = Helpers.fakeApplication();
	      Helpers.start(app);
          headerMap = new HashMap<String, String[]>();
          headerMap.put(HeaderParam.X_Consumer_ID.getName(), new String[]{"Service test consumer"});
          headerMap.put(HeaderParam.X_Device_ID.getName(), new String[]{"Some Device Id"});
          headerMap.put(HeaderParam.X_Authenticated_Userid.getName(), new String[]{"Authenticated user id"});
          headerMap.put(JsonKey.MESSAGE_ID, new String[]{"Unique Message id"});
	  }

	  @Before
	  public void setUp() throws Exception {
	      Map<String, String> flashData = Collections.emptyMap();
	     // Http.Context context = new Http.Context(200L,null, request, flashData, flashData, null);
	      //Http.Context.current.set(context);
	  }

	  @Test
	  public void testgetCourses() {
		  RequestBuilder req = new RequestBuilder().uri("/v1/user/courses/id").method(GET);
		    req.headers(headerMap);
			Result result = route(req);
			assertEquals(401, result.status());
	  }
	  
	  
	 @Test
      public void testUserFailureLogin() {
        String data = "{ \"request\":{\"userName\":\"manzarul.haque@tareno.com\",\"source\":\"web\",\"password\":\"password\"}}";    
         JsonNode json = Json.parse(data);
          RequestBuilder req = new RequestBuilder().bodyJson(json).uri("/v1/user/login").method("POST");
            req.headers(headerMap);
            Result result = route(req);
            assertEquals(401, result.status());
      }
	  
	 @Test
     public void testUserFailureLogout() {
       String data = "{\"request\":{\"authToken\":\"some token value\",\"userId\":\"123344-334444-5566\"}}";    
        JsonNode json = Json.parse(data);
         RequestBuilder req = new RequestBuilder().bodyJson(json).uri("/v1/user/logout").method("POST");
           req.headers(headerMap);
           Result result = route(req);
           assertEquals(401, result.status());
     }
	 
	 
	 @Test
     public void testFailureChangePassword() {
       String data = "{\"request\":{\"authToken\":\"some token value\",\"oldPassword\":\"password1\",\"Password\":\"password\"}}";    
        JsonNode json = Json.parse(data);
         RequestBuilder req = new RequestBuilder().bodyJson(json).uri("/v1/user/changepassword").method("POST");
           req.headers(headerMap);
           Result result = route(req);
           assertEquals(401, result.status());
     }
	
	 
	 @Test
     public void testGetUserWithWrongId() {
         RequestBuilder req = new RequestBuilder().uri("/v1/user/getprofile/1234-5465-1234-455").method("GET");
           req.headers(headerMap);
           Result result = route(req);
           assertEquals(401, result.status());
     }
	 
	  @AfterClass
	  public static void stopApp() {

	  }
}
