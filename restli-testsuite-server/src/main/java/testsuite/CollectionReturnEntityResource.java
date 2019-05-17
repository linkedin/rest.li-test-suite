/*
 Copyright (c) 2018 LinkedIn Corp.

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
import com.linkedin.restli.server.CreateKVResponse;
import com.linkedin.restli.server.RestLiServiceException;
import com.linkedin.restli.server.UpdateResponse;
import com.linkedin.restli.server.annotations.Finder;
import com.linkedin.restli.server.annotations.QueryParam;
import com.linkedin.restli.server.annotations.RestLiCollection;
import com.linkedin.restli.server.annotations.ReturnEntity;
import com.linkedin.restli.server.resources.CollectionResourceTemplate;
import com.linkedin.restli.server.util.PatchApplier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * @author katlee@linkedin.com
 */
@RestLiCollection(name = "collectionReturnEntity", namespace = "testsuite")
public class CollectionReturnEntityResource extends CollectionResourceTemplate<Long, Message>
{

  @ReturnEntity
  @Override
  public CreateKVResponse create(Message entity) {

    if(entity.getMessage().equals("test message"))
    {
      return new CreateKVResponse<Long, Message>(1l, entity, HttpStatus.S_201_CREATED);
    }
    else if(entity.getMessage().equals("another message"))
    {
      return new CreateKVResponse<Long, Message>(3l, entity, HttpStatus.S_201_CREATED);
    }
    else
    {
      return new CreateKVResponse<Long, Message>(null, entity, HttpStatus.S_404_NOT_FOUND);
    }
  }
}
