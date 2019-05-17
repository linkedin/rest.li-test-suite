package testsuite.collectionsubresources;


import com.linkedin.restli.server.annotations.RestLiSimpleResource;
import com.linkedin.restli.server.resources.SimpleResourceTemplate;
import testsuite.CollectionResource;
import testsuite.Message;


/**
 * @author jbetz@linkedin.com
 */
@RestLiSimpleResource(name = "subsimple", parent = CollectionResource.class, namespace = "testsuite.collectionsubresources")
public class SimpleSubResource extends SimpleResourceTemplate<Message>
{
  @Override
  public Message get()
  {
    return new Message().setMessage("sub simple message");
  }
}
