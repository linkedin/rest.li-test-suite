package testsuite.simplesubresources;


import com.linkedin.restli.server.annotations.RestLiSimpleResource;
import com.linkedin.restli.server.resources.SimpleResourceTemplate;
import testsuite.Message;
import testsuite.SimpleResource;


/**
 * @author jbetz@linkedin.com
 */
@RestLiSimpleResource(name = "subsimple", parent = SimpleResource.class, namespace = "testsuite.simplesubresources")
public class SimpleSubResource extends SimpleResourceTemplate<Message>
{
  @Override
  public Message get()
  {
    return new Message().setMessage("sub simple message");
  }
}
