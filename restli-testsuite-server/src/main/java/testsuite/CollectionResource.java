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


import com.linkedin.data.DataMap;
import com.linkedin.data.transform.DataProcessingException;
import com.linkedin.restli.common.HttpStatus;
import com.linkedin.restli.common.PatchRequest;
import com.linkedin.restli.server.BatchCreateRequest;
import com.linkedin.restli.server.BatchCreateResult;
import com.linkedin.restli.server.BatchDeleteRequest;
import com.linkedin.restli.server.BatchPatchRequest;
import com.linkedin.restli.server.BatchUpdateRequest;
import com.linkedin.restli.server.BatchUpdateResult;
import com.linkedin.restli.server.CollectionResult;
import com.linkedin.restli.server.CreateResponse;
import com.linkedin.restli.server.RestLiServiceException;
import com.linkedin.restli.server.UpdateResponse;
import com.linkedin.restli.server.annotations.Finder;
import com.linkedin.restli.server.annotations.QueryParam;
import com.linkedin.restli.server.annotations.RestLiCollection;
import com.linkedin.restli.server.resources.CollectionResourceTemplate;
import com.linkedin.restli.server.util.PatchApplier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * @author jbetz@linkedin.com
 */
@RestLiCollection(name = "collection", namespace = "testsuite")
public class CollectionResource extends CollectionResourceTemplate<Long, Message>
{
  @Override
  public Message get(Long key)
  {
    switch(key.intValue())
    {
      case 1: return new Message().setId(1).setMessage("test message");
      case 2: return null; // 404
      case 3: return new Message().setId(2).setMessage("another message");
      default: return null;
    }
  }

  @Override
  public Map<Long, Message> batchGet(Set<Long> ids)
  {
    HashMap<Long, Message> results = new HashMap<Long, Message>();
    for(Long id : ids)
    {
      results.put(id, get(id));
    }
    return results;
  }

  @Override
  public UpdateResponse update(Long key, Message entity)
  {
    if(!entity.hasMessage())
    {
      throw new RestLiServiceException(HttpStatus.S_400_BAD_REQUEST, "Missing required field: message");
    }

    switch(key.intValue())
    {
      case 1: return new UpdateResponse(HttpStatus.S_204_NO_CONTENT);
      case 2: return new UpdateResponse(HttpStatus.S_404_NOT_FOUND);
      case 3: return new UpdateResponse(HttpStatus.S_201_CREATED);
      case 4: throw new RestLiServiceException(HttpStatus.S_500_INTERNAL_SERVER_ERROR);
      case 5:
        RestLiServiceException ex = new RestLiServiceException(HttpStatus.S_500_INTERNAL_SERVER_ERROR);
        DataMap errorDetails = new DataMap(); // TODO: these error details are not included in the response
        errorDetails.put("one", 1);
        errorDetails.put("two", 2);
        ex.setErrorDetails(errorDetails);
        throw ex;
      default: return new UpdateResponse(HttpStatus.S_404_NOT_FOUND);
    }
  }

  @Override
  public UpdateResponse update(Long key, PatchRequest<Message> patch)
  {
    try
    {
      PatchApplier.applyPatch(get(key), patch);
      return new UpdateResponse(HttpStatus.S_204_NO_CONTENT);
    }
    catch (DataProcessingException e)
    {
      throw new RestLiServiceException(HttpStatus.S_400_BAD_REQUEST, e.getMessage());
    }
  }

  @Override
  public BatchUpdateResult<Long, Message> batchUpdate(BatchUpdateRequest<Long, Message> entities)
  {
    HashMap<Long, UpdateResponse> results = new HashMap<Long, UpdateResponse>();
    HashMap<Long, RestLiServiceException> errors = new HashMap<Long, RestLiServiceException>();
    for(Map.Entry<Long, Message> entry : entities.getData().entrySet())
    {
      try
      {
        UpdateResponse updateResponse = update(entry.getKey(), entry.getValue());
        results.put(entry.getKey(), updateResponse);
      }
      catch(RestLiServiceException e)
      {
        errors.put(entry.getKey(), e);
      }
    }
    return new BatchUpdateResult<Long, Message>(results, errors);
  }

  @Override
  public BatchUpdateResult<Long, Message> batchUpdate(BatchPatchRequest<Long, Message> entityUpdates)
  {
    HashMap<Long, UpdateResponse> results = new HashMap<Long, UpdateResponse>();
    HashMap<Long, RestLiServiceException> errors = new HashMap<Long, RestLiServiceException>();
    for(Map.Entry<Long, PatchRequest<Message>> entry : entityUpdates.getData().entrySet())
    {
      try
      {
        UpdateResponse updateResponse = update(entry.getKey(), entry.getValue());
        results.put(entry.getKey(), updateResponse);
      }
      catch(RestLiServiceException e)
      {
        errors.put(entry.getKey(), e);
      }
    }
    return new BatchUpdateResult<Long, Message>(results, errors);
  }

  @Override
  public UpdateResponse delete(Long key)
  {
    switch(key.intValue())
    {
      case 1: return new UpdateResponse(HttpStatus.S_204_NO_CONTENT);
      case 2: return new UpdateResponse(HttpStatus.S_404_NOT_FOUND);
      default: return new UpdateResponse(HttpStatus.S_404_NOT_FOUND);
    }
  }

  @Override
  public BatchUpdateResult<Long, Message> batchDelete(BatchDeleteRequest<Long, Message> ids)
  {
    HashMap<Long, UpdateResponse> results = new HashMap<Long, UpdateResponse>();
    HashMap<Long, RestLiServiceException> errors = new HashMap<Long, RestLiServiceException>();
    for(Long id : ids.getKeys())
    {
      try
      {
        UpdateResponse updateResponse = delete(id);
        results.put(id, updateResponse);
      }
      catch(RestLiServiceException e)
      {
        errors.put(id, e);
      }
    }
    return new BatchUpdateResult<Long, Message>(results, errors);
  }

  @Override
  public CreateResponse create(Message entity)
  {
    if(entity.getMessage().equals("internal error test"))
    {
      throw new NullPointerException("simulated NPE");
    }
    else if(entity.getMessage().equals("error details test"))
    {
      RestLiServiceException ex = new RestLiServiceException(HttpStatus.S_400_BAD_REQUEST, "error details message");
      DataMap errorDetails = new DataMap();
      errorDetails.put("one", 1);
      errorDetails.put("two", 2);
      ex.setErrorDetails(errorDetails);
      throw ex;
    }
    else if(entity.getMessage().equals("test message"))
    {
      return new CreateResponse(1l, HttpStatus.S_201_CREATED);
    }
    else if(entity.getMessage().equals("another message"))
    {
      return new CreateResponse(3l, HttpStatus.S_201_CREATED);
    }
    else
    {
      return new CreateResponse(HttpStatus.S_404_NOT_FOUND);
    }
  }

  private CreateResponse createWrapper(Message entity)
  {
    try
    {
      return create(entity);
    }
    catch (Exception e)
    {
      RestLiServiceException restLiServiceException;
      if (e instanceof RestLiServiceException)
      {
        restLiServiceException = (RestLiServiceException) e;
      }
      else
      {
        restLiServiceException = new RestLiServiceException(HttpStatus.S_500_INTERNAL_SERVER_ERROR, e.getMessage(), e);
      }
      return new CreateResponse(restLiServiceException);
    }
  }

  @Override
  public BatchCreateResult<Long, Message> batchCreate(BatchCreateRequest<Long, Message> entities)
  {
    List<CreateResponse> results = new ArrayList<CreateResponse>();
    for(Message entry : entities.getInput())
    {
      CreateResponse response = createWrapper(entry);
      results.add(response);
    }
    return new BatchCreateResult<Long, Message>(results);
  }

  @Finder("search")
  public CollectionResult<Message, Optionals> search(@QueryParam("keyword") String keyword)
  {
    ArrayList<Message> results = new ArrayList<Message>();

    for(long i = 1l; i < 4l; i++)
    {
      Message message = get(i);
      if(message != null)
      {
        if(message.getMessage().contains(keyword))
        {
          results.add(message);
        }
      }
    }

    Optionals metadata = new Optionals().setOptionalLong(5l).setOptionalString("metadata");
    return new CollectionResult<Message, Optionals>(results, results.size(), metadata);
  }
}
