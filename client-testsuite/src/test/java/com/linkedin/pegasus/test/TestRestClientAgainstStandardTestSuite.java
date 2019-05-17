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
import com.linkedin.data.schema.NamedDataSchema;
import com.linkedin.data.schema.SchemaParserFactory;
import com.linkedin.data.schema.resolver.FileDataSchemaResolver;
import com.linkedin.data.schema.validation.CoercionMode;
import com.linkedin.data.schema.validation.RequiredMode;
import com.linkedin.data.schema.validation.ValidateDataAgainstSchema;
import com.linkedin.data.schema.validation.ValidationOptions;
import com.linkedin.data.schema.validation.ValidationResult;
import com.linkedin.pegasus.testsuite.RequestResponseTestCases;
import com.linkedin.r2.message.rest.RestResponse;
import com.linkedin.restli.client.Request;
import com.linkedin.restli.client.Response;
import com.linkedin.restli.common.ErrorResponse;
import com.linkedin.restli.internal.server.util.DataMapUtils;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import manifest.JsonTest;
import manifest.SchemaTest;
import manifest.WireProtocolOperationTest;
import manifest.WireProtocolTest;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;


/**
 * This test suite is driven by the contents of the manifest.json file.  All the various
 * test data entries in manifest.json are processed and some basic tests run to verify the java rest.li client
 * correctly handles the test cases.
 */
public class TestRestClientAgainstStandardTestSuite extends StandardTestSuiteBase
{
  public TestRestClientAgainstStandardTestSuite()
      throws Exception
  {
    super(System.getProperty("test.projectDir"));
  }

  @DataProvider(name = "json-test-data")
  public Object[][] jsonTestData()
  {
    List<Object[]> results = new ArrayList<Object[]>();
    for(JsonTest test: _manifest.getJsonTestData())
    {
      results.add(new Object[] { test });
    }
    return results.toArray(new Object[results.size()][1]);
  }

  @Test(dataProvider = "json-test-data")
  public void testJson(JsonTest jsonTest) throws Exception
  {
    // run deserialization
    DataMap deserialized = loadJsonFile(jsonTest.getData());

    // run serialization
    byte[] serialized = DataMapUtils.mapToBytes(deserialized);

    // perform round trip verification
    Assert.assertEquals(deserialized, DataMapUtils.readMap(new ByteArrayInputStream(serialized)));
  }

  @DataProvider(name = "schema-test-data")
  public Object[][] schemaTestData()
  {
    List<Object[]> results = new ArrayList<Object[]>();
    for(SchemaTest test: _manifest.getSchemaTestData())
    {
      results.add(new Object[] { test });
    }
    return results.toArray(new Object[results.size()][1]);
  }

  @Test(dataProvider = "schema-test-data")
  public void testSchema(SchemaTest test) throws Exception
  {
    FileDataSchemaResolver resolver = new FileDataSchemaResolver(SchemaParserFactory.instance());
    resolver.setPaths(_rootDir + File.separator + "schemas");
    resolver.setExtension(".pdsc");

    // load the schema
    StringBuilder errors = new StringBuilder();
    NamedDataSchema dataSchema = resolver.findDataSchema(test.getSchema(), errors);
    Assert.assertNotNull(dataSchema, "could not find schema: " + test.getSchema());
    Assert.assertEquals(test.getSchema(), dataSchema.getFullName());

    // load the data
    DataMap dataMap = loadJsonFile(test.getData());

    // validate that the data conforms to the schema
    ValidationResult validationResult = ValidateDataAgainstSchema.validate(dataMap,
                                                                           dataSchema,
                                                                           new ValidationOptions(RequiredMode.FIXUP_ABSENT_WITH_DEFAULT,
                                                                                                 CoercionMode.NORMAL)
    );
    Assert.assertTrue(validationResult.isValid(), test.data() + " failed to validate against schema " + test.getSchema() + " : " + validationResult.getMessages());
    DataMap expectedAfterFixup = loadJsonFile(test.hasWithFixup() ? test.getWithFixup() : test.getData());
    compareDataMapsBySerializedRepresentation(dataMap, expectedAfterFixup);
  }

  @DataProvider(name = "wire-protocol-test-data-v1")
  public Object[][] wireProtocolTestDataV1() {
    return wireProtocolTestData(RequestResponseTestCases.V1_TEST_CASES);
  }

  @DataProvider(name = "wire-protocol-test-data-v2")
  public Object[][] wireProtocolTestDataV2() {
    return wireProtocolTestData(RequestResponseTestCases.V2_TEST_CASES);
  }

  private Object[][] wireProtocolTestData(RequestResponseTestCases testCases)
  {
    List<Object[]> results = new ArrayList<Object[]>();
    for (WireProtocolTest wireProtocolTest: _manifest.getWireProtocolTestData())
    {
      for (WireProtocolOperationTest wireProtocolOperationTest: wireProtocolTest.getOperations())
      {
        Request<?> request = testCases.getRequest(wireProtocolOperationTest.getName());
        results.add(new Object[]{wireProtocolOperationTest, request});
      }
    }
    return results.toArray(new Object[results.size()][2]);
  }

  @Test(dataProvider = "wire-protocol-test-data-v1")
  public void wireProtocolTestDataV1(WireProtocolOperationTest operationTest, Request<?> request) throws Exception
  {
    testWireProtocolTestData(operationTest, request, "");
  }

  @Test(dataProvider = "wire-protocol-test-data-v2")
  public void wireProtocolTestDataV2(WireProtocolOperationTest operationTest, Request<?> request) throws Exception
  {
    testWireProtocolTestData(operationTest, request, "-v2");
  }

  private void testWireProtocolTestData(WireProtocolOperationTest operationTest, Request<?> request, String versionExtension) throws Exception
  {
    String requestFilename = operationTest.getRequest().replaceFirst("requests", "requests" + versionExtension);
    String responseFilename = operationTest.getResponse().replaceFirst("responses", "responses" + versionExtension);
    compareRequests(request, requestFilename);

    Response<?> response;
    if (operationTest.getStatus().intValue() > 300 && operationTest.getStatus().intValue() != 404)
    {
      Response<ErrorResponse> error = _requestResponseLoader.loadErrorResponse(responseFilename);
      response = error;
      Assert.assertEquals(error.getEntity().getMessage(), operationTest.getErrorMessage());
    }
    else
    {
      Request<?> matchingRequest = RequestResponseTestCases.V1_TEST_CASES.getRequest(operationTest.getName());
      RestResponse success = _requestResponseLoader.loadRestResponse(responseFilename);
      response = matchingRequest.getResponseDecoder().decodeResponse(success);
    }
    Assert.assertEquals(response.getStatus(), operationTest.getStatus().intValue());
  }
}
