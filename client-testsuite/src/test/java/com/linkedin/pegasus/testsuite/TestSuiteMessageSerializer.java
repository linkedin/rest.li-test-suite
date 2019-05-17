/*
   Copyright (c) 2012 LinkedIn Corp.

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

/* $Id$ */
package com.linkedin.pegasus.testsuite;


import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.linkedin.data.codec.JacksonDataCodec;
import com.linkedin.r2.caprep.db.DefaultMessageSerializer;
import com.linkedin.r2.message.Request;
import com.linkedin.r2.message.Response;
import com.linkedin.r2.message.rest.RestRequest;
import com.linkedin.r2.message.rest.RestRequestBuilder;
import com.linkedin.r2.message.rest.RestResponse;
import com.linkedin.r2.message.rest.RestResponseBuilder;
import com.linkedin.restli.internal.server.util.DataMapUtils;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;


/**
 * Customization of the default serializer.
 *
 * Modified the default serializer to:
 * <ul>
 *   <li>Remove the "Server" header from responses.</li>
 *   <li>Add the "Accept: application/json" header to all requests</li>
 *   <li>Converts all URIs to relative URIs</li>
 * </ul>
 *
 */
public class TestSuiteMessageSerializer extends DefaultMessageSerializer
{
  private static final JacksonDataCodec CODEC = new JacksonDataCodec();
  static
  {
    CODEC.setPrettyPrinter(new DefaultPrettyPrinter());
  }

  private RestRequest customizeRequest(RestRequest req) throws IOException
  {
    RestRequestBuilder builder = req.builder();

    // make the URI relative
    URI uri = req.getURI();
    URI relativeUri;
    try
    {
      relativeUri = new URI(uri.getRawPath() + ((uri.getRawQuery() != null) ? "?" + uri.getRawQuery() : ""));
    }
    catch (URISyntaxException e)
    {
      throw new IllegalArgumentException("Invalid URI", e); // should never happen since the URI parts are from an already valid URI
    }
    builder.setURI(relativeUri);

    // add the accept header
    Map<String, String> headers = new HashMap<String, String>(builder.getHeaders());
    headers.put("Accept", "application/json");
    builder.overwriteHeaders(headers);

    if(req.getEntity().length() > 0)
    {
      builder.setEntity(CODEC.mapToBytes(DataMapUtils.readMap(req)));
    }

    return builder.build();
  }

  private RestResponse customizeResponse(RestResponse res)
      throws IOException
  {
    RestResponseBuilder builder = res.builder();
    Map<String, String> headers = new HashMap<String, String>(builder.getHeaders());
    headers.remove("Server");
    builder.setHeaders(headers);

    if(res.getEntity().length() > 0)
    {
      builder.setEntity(CODEC.mapToBytes(DataMapUtils.readMap(res)));
    }

    return builder.build();
  }

  @Override
  public void writeRequest(OutputStream out, RestRequest req) throws IOException
  {
    if(req instanceof RestRequest)
    {
      super.writeRequest(out, customizeRequest(req));
    }
    else
    {
      super.writeRequest(out, req);
    }
  }

  @Override
  public void writeResponse(OutputStream out, RestResponse res) throws IOException
  {
    if(res instanceof RestResponse)
    {
      super.writeResponse(out, customizeResponse(res));
    }
    else
    {
      super.writeResponse(out, res);
    }
  }
}
