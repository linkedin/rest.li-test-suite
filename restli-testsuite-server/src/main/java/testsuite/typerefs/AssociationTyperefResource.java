package testsuite.typerefs;


import com.linkedin.restli.common.CompoundKey;
import com.linkedin.restli.server.annotations.Key;
import com.linkedin.restli.server.annotations.RestLiAssociation;
import com.linkedin.restli.server.resources.AssociationResourceTemplate;
import testsuite.Message;
import testsuite.Url;


/**
 * @author jbetz@linkedin.com
 */
@RestLiAssociation(name = "associationTyperef",
                   namespace = "testsuite.typerefs",
                   assocKeys = {
                       @Key(name = "part1", type = String.class, typeref = Url.class),
                       @Key(name = "part2", type = Long.class)
                   })
public class AssociationTyperefResource extends AssociationResourceTemplate<Message>
{
  @Override
  public Message get(CompoundKey key)
  {
    return new Message().setMessage("test message");
  }
}
