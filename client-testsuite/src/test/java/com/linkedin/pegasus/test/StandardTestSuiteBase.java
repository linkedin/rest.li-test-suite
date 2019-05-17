package com.linkedin.pegasus.test;


import com.linkedin.data.ByteString;
import com.linkedin.data.DataMap;
import com.linkedin.pegasus.testsuite.RequestResponseFileLoader;
import com.linkedin.r2.message.rest.RestRequest;
import com.linkedin.restli.client.Request;
import com.linkedin.restli.internal.server.util.DataMapUtils;
import java.io.File;
import java.io.FileInputStream;
import manifest.Manifest;
import org.testng.Assert;


/**
 * Provides data file loading and convenience comparison utils.
 *
 * @author jbetz@linkedin.com
 */
public abstract class StandardTestSuiteBase
{
  protected final String _rootDir;
  protected final Manifest _manifest;
  protected final RequestResponseFileLoader _requestResponseLoader;

  public StandardTestSuiteBase(String rootDir) throws Exception
  {
    _rootDir = rootDir;
    _manifest = loadManifest("manifest.json");
    _requestResponseLoader = new RequestResponseFileLoader(new File(rootDir));
  }

  protected DataMap loadJsonFile(String filepath) throws Exception
  {
    return DataMapUtils.readMap(new FileInputStream(_rootDir + File.separator + filepath.replaceAll("/", File.separator)));
  }

  protected void compareRequests(Request<?> request, String requestFilename) throws Exception
  {
    compareRestRequests(MockRestliClientRequestBuilder.buildRequest(request),
                        _requestResponseLoader.loadRestRequest(requestFilename));
  }

  protected void compareRestRequests(RestRequest restRequest, RestRequest expectedRequest)
  {
    Assert.assertEquals(restRequest.getHeaders(), expectedRequest.getHeaders());
    Assert.assertEquals(restRequest.getMethod(), expectedRequest.getMethod());
    Assert.assertEquals("/" + restRequest.getURI().toString(), expectedRequest.getURI().toString());
    compareByteStringsAsDataMaps(restRequest.getEntity(), expectedRequest.getEntity());
  }

  protected void compareDataMapsBySerializedRepresentation(DataMap actual, DataMap expected)
  {
    compareByteStringsAsDataMaps(ByteString.copy(DataMapUtils.mapToBytes(actual)),
                                 ByteString.copy(DataMapUtils.mapToBytes(expected)));
  }

  protected void compareByteStringsAsDataMaps(ByteString actual, ByteString expected)
  {
    if(expected == null)
    {
      Assert.assertNull(actual);
    }
    else if(expected.length() == 0)
    {
      Assert.assertEquals(actual.length(), 0);
    }
    else
    {
      Assert.assertEquals(DataMapUtils.readMap(actual.asInputStream()),
                          DataMapUtils.readMap(expected.asInputStream()));
    }
  }

  private Manifest loadManifest(String filepath) throws Exception
  {
    return DataMapUtils.read(new FileInputStream(_rootDir + File.separator + filepath), Manifest.class);
  }
}
