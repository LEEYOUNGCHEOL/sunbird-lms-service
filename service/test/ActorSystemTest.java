import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import controllers.actorutility.ActorSystemFactory;
import controllers.actorutility.impl.LocalActorSystem;
import controllers.actorutility.impl.RemoteActorSystem;
import junit.framework.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sunbird.common.models.util.ConfigUtil;
import org.sunbird.common.models.util.JsonKey;

public class ActorSystemTest {
  
  static String provider = null;
      
  @BeforeClass
  public static void setUp() {
    
    provider = ConfigUtil.getString(JsonKey.API_ACTOR_PROVIDER);
  }
  
  @SuppressWarnings("deprecation")
  @Test
  public void testActorSystem(){
    Object obj = ActorSystemFactory.getActorSystem();
     if(provider.equalsIgnoreCase("local")){
       Assert.assertTrue(obj instanceof LocalActorSystem);
     } else {
       Assert.assertTrue(obj instanceof RemoteActorSystem);
     }
  }
  
  @SuppressWarnings("deprecation")
  @Test
  public void testActorRef(){
    Object obj = ActorSystemFactory.getActorSystem().initializeActorSystem();
     if(provider.equalsIgnoreCase("local")){
       Assert.assertTrue(obj instanceof ActorRef);
     } else {
       Assert.assertTrue(obj instanceof ActorSelection);
     }
  }

}
