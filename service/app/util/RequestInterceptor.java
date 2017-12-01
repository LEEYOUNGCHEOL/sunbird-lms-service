package util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.sunbird.common.models.util.ConfigUtil;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.models.util.LoggerEnum;
import org.sunbird.common.models.util.ProjectLogger;
import org.sunbird.common.models.util.ProjectUtil;
import org.sunbird.common.request.HeaderParam;
import org.sunbird.common.responsecode.ResponseCode;
import play.mvc.Http.Request;

/**
 * This class will do the request header validation
 * 
 * @author Amit Kumar
 *
 */
public class RequestInterceptor {

  private RequestInterceptor() {}
  protected static List<String> restrictedUriList = null;
  private static ConcurrentHashMap<String, Short> apiHeaderIgnoreMap = new ConcurrentHashMap<>();
  static {
    restrictedUriList = new ArrayList<>();
    restrictedUriList.add("/v1/user/update");
    restrictedUriList.add("/v1/note/create");
    restrictedUriList.add("/v1/note/update");
    restrictedUriList.add("/v1/note/search");
    restrictedUriList.add("/v1/note/read");
    restrictedUriList.add("/v1/note/delete");
    restrictedUriList.add("/v1/content/state/update");
    
    //---------------------------
    short var = 1;
    apiHeaderIgnoreMap.put("/v1/user/create", var);
    apiHeaderIgnoreMap.put("/v1/org/search", var);
    apiHeaderIgnoreMap.put("/v1/health", var);
    apiHeaderIgnoreMap.put("/v1/page/assemble", var);
    apiHeaderIgnoreMap.put("/health", var);
    apiHeaderIgnoreMap.put("/v1/notification/email", var);
    apiHeaderIgnoreMap.put("/v1/data/sync", var);
    apiHeaderIgnoreMap.put("/v1/user/data/encrypt", var);
    apiHeaderIgnoreMap.put("/v1/user/data/decrypt", var);
    apiHeaderIgnoreMap.put("/v1/file/upload", var);
    apiHeaderIgnoreMap.put("/v1/user/forgotpassword", var);
    apiHeaderIgnoreMap.put("/v1/user/login", var);
    apiHeaderIgnoreMap.put("/v1/user/logout", var);
    apiHeaderIgnoreMap.put("/v1/object/read/list", var);
    apiHeaderIgnoreMap.put("/v1/object/read", var);
    apiHeaderIgnoreMap.put("/v1/object/create", var);
    apiHeaderIgnoreMap.put("/v1/object/update", var);
    apiHeaderIgnoreMap.put("/v1/object/delete", var);
    apiHeaderIgnoreMap.put("/v1/object/search", var);
    apiHeaderIgnoreMap.put("/v1/client/register",var);
    apiHeaderIgnoreMap.put("/v1/client/key/read", var);
    apiHeaderIgnoreMap.put("/v1/notification/send", var);
    apiHeaderIgnoreMap.put("/v1/user/getuser", var);
  }

  /**
   * This Method will do the request header validation.
   * 
   * @param request Request
   * @return String
   */
  public static String verifyRequestData(Request request) {
    String response = "{userId}";
    if (!isRequestInExcludeList(request.path())) {
      if (ProjectUtil
          .isStringNullOREmpty(request.getHeader(HeaderParam.X_Access_TokenId.getName()))) {
        if (ProjectUtil
            .isStringNullOREmpty(request.getHeader(HeaderParam.X_Authenticated_Client_Token.getName()))
            && ProjectUtil
                .isStringNullOREmpty(request.getHeader(HeaderParam.X_Authenticated_Client_Id.getName()))) {
          return ResponseCode.unAuthorised.getErrorCode();
        }
      }
      if (ConfigUtil.config.hasPath(JsonKey.SSO_PUBLIC_KEY)
          && ConfigUtil.config.getBoolean(JsonKey.IS_SSO_ENABLED)) {
        ProjectLogger.log("SSO public key is not set by environment variable==",
            LoggerEnum.INFO.name());
        response = "{userId}" + JsonKey.NOT_AVAILABLE;
      } else if(!ProjectUtil
            .isStringNullOREmpty(request.getHeader(HeaderParam.X_Authenticated_Client_Token.getName()))
            && !ProjectUtil
                .isStringNullOREmpty(request.getHeader(HeaderParam.X_Authenticated_Client_Id.getName()))) {
        String clientId = AuthenticationHelper.verifyClientAccessToken(request.getHeader(HeaderParam.X_Authenticated_Client_Id.getName()),
            request.getHeader(HeaderParam.X_Authenticated_Client_Token.getName()));
        if (ProjectUtil.isStringNullOREmpty(clientId)) {
          return ResponseCode.unAuthorised.getErrorCode();
        }
        response = "{userId}" + clientId;
      } else {
        String userId = AuthenticationHelper.verifyUserAccesToken(
            request.getHeader(HeaderParam.X_Access_TokenId.getName()));
        if (ProjectUtil.isStringNullOREmpty(userId)) {
          return ResponseCode.unAuthorised.getErrorCode();
        }
        response = "{userId}" + userId;
      }
    }else{
      AuthenticationHelper.invalidateToken("");
    }
    return response;
  }
  
  /**
   * this method will check incoming request required validation or not. if this method return true
   * it means no need of validation other wise validation is required.
   * 
   * @param request Stirng URI
   * @return boolean
   */
  public static boolean isRequestInExcludeList(String request) {
    boolean resp = false;
    if (!ProjectUtil.isStringNullOREmpty(request)) {
      if (apiHeaderIgnoreMap.containsKey(request)) {
        resp = true;
      } else {
        String[] splitedpath = request.split("[/]");
        request = removeLastValue(splitedpath);
        if (apiHeaderIgnoreMap.containsKey(request)) {
          resp = true;
        }
      }
    }
    return resp;
  }
  
  /**
   * Method to remove last value
   * 
   * @param splited String []
   * @return String
   */
  private static String removeLastValue(String splited[]) {

    StringBuilder builder = new StringBuilder();
    if (splited != null && splited.length > 0) {
      for (int i = 1; i < splited.length - 1; i++) {
        builder.append("/" + splited[i]);
      }
    }
    return builder.toString();
  }
  
}
