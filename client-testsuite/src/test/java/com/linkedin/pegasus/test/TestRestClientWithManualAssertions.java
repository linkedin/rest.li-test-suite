package com.linkedin.pegasus.test;

import com.linkedin.data.template.StringArray;
import com.linkedin.data.template.StringMap;
import com.linkedin.pegasus.testsuite.RequestResponseTestCases;
import com.linkedin.r2.message.rest.RestResponse;
import com.linkedin.restli.client.Request;
import com.linkedin.restli.client.Response;
import com.linkedin.restli.client.response.BatchKVResponse;
import com.linkedin.restli.common.BatchResponse;
import com.linkedin.restli.common.CollectionResponse;
import com.linkedin.restli.common.ComplexResourceKey;
import com.linkedin.restli.common.EmptyRecord;
import com.linkedin.restli.common.ErrorResponse;
import com.linkedin.restli.common.IdResponse;
import com.linkedin.restli.common.LinkArray;
import com.linkedin.restli.common.UpdateStatus;
import java.io.File;
import java.lang.reflect.Method;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import testsuite.Fruits;
import testsuite.LargeRecord;
import testsuite.Message;
import testsuite.MessageArray;
import testsuite.UnionOfComplexTypes;
import testsuite.UnionOfPrimitives;
import testsuite.complexkey.ComplexKey;
import testsuite.complexkey.KeyParams;


/**
 * This test suite supplementary to the manifest driven tests run by TestRestClientAgainstStandardTestSuite.
 *
 * This suite uses manual assertions to check correctness of aspects of the java rest.li client that
 * cannot be validated using the manifest driven assertions in TestRestClientAgainstStandardTestSuite.
 *
 * TODO:  this suite is not sufficiently comprehensive.  It should be expanded to give language implementers a
 * template for the manual assertions they should include.
 *
 * @author jbetz@linkedin.com
 * @author mnchen@linkedin.com
 */
public class TestRestClientWithManualAssertions extends StandardTestSuiteBase
{
  private final static String EXCEPTION_CLASS = "com.linkedin.restli.server.RestLiServiceException";

  public TestRestClientWithManualAssertions() throws Exception
  {
    super(System.getProperty("test.projectDir"));
  }

  @DataProvider(name = "dp")
  public Object[][] responseNames(Method m) throws Exception{
    Object[][] names =  new Object[2][1];
    names[0][0] = "v1";
    names[1][0] = "v2";
    return names;
  }

  @Test(dataProvider = "dp")
  public void testCollectionReturnEntityCreate(String version) throws Exception
  {
    Response<Message> response = loadResponse(version, "collectionReturnEntity-create");
    Assert.assertEquals(response.getId(), "1");
    Assert.assertNotNull(response.getEntity());
  }

  @Test(dataProvider = "dp")
  public void testCollectionCreate(String version) throws Exception
  {
    Response<EmptyRecord> response = loadResponse(version, "collection-create");
    Assert.assertEquals(response.getId(), "1");
  }

  @Test(dataProvider = "dp")
  public void testCollectionGet(String version) throws Exception
  {
    Response<Message> response = loadResponse(version,"collection-get");
    Assert.assertEquals(response.getEntity().getId().longValue(), 1l);
    Assert.assertEquals(response.getEntity().getMessage(), "test message");
  }

  @Test(dataProvider = "dp")
  public void testCollectionUpdate(String version) throws Exception
  {
    Response<Message> response = loadResponse(version, "collection-update");
    Assert.assertNull(response.getEntity());
  }

  @Test(dataProvider = "dp")
  public void testCollectionPartialUpdate(String version) throws Exception
  {
    Response<Message> response = loadResponse(version, "collection-partial-update");
    Assert.assertNull(response.getEntity());
  }

  @Test(dataProvider = "dp")
  public void testCollectionDelete(String version) throws Exception
  {
    Response<Message> response = loadResponse(version, "collection-delete");
    Assert.assertNull(response.getEntity());
  }

  @Test(dataProvider = "dp")
  public void testCollectionBatchGet(String version) throws Exception
  {
    Response<BatchResponse> response = loadResponse(version,"collection-batch-get");
    Assert.assertNotNull(response.getEntity());
    Assert.assertTrue(response.getEntity().getStatuses().isEmpty());
    Assert.assertTrue(response.getEntity().getErrors().isEmpty());

    Message message1 = (Message) response.getEntity().getResults().get("1");
    Assert.assertEquals(message1.getMessage(), "test message");
    Assert.assertEquals(message1.getId().longValue(), 1l);

    Message message2 = (Message) response.getEntity().getResults().get("3");
    Assert.assertEquals(message2.getMessage(), "another message");
    Assert.assertEquals(message2.getId().longValue(), 2l);
  }

  @Test(dataProvider = "dp")
  public void testCollectionBatchUpdate(String version) throws Exception
  {
    Response<BatchKVResponse> response = loadResponse(version,"collection-batch-update");
    Assert.assertNotNull(response.getEntity());
    Assert.assertTrue(response.getEntity().getErrors().isEmpty());
    UpdateStatus entity_id1 = (UpdateStatus) response.getEntity().getResults().get(1l);
    UpdateStatus entity_id3 = (UpdateStatus) response.getEntity().getResults().get(3l);
    Assert.assertEquals(entity_id1.getStatus().intValue(), 204);
    Assert.assertEquals(entity_id3.getStatus().intValue(), 201);
  }

  @Test(dataProvider = "dp")
  public void testCollectionBatchPartialUpdate(String version) throws Exception
  {
    Response<BatchKVResponse> response = loadResponse(version, "collection-batch-partial-update");
    Assert.assertNotNull(response.getEntity());
    Assert.assertTrue(response.getEntity().getErrors().isEmpty());
    UpdateStatus entity_id1 = (UpdateStatus) response.getEntity().getResults().get(1l);
    UpdateStatus entity_id3 = (UpdateStatus) response.getEntity().getResults().get(3l);
    Assert.assertEquals(entity_id1.getStatus().intValue(), 204);
    Assert.assertEquals(entity_id3.getStatus().intValue(), 204);
  }

  @Test(dataProvider = "dp")
  public void testCollectionBatchDelete(String version) throws Exception
  {
    Response<BatchKVResponse> response = loadResponse(version, "collection-batch-delete");
    Assert.assertNotNull(response.getEntity());
    Assert.assertTrue(response.getEntity().getErrors().isEmpty()); // deprecated in rest.li protocol 2
    UpdateStatus entity_id1 = (UpdateStatus) response.getEntity().getResults().get(1l);
    UpdateStatus entity_id3 = (UpdateStatus) response.getEntity().getResults().get(3l);
    Assert.assertEquals(entity_id1.getStatus().intValue(), 204);
    Assert.assertEquals(entity_id3.getStatus().intValue(), 404);
  }

  @Test(dataProvider = "dp")
  public void testCollectionGetProjection(String version) throws Exception
  {
    Response<Message> response = loadResponse(version, "collection-get-projection");
    Assert.assertEquals(response.getEntity().getId().longValue(), 1);
  }

  @Test(dataProvider = "dp")
  public void testCollectionGet404(String version) throws Exception
  {
    Response<ErrorResponse> response = loadError(version, "collection-get-404");
    if (version.equals("v1"))
    {
      Assert.assertEquals(response.getHeader("X-LinkedIn-Error-Response"), "true");
    }
    else if (version.equals("v2"))
    {
      Assert.assertEquals(response.getHeader("X-RestLi-Error-Response"), "true");
    }
    Assert.assertEquals(response.getEntity().getMessage(), "Requested entity not found: type=get, resourceName=collection");
    Assert.assertEquals(response.getStatus(), 404);
  }

  @Test(dataProvider = "dp")
  public void testCollectionUpdate400(String version) throws Exception {
    Response<ErrorResponse> response = loadError(version, "collection-update-400");
    if (version.equals("v1"))
    {
      Assert.assertEquals(response.getHeader("X-LinkedIn-Error-Response"), "true");
    }
    else if (version.equals("v2"))
    {
      Assert.assertEquals(response.getHeader("X-RestLi-Error-Response"), "true");
    }
    Assert.assertNotNull(response.getEntity());
    Assert.assertEquals(response.getEntity().getMessage(), "Missing required field: message");
    Assert.assertEquals(response.getStatus(), 400);
  }

  @Test(dataProvider = "dp")
  public void testCollectionCreate500(String version) throws Exception {
    Response<ErrorResponse> response = loadError(version, "collection-create-500");
    if (version.equals("v1"))
    {
      Assert.assertEquals(response.getHeader("X-LinkedIn-Error-Response"), "true");
    }
    else if (version.equals("v2"))
    {
      Assert.assertEquals(response.getHeader("X-RestLi-Error-Response"), "true");
    }
    Assert.assertEquals(response.getStatus(), 500);
  }

  @Test(dataProvider = "dp")
  public void testCollectionCreateErrorDetails(String version) throws Exception {
    Response<ErrorResponse> response = loadError(version, "collection-create-error-details");
    if (version.equals("v1"))
    {
      Assert.assertEquals(response.getHeader("X-LinkedIn-Error-Response"), "true");
    }
    else if (version.equals("v2"))
    {
      Assert.assertEquals(response.getHeader("X-RestLi-Error-Response"), "true");
    }
    Assert.assertEquals(response.getEntity().getMessage(), "error details message");
    Assert.assertEquals(response.getStatus(), 400);
    Assert.assertEquals(response.getEntity().getErrorDetails().toString(), "{one=1, two=2}");
  }

  @Test(dataProvider = "dp")
  public void testCollectionBatchUpdateErrors(String version) throws Exception {
    Response<BatchKVResponse> response = loadResponse(version, "collection-batch-update-errors");
    UpdateStatus entity_id1 = (UpdateStatus) response.getEntity().getResults().get(1l);
    ErrorResponse entity_id4 = (ErrorResponse) response.getEntity().getErrors().get(4l);
    ErrorResponse entity_id2 = (ErrorResponse) response.getEntity().getErrors().get(2l);

    Assert.assertEquals(entity_id1.getStatus().intValue(), 204);
    Assert.assertEquals(entity_id4.getStatus().intValue(), 500);
    Assert.assertEquals(entity_id2.getStatus().intValue(), 400);
    Assert.assertEquals(entity_id4.getExceptionClass(), EXCEPTION_CLASS);
    Assert.assertEquals(entity_id4.getExceptionClass(), EXCEPTION_CLASS);
    Assert.assertEquals(entity_id2.getMessage(), "Missing required field: message");

  }
  @Test(dataProvider = "dp")
  public void testSubCollectionOfCollectionGet(String version) throws Exception {
    Response<Message> response = loadResponse(version,"sub-collection-of-collection-get");
    Assert.assertEquals(response.getEntity().getId().longValue(), 100l);
    Assert.assertEquals(response.getEntity().getMessage(), "sub collection message");
    }

  @Test(dataProvider = "dp")
  public void testSubSimpleOfCollectionGet(String version) throws Exception {
    Response<Message> response = loadResponse(version, "sub-simple-of-collection-get");
    Assert.assertEquals(response.getEntity().getMessage(), "sub simple message");
  }

  @Test(dataProvider = "dp")
  public void testCollectionSearchFinder(String version) throws Exception {
    Response<CollectionResponse> response = loadResponse(version, "collection-search-finder");
    Message message1 = (Message)response.getEntity().getElements().get(0);
    Assert.assertEquals(message1.getMessage(),"test message");
    Assert.assertEquals(message1.getId().longValue(),1l);
    Message message2 = (Message)response.getEntity().getElements().get(1);
    Assert.assertEquals(message2.getMessage(),"another message");
    Assert.assertEquals(message2.getId().longValue(),2l);
    Assert.assertEquals(response.getEntity().getMetadataRaw().get("optionalLong").toString(), "5");
    Assert.assertEquals(response.getEntity().getMetadataRaw().get("optionalString").toString(), "metadata");
    Assert.assertEquals(response.getEntity().getPaging().getTotal().intValue(), 2);
    Assert.assertEquals(response.getEntity().getPaging().getCount().intValue(), 10);
    Assert.assertEquals(response.getEntity().getPaging().getLinks(), new LinkArray());
  }

  @Test(dataProvider = "dp")
  public void testActionEcho(String version) throws Exception
  {
    Response<String> echoResponse = loadResponse(version,"actionset-echo");
    Assert.assertEquals(echoResponse.getStatus(), 200);
    Assert.assertEquals(echoResponse.getEntity(), "Is anybody out there?");
  }


  @Test(dataProvider = "dp")
  public void testActionReturnBool(String version) throws Exception
  {
    Response<Message> response = loadResponse(version, "actionset-return-bool");
    Assert.assertEquals(response.getEntity(), true);
  }

  @Test(dataProvider = "dp")
  public void testActionEchoMessage(String version) throws Exception
  {
    Response<Message> response = loadResponse(version, "actionset-echo-message");
    Assert.assertEquals(response.getEntity().getMessage(), "test message");
  }

  @Test(dataProvider = "dp")
  public void testActionEchoMessageArray(String version) throws Exception
  {
    Response<MessageArray> response = loadResponse(version,"actionset-echo-message-array");
    Assert.assertEquals(response.getEntity().get(0).getMessage(), "test message");
    Assert.assertEquals(response.getEntity().get(1).getMessage(), "another message");
  }

  @Test(dataProvider = "dp")
  public void testActionEchoStringArray(String version) throws Exception
  {
    Response<StringArray> response = loadResponse(version, "actionset-echo-string-array");
    Assert.assertEquals(response.getEntity().get(0), "string one");
    Assert.assertEquals(response.getEntity().get(1), "string two");
  }

  @Test(dataProvider = "dp")
  public void testActionEchoStringMap(String version) throws Exception
  {
    Response<StringMap> response = loadResponse(version, "actionset-echo-string-map");
    Assert.assertEquals(response.getEntity().get("one"), "string one");
    Assert.assertEquals(response.getEntity().get("two"), "string two");
  }

  @Test(dataProvider = "dp")
  public void testActionEchoTyperefUrl(String version) throws Exception
  {
    Response<StringMap> response = loadResponse(version,"actionset-echo-typeref-url");
    Assert.assertEquals(response.getEntity(), "http://rest.li");
  }

  @Test(dataProvider = "dp")
  public void testActionEchoPrimitiveUnion(String version) throws Exception
  {
    Response<UnionOfPrimitives> response = loadResponse(version,"actionset-echo-primitive-union");
    Assert.assertEquals(response.getEntity().getPrimitivesUnion().getLong().longValue(), 100l);
  }

  @Test(dataProvider = "dp")
  public void testActionEchoComplexTypesUnion(String version) throws Exception
  {
    Response<UnionOfComplexTypes> response = loadResponse(version,"actionset-echo-complex-types-union");
    Assert.assertEquals(response.getEntity().getComplexTypeUnion().getFruits(), Fruits.APPLE);
  }

  @Test(dataProvider = "dp")
  public void testActionEmptyResponse(String version) throws Exception
  {
    Response<Message> response = loadResponse(version,"actionset-empty-response");
    Assert.assertNull(response.getEntity());
  }

  @Test(dataProvider = "dp")
  public void testActionMultipleInputs(String version) throws Exception
  {
    Response<Boolean> response = loadResponse(version,"actionset-multiple-inputs");
    Assert.assertTrue(response.getEntity());
  }

  @Test(dataProvider = "dp")
  public void testActionMultipleInputsNoOptional(String version) throws Exception
  {
    Response<Boolean> response = loadResponse(version,"actionset-multiple-inputs-no-optional");
    Assert.assertTrue(response.getEntity());
  }

  @Test(dataProvider = "dp")
  public void testKeywithunionGet(String version) throws Exception
  {
    Response<LargeRecord> response = loadResponse(version,"keywithunion-get");
    Assert.assertEquals(response.getEntity().getMessage().getMessage(), "test message");
    Assert.assertEquals(response.getEntity().getKey().getPart1(), "one");
    Assert.assertEquals(response.getEntity().getKey().getPart2().longValue(), 2l);
    Assert.assertEquals(response.getEntity().getKey().getPart3(), Fruits.APPLE);
  }

  @Test(dataProvider = "dp")
  public void testParamsGetWithQueryParams(String version) throws Exception {
    Response<Message> response = loadResponse(version,"params-get-with-queryparams");
    Assert.assertEquals(response.getEntity().getMessage(), "test message");
    Assert.assertEquals(response.getEntity().getId().longValue(), 1l);
  }

  @Test(dataProvider = "dp")
  public void testComplexKeyGet(String version) throws Exception
  {
    Response<LargeRecord> response = loadResponse(version,"complexkey-get");
    Assert.assertEquals(response.getEntity().getKey().getPart1(), "one");
    Assert.assertEquals(response.getEntity().getKey().getPart2(), Long.valueOf(2l));
    Assert.assertEquals(response.getEntity().getKey().getPart3(), Fruits.APPLE);
    Assert.assertEquals(response.getEntity().getMessage().getMessage(), "test message");
  }

  @Test(dataProvider = "dp")
  public void testComplexKeyCreate(String version) throws Exception
  {
    Response<IdResponse<ComplexResourceKey<ComplexKey, KeyParams>>> response =
        loadResponse(version, "complexkey-create");
    ComplexKey key = response.getEntity().getId().getKey();
    Assert.assertEquals(key.getPart1(), "one");
    Assert.assertEquals(key.getPart2(), Long.valueOf(2l));
    Assert.assertEquals(key.getPart3(), Fruits.APPLE);
  }

  @Test(dataProvider = "dp")
  public void testAssociationGet(String version) throws Exception
  {
    Response<LargeRecord> messageResponse = loadResponse(version,"association-get");
    Assert.assertEquals(messageResponse.getEntity().getKey().getPart1(), "two");
    Assert.assertEquals(messageResponse.getEntity().getKey().getPart2(), Long.valueOf(3l));
    Assert.assertEquals(messageResponse.getEntity().getKey().getPart3(), Fruits.ORANGE);
    Assert.assertEquals(messageResponse.getEntity().getMessage().getMessage(), "test message");
  }

  @Test(dataProvider = "dp")
  public void testSimpleGet(String version) throws Exception
  {
    Response<Message> messageResponse = loadResponse(version,"simple-get");
    Assert.assertEquals(messageResponse.getEntity().getMessage(), "test message");
  }


  @SuppressWarnings("unchecked")
  public <T> Response<T> loadResponse(String version, String name) throws Exception
  {
    Request<T> request;
    String responseFilename;
    if (version.equals("v1")) {
      request = (Request<T>) RequestResponseTestCases.V1_TEST_CASES.getRequest(name);
      responseFilename = "responses" + File.separator + name + ".res";
    } else {
      request = (Request<T>) RequestResponseTestCases.V2_TEST_CASES.getRequest(name);
      responseFilename = "responses-v2" + File.separator + name + ".res";
    }
    RestResponse restResponse = _requestResponseLoader.loadRestResponse(responseFilename);
    return request.getResponseDecoder().decodeResponse(restResponse);
  }

  public Response<ErrorResponse> loadError(String version, String name) throws Exception
  {
    String responseFilename;
    if (version.equals("v1")) {
      responseFilename = "responses" + File.separator + name + ".res";
    } else {
      responseFilename = "responses-v2" + File.separator + name + ".res";
    }
    return _requestResponseLoader.loadErrorResponse(responseFilename);
  }
}
