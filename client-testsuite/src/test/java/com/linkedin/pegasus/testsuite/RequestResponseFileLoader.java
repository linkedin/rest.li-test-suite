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

package com.linkedin.pegasus.testsuite;


import com.linkedin.r2.message.rest.RestRequest;
import com.linkedin.r2.message.rest.RestResponse;
import com.linkedin.restli.client.Response;
import com.linkedin.restli.common.ErrorResponse;
import com.linkedin.restli.internal.client.EntityResponseDecoder;
import java.io.File;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;


/**
 * Loads requests and responses from files.
 *
 * @author jbetz@linkedin.com
 */
public class RequestResponseFileLoader
{
  private final File _rootDir;
  private final TestSuiteMessageSerializer _serializer;

  public RequestResponseFileLoader(File rootDir)
  {
    _rootDir = rootDir;
    _serializer = new TestSuiteMessageSerializer();
  }

  public RestRequest loadRestRequest(String requestFilename) throws Exception
  {
    String requestString = FileUtils.readFileToString(new File(_rootDir + File.separator + requestFilename.replaceAll("/", File.separator)));
    return new TestSuiteMessageSerializer().readRestRequest(IOUtils.toInputStream(requestString));
  }

  private static final EntityResponseDecoder<ErrorResponse> ERROR_DECODER = new EntityResponseDecoder<ErrorResponse>(ErrorResponse.class);

  public Response<ErrorResponse> loadErrorResponse(String filename) throws Exception
  {
    RestResponse restResponse = loadRestResponse(filename);
    return ERROR_DECODER.decodeResponse(restResponse);
  }

  public RestResponse loadRestResponse(String responseFilename) throws Exception
  {
    String responseString = FileUtils.readFileToString(new File(_rootDir + File.separator + responseFilename.replaceAll("/", File.separator)));
    return _serializer.readRestResponse(IOUtils.toInputStream(responseString));
  }
}
