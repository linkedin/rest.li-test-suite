package testsuite;


import com.linkedin.restli.common.HttpStatus;
import com.linkedin.restli.server.UpdateResponse;
import com.linkedin.restli.server.annotations.RestLiSimpleResource;
import com.linkedin.restli.server.resources.SimpleResourceTemplate;


/**
 * @author jbetz@linkedin.com
 */
@RestLiSimpleResource(name = "simple", namespace = "testsuite")
public class SimpleResource extends SimpleResourceTemplate<Message>
{
  @Override
  public Message get()
  {
    return new Message().setMessage("test message");
  }

  @Override
  public UpdateResponse update(Message entity)
  {
    return new UpdateResponse(HttpStatus.S_204_NO_CONTENT);
  }

  @Override
  public UpdateResponse delete()
  {
    return new UpdateResponse(HttpStatus.S_204_NO_CONTENT);
  }
}
