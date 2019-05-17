package testsuite.simplesubresources;


import com.linkedin.restli.server.annotations.RestLiCollection;
import com.linkedin.restli.server.resources.CollectionResourceTemplate;
import testsuite.Message;
import testsuite.SimpleResource;


/**
 * @author jbetz@linkedin.com
 */
// TODO: mismatching annotations and template types can result in confusing NPEs,  better error messages would help
@RestLiCollection(name = "subcollection", parent = SimpleResource.class, namespace = "testsuite.simplesubresources")
public class CollectionSubResource extends CollectionResourceTemplate<Long, Message>
{
  @Override
  public Message get(Long key)
  {
    return new Message().setId(key).setMessage("sub collection message");
  }
}
