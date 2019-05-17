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

package com.linkedin.pegasus.test;


import com.linkedin.data.DataMap;
import com.linkedin.data.template.JacksonDataTemplateCodec;
import com.linkedin.r2.message.rest.RestMessageBuilder;
import com.linkedin.r2.message.rest.RestRequest;
import com.linkedin.r2.message.rest.RestRequestBuilder;
import com.linkedin.restli.client.Request;
import com.linkedin.restli.client.uribuilders.RestliUriBuilderUtil;
import com.linkedin.restli.common.CollectionRequest;
import com.linkedin.restli.common.HttpMethod;
import com.linkedin.restli.common.KeyValueRecord;
import com.linkedin.restli.common.PatchRequest;
import com.linkedin.restli.common.ProtocolVersion;
import com.linkedin.restli.common.ResourceSpec;
import com.linkedin.restli.common.RestConstants;
import com.linkedin.restli.common.TypeSpec;
import com.linkedin.restli.internal.client.CollectionRequestUtil;
import com.linkedin.restli.internal.common.AllProtocolVersions;
import java.io.IOException;
import java.net.URI;


/**
 * Built a R2 RestRequest from a Rest.li Request.
 *
 * This class should not need to exist.  A call to RestRequestBuilder.build should be all that is needed.
 *
 * But logic has been bled into RestClient and cannot currently be reused, so I've emulated it here, at
 * least until is pull out of RestClient so it can be reused properly.
 *
 * @author jbetz@linkedin.com
 * @author mnchen@linkedin.com
 */
public class MockRestliClientRequestBuilder
{
  public static RestRequest buildRequest(Request<?> request)
  {
    ProtocolVersion protocolVersion;
    switch (request.getRequestOptions().getProtocolVersionOption())
    {
      case FORCE_USE_PREVIOUS:
        protocolVersion = AllProtocolVersions.PREVIOUS_PROTOCOL_VERSION;
        break;
      case FORCE_USE_LATEST:
      case USE_LATEST_IF_AVAILABLE:
        protocolVersion = AllProtocolVersions.LATEST_PROTOCOL_VERSION;
        break;
      case FORCE_USE_NEXT:
        protocolVersion = AllProtocolVersions.NEXT_PROTOCOL_VERSION;
        break;
      default:
        throw new IllegalArgumentException("Unsupported enum value: " + request.getRequestOptions().getProtocolVersionOption());
    }

    URI uri = RestliUriBuilderUtil.createUriBuilder(request, "", protocolVersion).build();
    RestRequestBuilder requestBuilder = new RestRequestBuilder(uri);
    requestBuilder.setMethod(request.getMethod().getHttpMethod().name());

    // unfortunately some headers get set in RestClient, and since we're not using rest client, we
    // replicate that behavior here
    requestBuilder.setHeader(RestConstants.HEADER_ACCEPT, RestConstants.HEADER_VALUE_APPLICATION_JSON);

    requestBuilder.setHeader(RestConstants.HEADER_RESTLI_PROTOCOL_VERSION, protocolVersion.toString());

    if (request.getMethod().getHttpMethod() == HttpMethod.POST)
    {
      requestBuilder.setHeader(RestConstants.HEADER_RESTLI_REQUEST_METHOD, request.getMethod().toString());
    }

    if (request.getInputRecord() != null)
    {
      requestBuilder.setHeader(RestConstants.HEADER_CONTENT_TYPE, RestConstants.HEADER_VALUE_APPLICATION_JSON);
      DataMap inputData = getInputData(request, protocolVersion);
      writeEntity(inputData, requestBuilder);
    }

    return requestBuilder.build();
  }

  @SuppressWarnings("unchecked")
  private static <T> DataMap getInputData(Request<T> request, ProtocolVersion version)
  {
    ResourceSpec resourceSpec = request.getResourceSpec();
    switch (request.getMethod())
    {
      case BATCH_UPDATE:
        return CollectionRequestUtil.
            convertToBatchRequest((CollectionRequest<KeyValueRecord>) request.getInputRecord(),
                                  resourceSpec.getKeyType(),
                                  resourceSpec.getComplexKeyType(),
                                  resourceSpec.getKeyParts(),
                                  resourceSpec.getValueType(),
                                  version).data();
      case BATCH_PARTIAL_UPDATE:
        return CollectionRequestUtil.
            convertToBatchRequest((CollectionRequest<KeyValueRecord>) request.getInputRecord(),
                                  resourceSpec.getKeyType(),
                                  resourceSpec.getComplexKeyType(),
                                  resourceSpec.getKeyParts(),
                                  new TypeSpec<PatchRequest>(PatchRequest.class),
                                  version).data();
      default:
        return request.getInputRecord().data();
    }
  }

  private static final JacksonDataTemplateCodec CODEC = new JacksonDataTemplateCodec();

  private static void writeEntity(DataMap inputData, RestMessageBuilder<?> messageBuilder)
  {
    try
    {
      messageBuilder.setEntity(CODEC.mapToBytes(inputData));
    }
    catch (IOException e)
    {
      throw new RuntimeException("Unable to serializing data", e);
    }
  }
}
