package testsuite.collectionsubresources;


import com.linkedin.restli.server.annotations.RestLiCollection;
import com.linkedin.restli.server.resources.CollectionResourceTemplate;
import testsuite.CollectionResource;
import testsuite.Message;


/**
 * @author jbetz@linkedin.com
 */
// TODO: mismatching annotations and template types can result in confusing NPEs,  better error messages would help
@RestLiCollection(name = "subcollection", parent = CollectionResource.class, namespace = "testsuite.collectionsubresources")
public class CollectionSubResource extends CollectionResourceTemplate<Long, Message>
{
  @Override
  public Message get(Long key)
  {
    return new Message().setId(key).setMessage("sub collection message");
  }
}
