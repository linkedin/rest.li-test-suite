package testsuite;


import com.linkedin.restli.common.CompoundKey;
import com.linkedin.restli.common.HttpStatus;
import com.linkedin.restli.server.BatchDeleteRequest;
import com.linkedin.restli.server.BatchUpdateRequest;
import com.linkedin.restli.server.BatchUpdateResult;
import com.linkedin.restli.server.UpdateResponse;
import com.linkedin.restli.server.annotations.AssocKey;
import com.linkedin.restli.server.annotations.Finder;
import com.linkedin.restli.server.annotations.Key;
import com.linkedin.restli.server.annotations.RestLiAssociation;
import com.linkedin.restli.server.resources.AssociationResourceTemplate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import testsuite.complexkey.ComplexKey;


/**
 * @author jbetz@linkedin.com
 */
@RestLiAssociation(name = "association",
                   namespace = "testsuite",
                   assocKeys = {
                       @Key(name = "part1", type = String.class),
                       @Key(name = "part2", type = Long.class),
                       @Key(name = "part3", type = String.class)
                   })
public class AssociationResource extends AssociationResourceTemplate<LargeRecord>
{
  private ComplexKey convert(CompoundKey key)
  {
    String part1 = (String)key.getPart("part1");
    Long part2 = (Long)key.getPart("part2");
    String part3 = (String)key.getPart("part3");

    return new ComplexKey().setPart1(part1).setPart2(part2).setPart3(Fruits.valueOf(part3));
  }

  @Override
  public LargeRecord get(CompoundKey key)
  {
    ComplexKey recordKey = convert(key);
    return new LargeRecord().setKey(recordKey).setMessage(new Message().setMessage("test message"));
  }

  @Override
  public Map<CompoundKey, LargeRecord> batchGet(Set<CompoundKey> ids)
  {
    HashMap<CompoundKey, LargeRecord> results = new HashMap<CompoundKey, LargeRecord>();
    for(CompoundKey key: ids)
    {
      LargeRecord record = get(key);
      results.put(key, record);
    }
    return results;
  }

  @Override
  public UpdateResponse update(CompoundKey key, LargeRecord entity)
  {
    return new UpdateResponse(HttpStatus.S_204_NO_CONTENT);
  }

  @Override
  public BatchUpdateResult<CompoundKey, LargeRecord> batchUpdate(BatchUpdateRequest<CompoundKey, LargeRecord> entities)
  {
    Map<CompoundKey, UpdateResponse> results = new HashMap<CompoundKey, UpdateResponse>();
    for(Map.Entry<CompoundKey, LargeRecord> entry: entities.getData().entrySet())
    {
      UpdateResponse update = update(entry.getKey(), entry.getValue());
      results.put(entry.getKey(), update);
    }
    return new BatchUpdateResult<CompoundKey, LargeRecord>(results);
  }

  @Override
  public UpdateResponse delete(CompoundKey key)
  {
    return new UpdateResponse(HttpStatus.S_204_NO_CONTENT);
  }

  @Override
  public BatchUpdateResult<CompoundKey, LargeRecord> batchDelete(BatchDeleteRequest<CompoundKey, LargeRecord> ids)
  {
    Map<CompoundKey, UpdateResponse> results = new HashMap<CompoundKey, UpdateResponse>();
    for(CompoundKey key: ids.getKeys())
    {
      UpdateResponse update = delete(key);
      results.put(key, update);
    }
    return new BatchUpdateResult<CompoundKey, LargeRecord>(results);
  }

  @Finder("part1")
  public List<LargeRecord> byPart1(@AssocKey("part1") String part1)
  {
    ArrayList<LargeRecord> results = new ArrayList<LargeRecord>();
    for(long i = 1l; i < 4l; i++)
    {
      results.add(get(new CompoundKey().append("part1", part1).append("part2", i).append("part3", "APPLE")));
    }
    return results;
  }
}
