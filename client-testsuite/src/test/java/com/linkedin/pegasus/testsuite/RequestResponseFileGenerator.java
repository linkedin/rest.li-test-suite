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


import com.linkedin.common.callback.Callbacks;
import com.linkedin.common.util.None;
import com.linkedin.r2.caprep.CaptureFilter;
import com.linkedin.r2.caprep.db.FixedFileDbSink;
import com.linkedin.r2.filter.FilterChains;
import com.linkedin.r2.transport.common.Client;
import com.linkedin.r2.transport.common.bridge.client.TransportClientAdapter;
import com.linkedin.r2.transport.http.client.HttpClientFactory;
import com.linkedin.restli.client.ErrorHandlingBehavior;
import com.linkedin.restli.client.Request;
import com.linkedin.restli.client.RestClient;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;


/**
 * Generates request and response files for the test cases in RequestResponseTestCases.
 *
 * @author jbetz@linkedin.com
 */
public class RequestResponseFileGenerator
{
  private final File _requestDir;
  private final File _responseDir;

  public static void main(String[] args) throws Exception
  {
    {
      RequestResponseFileGenerator generator = new RequestResponseFileGenerator("");
      for (Map.Entry<String, Request<?>> entry : RequestResponseTestCases.V1_TEST_CASES.getTestCases().entrySet())
      {
        System.err.println("generating: " + entry.getKey());
        generator.recordRequestResponse(entry.getKey(), entry.getValue());
      }
    }

    {
      RequestResponseFileGenerator generator = new RequestResponseFileGenerator("-v2");
      for (Map.Entry<String, Request<?>> entry : RequestResponseTestCases.V2_TEST_CASES.getTestCases().entrySet())
      {
        System.err.println("generating: " + entry.getKey());
        generator.recordRequestResponse(entry.getKey(), entry.getValue());
      }
    }
  }

  public RequestResponseFileGenerator(String version) throws Exception
  {
    String rootDir = System.getProperty("test.projectDir");
    _requestDir = new File(rootDir + File.separator + "requests" + version);
    _responseDir = new File(rootDir + File.separator + "responses" + version);
    if (!_requestDir.exists() & !_requestDir.mkdirs())
    {
      throw new IOException("Could not create directory: " + _requestDir);
    }
    if (!_responseDir.exists() & !_responseDir.mkdirs())
    {
      throw new IOException("Could not create directory: " + _responseDir);
    }
  }

  public void recordRequestResponse(final String name, Request<?> request) throws Exception
  {
    CaptureFilter captureFilter = new CaptureFilter(
        new FixedFileDbSink(new File(_requestDir + File.separator + name + ".req"),
           new File(_responseDir + File.separator + name + ".res"),
           new TestSuiteMessageSerializer()));

    final HttpClientFactory http = new HttpClientFactory(FilterChains.createRestChain(captureFilter));
    final Client r2Client = new TransportClientAdapter(http.getClient(Collections.<String, String>emptyMap()));

    // Create a RestClient to talk to localhost:8080
    RestClient restClient = new RestClient(r2Client, "http://localhost:8080/");

    restClient.sendRequest(request, ErrorHandlingBehavior.TREAT_SERVER_ERROR_AS_SUCCESS).getResponse();
    System.err.println("recorded: " + name);
    restClient.shutdown(Callbacks.<None>empty());
    r2Client.shutdown(Callbacks.<None>empty());
    http.shutdown(Callbacks.<None>empty());
  }
}
