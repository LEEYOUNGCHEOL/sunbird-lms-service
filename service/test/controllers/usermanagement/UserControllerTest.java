package controllers.usermanagement;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static play.test.Helpers.route;

import com.fasterxml.jackson.databind.ObjectMapper;
import controllers.BaseControllerTest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.models.util.ProjectLogger;
import org.sunbird.common.responsecode.ResponseCode;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;

public class UserControllerTest extends BaseControllerTest {

  private static String userId = "user-id";
  private static String emailId = "abbc@gmail.com";
  private static String phoneNumber = "8800088000";
  private static String userName = "userName";
  private static String loginId = "login-id";
  private static String invalidPhonenumber = "00088000";
  private static String firstName = "firstName";
  private static String lastName = "lastName";
  private static String query = "query";
  private static String language = "any-language";
  private static String role = "user";

  @Test
  public void testCreateUserFailureWithoutContentType() {

    String data = (String) createUserRequest(userName, phoneNumber, null, false);
    Http.RequestBuilder req =
        new Http.RequestBuilder().bodyText(data).uri("/v1/user/create").method("POST");
    req.headers(headerMap);
    Result result = route(req);
    String response = Helpers.contentAsString(result);
    assertTrue(response.contains(ResponseCode.contentTypeRequiredError.getErrorCode()));
    assertEquals(400, result.status());
  }

  @Test
  public void testCreateUserSuccess() {

    Result result =
        performTest(
            "/v1/user/create",
            "POST",
            (Map<String, Object>) createUserRequest(userName, phoneNumber, null, true));
    String response = Helpers.contentAsString(result);
    assertTrue(response.contains("success"));
    assertEquals(200, result.status());
  }

  @Test
  public void testCreateUserFailureWithInvalidPhoneNumber() {

    Result result =
        performTest(
            "/v1/user/create",
            "POST",
            (Map<String, Object>) createUserRequest(userName, invalidPhonenumber, null, true));
    String response = Helpers.contentAsString(result);
    assertTrue(response.contains(ResponseCode.phoneNoFormatError.getErrorCode()));
    assertEquals(400, result.status());
  }

  @Test
  public void testUpdateUserProfileSuccess() {

    Result result =
        performTest(
            "/v1/user/update",
            "PATCH",
            (Map<String, Object>) createUserRequest(null, phoneNumber, userId, true));
    String response = Helpers.contentAsString(result);
    assertTrue(response.contains("success"));
    assertEquals(200, result.status());
  }

  @Test
  public void testUpdateUserFailureWithInvalidPhoneNumber() {

    Result result =
        performTest(
            "/v1/user/update",
            "PATCH",
            (Map<String, Object>) createUserRequest(null, invalidPhonenumber, userId, true));
    String response = Helpers.contentAsString(result);
    assertTrue(response.contains(ResponseCode.phoneNoFormatError.getErrorCode()));
    assertEquals(400, result.status());
  }

  @Test
  public void testGetUserDetailsSuccessByUserId() {

    Result result = performTest("/v1/user/read/user-id", "GET", null);
    String response = Helpers.contentAsString(result);
    assertTrue(response.contains("success"));
    assertEquals(200, result.status());
  }

  @Test
  public void testGetUserDetailsSuccessByLoginId() {
    Map<String, Object> requestMap = new HashMap<>();
    Map<String, Object> innerMap = new HashMap<>();
    innerMap.put(JsonKey.LOGIN_ID, loginId);
    requestMap.put(JsonKey.REQUEST, innerMap);
    Result result = performTest("/v1/user/getuser", "POST", requestMap);
    String response = Helpers.contentAsString(result);
    assertTrue(response.contains("success"));
    assertEquals(200, result.status());
  }

  @Test
  public void testGetUserDetailsFailureWithoutLoginId() {
    Map<String, Object> requestMap = new HashMap<>();
    Map<String, Object> innerMap = new HashMap<>();
    innerMap.put(JsonKey.LOGIN_ID, null);
    requestMap.put(JsonKey.REQUEST, innerMap);
    Result result = performTest("/v1/user/getuser", "POST", requestMap);
    String response = Helpers.contentAsString(result);
    assertTrue(response.contains(ResponseCode.loginIdRequired.getErrorCode()));
    assertEquals(400, result.status());
  }

  @Test
  public void testSearchUserSuccess() {
    Map<String, Object> requestMap = new HashMap<>();
    Map<String, Object> innerMap = new HashMap<>();
    Map<String, Object> filters = new HashMap<>();
    innerMap.put(JsonKey.QUERY, query);
    innerMap.put(JsonKey.FILTERS, filters);
    requestMap.put(JsonKey.REQUEST, innerMap);
    Result result = performTest("/v1/user/search", "POST", requestMap);
    String response = Helpers.contentAsString(result);
    assertTrue(response.contains("success"));
    assertEquals(200, result.status());
  }

  @Test
  public void testSearchUserFailureWithoutFilters() {
    Map<String, Object> requestMap = new HashMap<>();
    Map<String, Object> innerMap = new HashMap<>();
    requestMap.put(JsonKey.REQUEST, innerMap);
    Result result = performTest("/v1/user/search", "POST", requestMap);
    String response = Helpers.contentAsString(result);
    assertTrue(response.contains(ResponseCode.mandatoryParamsMissing.getErrorCode()));
    assertEquals(400, result.status());
  }

  private Object createUserRequest(
      String userName, String phoneNumber, String userId, boolean isContentType) {

    Map<String, Object> requestMap = new HashMap<>();
    Map<String, Object> innerMap = new HashMap<>();
    innerMap.put(JsonKey.PHONE_VERIFIED, true);
    innerMap.put(JsonKey.PHONE, phoneNumber);
    innerMap.put(JsonKey.COUNTRY_CODE, "+91");
    innerMap.put(JsonKey.EMAIL, emailId);
    if (userName != null) {
      innerMap.put(JsonKey.USERNAME, userName);
    }
    if (userId != null) {
      innerMap.put(JsonKey.USER_ID, userId);
    }
    innerMap.put(JsonKey.FIRST_NAME, firstName);
    innerMap.put(JsonKey.LAST_NAME, lastName);
    List<String> roles = new ArrayList<>();
    roles.add(role);
    List languages = new ArrayList<>();
    languages.add(language);

    innerMap.put(JsonKey.ROLES, roles);
    innerMap.put(JsonKey.LANGUAGE, languages);
    requestMap.put(JsonKey.REQUEST, innerMap);

    if (isContentType) return requestMap;
    else return mapToJson(requestMap);
  }

  public static String mapToJson(Map map) {
    ObjectMapper mapperObj = new ObjectMapper();
    String jsonResp = "";
    try {
      jsonResp = mapperObj.writeValueAsString(map);
    } catch (IOException e) {
      ProjectLogger.log(e.getMessage(), e);
    }
    return jsonResp;
  }
}
