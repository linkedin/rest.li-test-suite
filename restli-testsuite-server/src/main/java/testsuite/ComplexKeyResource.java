/*
 Copyright (c) 2014 LinkedIn Corp.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
 */

package testsuite;


import com.linkedin.data.transform.DataProcessingException;
import com.linkedin.restli.common.ComplexResourceKey;
import com.linkedin.restli.common.HttpStatus;
import com.linkedin.restli.common.PatchRequest;
import com.linkedin.restli.server.BatchCreateRequest;
import com.linkedin.restli.server.BatchCreateResult;
import com.linkedin.restli.server.BatchDeleteRequest;
import com.linkedin.restli.server.BatchUpdateRequest;
import com.linkedin.restli.server.BatchUpdateResult;
import com.linkedin.restli.server.CreateResponse;
import com.linkedin.restli.server.RestLiServiceException;
import com.linkedin.restli.server.UpdateResponse;
import com.linkedin.restli.server.annotations.RestLiCollection;
import com.linkedin.restli.server.resources.ComplexKeyResourceTemplate;
import com.linkedin.restli.server.util.PatchApplier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import testsuite.complexkey.ComplexKey;
import testsuite.complexkey.KeyParams;

/**
 * @author jbetz@linkedin.com
 */
@RestLiCollection(name = "complexkey", namespace = "testsuite")
public class ComplexKeyResource extends ComplexKeyResourceTemplate<ComplexKey, KeyParams, LargeRecord>
{
  @Override
  public LargeRecord get(ComplexResourceKey<ComplexKey, KeyParams> key)
  {
    return new LargeRecord().setKey(key.getKey()).setMessage(new Message().setMessage("test message"));
  }

  @Override
  public Map<ComplexResourceKey<ComplexKey, KeyParams>, LargeRecord> batchGet(Set<ComplexResourceKey<ComplexKey, KeyParams>> ids)
  {
    Map<ComplexResourceKey<ComplexKey, KeyParams>, LargeRecord> results = new HashMap<ComplexResourceKey<ComplexKey, KeyParams>, LargeRecord>();
    for(ComplexResourceKey<ComplexKey, KeyParams> id : ids)
    {
      results.put(id, get(id));
    }
    return results;
  }

  @Override
  public UpdateResponse update(ComplexResourceKey<ComplexKey, KeyParams> key, LargeRecord entity)
  {
    return new UpdateResponse(HttpStatus.S_204_NO_CONTENT);
  }

  @Override
  public UpdateResponse update(ComplexResourceKey<ComplexKey, KeyParams> key, PatchRequest<LargeRecord> patch)
  {
    LargeRecord largeRecord = get(key);
    if(largeRecord == null) return new UpdateResponse(HttpStatus.S_404_NOT_FOUND);

    try
    {
      PatchApplier.applyPatch(largeRecord, patch);
      return new UpdateResponse(HttpStatus.S_204_NO_CONTENT);
    }
    catch (DataProcessingException e)
    {
      throw new RestLiServiceException(HttpStatus.S_400_BAD_REQUEST, e.getMessage());
    }
  }

  @Override
  public BatchUpdateResult<ComplexResourceKey<ComplexKey, KeyParams>, LargeRecord> batchUpdate(BatchUpdateRequest<ComplexResourceKey<ComplexKey, KeyParams>, LargeRecord> entities)
  {
    Map<ComplexResourceKey<ComplexKey, KeyParams>, UpdateResponse> results = new HashMap<ComplexResourceKey<ComplexKey, KeyParams>, UpdateResponse>();
    for(Map.Entry<ComplexResourceKey<ComplexKey, KeyParams>, LargeRecord> entry: entities.getData().entrySet())
    {
      UpdateResponse update = update(entry.getKey(), entry.getValue());
      results.put(entry.getKey(), update);
    }
    return new BatchUpdateResult<ComplexResourceKey<ComplexKey, KeyParams>, LargeRecord>(results);
  }

  @Override
  public UpdateResponse delete(ComplexResourceKey<ComplexKey, KeyParams> key)
  {
    return new UpdateResponse(HttpStatus.S_204_NO_CONTENT);
  }

  @Override
  public BatchUpdateResult<ComplexResourceKey<ComplexKey, KeyParams>, LargeRecord> batchDelete(BatchDeleteRequest<ComplexResourceKey<ComplexKey, KeyParams>, LargeRecord> ids)
  {
    Map<ComplexResourceKey<ComplexKey, KeyParams>, UpdateResponse> results = new HashMap<ComplexResourceKey<ComplexKey, KeyParams>, UpdateResponse>();
    for(ComplexResourceKey<ComplexKey, KeyParams> key: ids.getKeys())
    {
      UpdateResponse update = delete(key);
      results.put(key, update);
    }
    return new BatchUpdateResult<ComplexResourceKey<ComplexKey, KeyParams>, LargeRecord>(results);
  }

  @Override
  public CreateResponse create(LargeRecord entity)
  {
    return new CreateResponse(
        new ComplexResourceKey<ComplexKey, KeyParams>(entity.getKey(), null),
        HttpStatus.S_201_CREATED
    );
  }

  @Override
  public BatchCreateResult<ComplexResourceKey<ComplexKey, KeyParams>, LargeRecord> batchCreate(BatchCreateRequest<ComplexResourceKey<ComplexKey, KeyParams>, LargeRecord> entities)
  {
    List<CreateResponse> results = new ArrayList<CreateResponse>();
    for(LargeRecord input: entities.getInput())
    {
      CreateResponse createResponse = create(input);
      results.add(createResponse);
    }

    return new BatchCreateResult<ComplexResourceKey<ComplexKey, KeyParams>, LargeRecord>(results);
  }
}
