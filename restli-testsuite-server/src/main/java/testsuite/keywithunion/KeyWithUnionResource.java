package testsuite.keywithunion;


import com.linkedin.restli.common.ComplexResourceKey;
import com.linkedin.restli.common.HttpStatus;
import com.linkedin.restli.server.RestLiServiceException;
import com.linkedin.restli.server.annotations.RestLiCollection;
import com.linkedin.restli.server.resources.ComplexKeyResourceTemplate;
import testsuite.LargeRecord;
import testsuite.Message;
import testsuite.complexkey.KeyParams;
import testsuite.complexkey.KeyWithUnion;


/**
 * @author jbetz@linkedin.com
 */
@RestLiCollection(name = "keywithunion", namespace = "testsuite.keywithunion")
public class KeyWithUnionResource extends ComplexKeyResourceTemplate<KeyWithUnion, KeyParams, LargeRecord>
{
  @Override
  public LargeRecord get(ComplexResourceKey<KeyWithUnion, KeyParams> key)
  {
    if(!key.getKey().getUnion().isComplexKey())
    {
      throw new RestLiServiceException(HttpStatus.S_404_NOT_FOUND);
    }
    return new LargeRecord().setKey(key.getKey().getUnion().getComplexKey()).setMessage(new Message().setMessage("test message"));
  }
}
