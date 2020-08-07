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


import com.linkedin.data.ByteString;
import com.linkedin.data.template.StringArray;
import com.linkedin.data.template.StringMap;
import com.linkedin.restli.client.ProtocolVersionOption;
import com.linkedin.restli.client.Request;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.client.RestliRequestOptionsBuilder;
import com.linkedin.restli.client.util.PatchGenerator;
import com.linkedin.restli.common.ComplexResourceKey;
import com.linkedin.restli.common.CompoundKey;
import com.linkedin.restli.common.PatchRequest;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import testsuite.ActionSetBuilders;
import testsuite.ActionSetRequestBuilders;
import testsuite.AssociationBuilders;
import testsuite.AssociationRequestBuilders;
import testsuite.CollectionBuilders;
import testsuite.CollectionRequestBuilders;
import testsuite.CollectionReturnEntityBuilders;
import testsuite.CollectionReturnEntityRequestBuilders;
import testsuite.ComplexkeyBuilders;
import testsuite.ComplexkeyRequestBuilders;
import testsuite.Fruits;
import testsuite.LargeRecord;
import testsuite.Message;
import testsuite.MessageArray;
import testsuite.ParamsBuilders;
import testsuite.ParamsRequestBuilders;
import testsuite.Primitives;
import testsuite.SimpleBuilders;
import testsuite.SimpleRequestBuilders;
import testsuite.UnionOfComplexTypes;
import testsuite.UnionOfPrimitives;
import testsuite.collectionsubresources.SubcollectionBuilders;
import testsuite.collectionsubresources.SubcollectionRequestBuilders;
import testsuite.collectionsubresources.SubsimpleBuilders;
import testsuite.collectionsubresources.SubsimpleRequestBuilders;
import testsuite.complexkey.ComplexKey;
import testsuite.complexkey.KeyParams;
import testsuite.complexkey.KeyWithUnion;
import testsuite.keywithunion.KeywithunionBuilders;
import testsuite.keywithunion.KeywithunionRequestBuilders;
import testsuite.typerefs.AssociationTyperefBuilders;
import testsuite.typerefs.AssociationTyperefRequestBuilders;
import testsuite.typerefs.CollectionTyperefBuilders;
import testsuite.typerefs.CollectionTyperefRequestBuilders;


/**
 * This is a map of named rest.li java client "Requests" that have been fully constructed using generated request builders.
 *
 * The key of each map entry is a name that is expected to match the name of a operations entry in manifest.json.
 *
 * The value of each map entry is a request that is both used to generate the .req and .req files (when RequestResponseFileGenerator is run).
 * It is also used by the testNG test suites (e.g. TestRestClientAgainstStandardTestSuite).
 *
 * @author jbetz@linkedin.com
 * @author mnchen@linkedin.com
 */
public class RequestResponseTestCases
{
  private static final RestliRequestOptions V1 = RestliRequestOptions.FORCE_USE_PREV_OPTION;
  private static final RestliRequestOptions V2 = RestliRequestOptions.DEFAULT_OPTIONS;

  public static final RequestResponseTestCases V1_TEST_CASES = new RequestResponseTestCases(V1);
  public static final RequestResponseTestCases V2_TEST_CASES = new RequestResponseTestCases(V2);

  private final RestliRequestOptions _options;
  private final Map<String, Request<?>> _requestsByNickname;

  public RequestResponseTestCases(RestliRequestOptions options)
  {
    _options = options;
    _requestsByNickname = buildRequests();
  }

  public Request<?> getRequest(String nickname)
  {
    return _requestsByNickname.get(nickname);
  }

  public Map<String, Request<?>> getTestCases()
  {
    return Collections.unmodifiableMap(_requestsByNickname);
  }

  @SuppressWarnings("unchecked")
  private Map<String, Request<?>> buildRequests()
  {
    Map<String, Request<?>> builtRequests = new HashMap<String, Request<?>>();

    Message testMessage = new Message().setMessage("test message");
    builtRequests.put("collectionReturnEntity-create", new CollectionReturnEntityRequestBuilders(_options).create().input(testMessage).build());

    builtRequests.put("collection-create", new CollectionRequestBuilders(_options).create().input(testMessage).build());
    Message anotherMessage = new Message().setMessage("another message");
    builtRequests.put("collection-batch-create",
                      new CollectionRequestBuilders(_options).batchCreate().input(testMessage).input(anotherMessage).build()
    );
    builtRequests.put("collection-get", new CollectionBuilders(_options).get().id(1l).build());
    builtRequests.put("collection-batch-get", new CollectionBuilders(_options).batchGet().ids(1l, 3l).build());
    builtRequests.put("collection-update",
                      new CollectionRequestBuilders(_options).update().input(new Message().setMessage("updated message").setId(
                          1l)).id(1l).build()
    );
    builtRequests.put("collection-partial-update",
                      new CollectionRequestBuilders(_options).partialUpdate().input(new PatchRequest<Message>(PatchGenerator.diffEmpty(
                                                                                new Message().setMessage(
                                                                                    "partial updated message")).data())
                      ).id(1l).build()
    );
    builtRequests.put("collection-batch-update",
                      new CollectionRequestBuilders(_options).batchUpdate().input(1l,
                                                                          new Message().setMessage("updated message").setId(
                                                                              1l)).input(3l,
                                                                                         new Message().setMessage(
                                                                                             "inserted message").setId(
                                                                                             3l)).build()
    );
    builtRequests.put("collection-batch-partial-update",
                      new CollectionRequestBuilders(_options).batchPartialUpdate().input(1l,
                                                                                 new PatchRequest<Message>(
                                                                                     PatchGenerator.diffEmpty(new Message().setMessage(
                                                                                         "partial updated message")).data())
                      ).input(3l,
                              new PatchRequest<Message>(PatchGenerator.diffEmpty(new Message().setMessage(
                                  "another partial message")).data())
                      ).build()
    );
    builtRequests.put("collection-delete", new CollectionRequestBuilders(_options).delete().id(1l).build());
    builtRequests.put("collection-batch-delete", new CollectionRequestBuilders(_options).batchDelete().ids(1l, 3l).build());
    builtRequests.put("collection-get-404", new CollectionRequestBuilders(_options).get().id(2l).build());
    builtRequests.put("collection-update-400",
                      new CollectionRequestBuilders(_options).update().input(new Message().setId(1l)).id(1l).build());
    builtRequests.put("collection-create-500",
                      new CollectionRequestBuilders(_options).create().input(new Message().setMessage("internal error test").setId(
                          3l)).build());

    builtRequests.put("collection-create-error-details",
                      new CollectionRequestBuilders(_options).create().input(new Message().setMessage("error details test").setId(
                          3l)).build());

    builtRequests.put("collection-batch-update-errors",
                      new CollectionRequestBuilders(_options).batchUpdate().input(1l,
                                                                          new Message().setMessage("updated message").setId(
                                                                              1l)).input(2l,
                                                                                         new Message().setId(3l)).input(
                          4l,
                          new Message().setMessage("inserted message").setId(3l)).build()
    );

    builtRequests.put("collection-get-projection",
                      new CollectionRequestBuilders(_options).get().id(1l).fields(Message.fields().id()).build());

    builtRequests.put("collection-search-finder",
                      new CollectionRequestBuilders(_options).findBySearch().keywordParam("message").build());

    builtRequests.put("collection-search-finder-empty-string",
        new CollectionRequestBuilders(_options).findBySearch().keywordParam("").build());

    builtRequests.put("collection-complex-finder",
        new CollectionRequestBuilders(_options).findByComplex()
            .complexParam(new Primitives().setPrimitiveString("test message"))
            .build());

    builtRequests.put("collection-complex-finder-with-optional",
        new CollectionRequestBuilders(_options).findByComplex()
            .complexParam(new Primitives().setPrimitiveString("test message"))
            .optionalParam(ByteString.copy(new byte[]{1, 2, 3}))
            .build());

    builtRequests.put("collection-complex-finder-with-empty-bytes",
        new CollectionRequestBuilders(_options).findByComplex()
            .complexParam(new Primitives().setPrimitiveString("test message"))
            .optionalParam(ByteString.copy(new byte[0]))
            .build());

    builtRequests.put("collection-map-finder",
        new CollectionRequestBuilders(_options).findByMap()
            .paramsParam(new StringMap(Collections.singletonMap("key", "value")))
            .build());

    builtRequests.put("collection-map-finder-empty-map",
        new CollectionRequestBuilders(_options).findByMap()
            .paramsParam(new StringMap())
            .build());

    builtRequests.put("collection-map-finder-empty-entry",
        new CollectionRequestBuilders(_options).findByMap()
            .paramsParam(new StringMap(Collections.singletonMap("", "")))
            .build());

    builtRequests.put("collection-array-finder",
        new CollectionRequestBuilders(_options).findByArray()
            .keywordsParam(new StringArray(Collections.singletonList("another")))
            .build());

    builtRequests.put("collection-array-finder-multiple-values",
        new CollectionRequestBuilders(_options).findByArray()
            .keywordsParam(new StringArray(Arrays.asList("another", "test")))
            .build());

    builtRequests.put("collection-array-finder-empty-string",
        new CollectionRequestBuilders(_options).findByArray()
            .keywordsParam(new StringArray(Collections.singletonList("")))
            .build());

    ComplexKey complexKey = new ComplexKey().setPart1("one").setPart2(2l).setPart3(Fruits.APPLE);
    KeyParams keyParams = new KeyParams().setParam1("param1").setParam2(5l);
    ComplexResourceKey<ComplexKey, KeyParams> resourceKey = new ComplexResourceKey<ComplexKey, KeyParams>(complexKey,
                                                                                                          keyParams);

    ComplexKey complexKey2 = new ComplexKey().setPart1("two").setPart2(7l).setPart3(Fruits.ORANGE);
    KeyParams keyParams2 = new KeyParams().setParam1("param2").setParam2(11l);
    ComplexResourceKey<ComplexKey, KeyParams> resourceKey2 = new ComplexResourceKey<ComplexKey, KeyParams>(complexKey2,
                                                                                                           keyParams2);
    LargeRecord record = new LargeRecord().setKey(complexKey).setMessage(testMessage);
    LargeRecord record2 = new LargeRecord().setKey(complexKey2).setMessage(anotherMessage);
    LargeRecord updatedRecord = new LargeRecord().setKey(complexKey).setMessage(new Message().setMessage(
        "updated message"));
    LargeRecord updatedRecord2 = new LargeRecord().setKey(complexKey).setMessage(new Message().setMessage(
        "another updated message"));

    builtRequests.put("complexkey-create", new ComplexkeyRequestBuilders(_options).create().input(record).build());
    builtRequests.put("complexkey-batch-create",
                      new ComplexkeyRequestBuilders(_options).batchCreate().input(record).input(record2).build());
    builtRequests.put("complexkey-get", new ComplexkeyRequestBuilders(_options).get().id(resourceKey).build());
    builtRequests.put("complexkey-get-projection",
                      new ComplexkeyRequestBuilders(_options).get().id(resourceKey).fields(LargeRecord.fields().message().id()).build());
    builtRequests.put("complexkey-batch-get",
                      new ComplexkeyRequestBuilders(_options).batchGet().ids(resourceKey, resourceKey2).build());
    builtRequests.put("complexkey-update",
                      new ComplexkeyRequestBuilders(_options).update().input(updatedRecord).id(resourceKey).build());
    builtRequests.put("complexkey-batch-update",
                      new ComplexkeyRequestBuilders(_options).batchUpdate().input(resourceKey,
                                                                          updatedRecord).input(resourceKey2,
                                                                                               updatedRecord2).build()
    );
    builtRequests.put("complexkey-delete", new ComplexkeyBuilders(_options).delete().id(resourceKey).build());
    builtRequests.put("complexkey-batch-delete",
                      new ComplexkeyRequestBuilders(_options).batchDelete().ids(resourceKey, resourceKey2).build());

    LargeRecord placeholder = new LargeRecord().setKey(new ComplexKey().setPart1("placeholder"));
    LargeRecord replacement = new LargeRecord().setKey(new ComplexKey().setPart1("newpart1"));
    builtRequests.put("complexkey-partial-update",
                      new ComplexkeyRequestBuilders(_options).partialUpdate().input(PatchGenerator.diff(placeholder,
                                                                                                replacement)).id(
                          resourceKey).build());

    String urlSpecialChars = "!*'();:@&=+$,/?#[]"; // http://en.wikipedia.org/wiki/Percent-encoding
    String restliSpecialChars = ".~"; // [] are already in handled by urlSpecialChars
    String specialChars = urlSpecialChars + restliSpecialChars;
    ComplexKey complexKeyWithSpecialChars = new ComplexKey().setPart1("key" + specialChars).setPart2(2l).setPart3(Fruits.APPLE);
    KeyParams keyParamsWithSpecialChars = new KeyParams().setParam1("param" + specialChars).setParam2(5l);
    ComplexResourceKey<ComplexKey, KeyParams> resourceKeyWithSpecialChars = new ComplexResourceKey<ComplexKey, KeyParams>(
        complexKeyWithSpecialChars,
        keyParamsWithSpecialChars);

    builtRequests.put("complexkey-get-with-special-chars",
                      new ComplexkeyRequestBuilders(_options).get().id(resourceKeyWithSpecialChars).build());
    builtRequests.put("complexkey-batch-get-with-special-chars",
                      new ComplexkeyRequestBuilders(_options).batchGet().ids(resourceKeyWithSpecialChars,
                                                                     resourceKey2).build());

    CompoundKey associationKey = new CompoundKey();
    associationKey.append("part1", "two");
    associationKey.append("part2", 3l);
    associationKey.append("part3",
                          Fruits.ORANGE.toString()); // TODO: appears to be a bug in batchUpdate restricting enums from being association keys

    CompoundKey associationKey2 = new CompoundKey();
    associationKey2.append("part1", "five");
    associationKey2.append("part2", 9l);
    associationKey2.append("part3", Fruits.APPLE.toString());

    ComplexKey associationRecordKey = new ComplexKey().setPart1("two").setPart2(3l).setPart3(Fruits.ORANGE);
    ComplexKey associationRecordKey2 = new ComplexKey().setPart1("five").setPart2(9l).setPart3(Fruits.APPLE);
    LargeRecord updatedAssociationRecord = new LargeRecord().setKey(associationRecordKey).setMessage(new Message().setMessage(
        "updated message"));
    LargeRecord updatedAssociationRecord2 = new LargeRecord().setKey(associationRecordKey2).setMessage(new Message().setMessage(
        "another updated message"));
    builtRequests.put("association-get", new AssociationRequestBuilders(_options).get().id(associationKey).build());
    builtRequests.put("association-batch-get",
                      new AssociationRequestBuilders(_options).batchGet().ids(associationKey, associationKey2).build());
    builtRequests.put("association-update",
                      new AssociationRequestBuilders(_options).update().input(updatedAssociationRecord).id(associationKey).build());
    builtRequests.put("association-batch-update",
                      new AssociationRequestBuilders(_options).batchUpdate().input(associationKey,
                                                                           updatedAssociationRecord).input(
                          associationKey2,
                          updatedAssociationRecord2).build()
    );
    builtRequests.put("association-delete", new AssociationRequestBuilders(_options).delete().id(associationKey).build());
    builtRequests.put("association-batch-delete",
                      new AssociationRequestBuilders(_options).batchDelete().ids(associationKey, associationKey2).build());

    builtRequests.put("association-assoc-key-finder",
                      new AssociationRequestBuilders(_options).findByPart1().part1Key("one").build());

    builtRequests.put("simple-get", new SimpleRequestBuilders(_options).get().build());
    builtRequests.put("simple-update",
                      new SimpleRequestBuilders(_options).update().input(new Message().setMessage("updated message")).build());
    builtRequests.put("simple-delete", new SimpleRequestBuilders(_options).delete().build());

    builtRequests.put("actionset-echo",
                      new ActionSetRequestBuilders(_options).actionEcho().inputParam("Is anybody out there?").build());

    builtRequests.put("actionset-echo-bytes",
        new ActionSetRequestBuilders(_options).actionEchoBytes()
            .inputParam(ByteString.copy(new byte[]{1, 2, 3, 4}))
            .build());

    builtRequests.put("actionset-return-int", new ActionSetRequestBuilders(_options).actionReturnInt().build());

    builtRequests.put("actionset-return-bool", new ActionSetRequestBuilders(_options).actionReturnBool().build());

    builtRequests.put("actionset-echo-message",
                      new ActionSetRequestBuilders(_options).actionEchoMessage().messageParam(testMessage).build());

    MessageArray messageArray = new MessageArray();
    messageArray.add(testMessage);
    messageArray.add(anotherMessage);

    builtRequests.put("actionset-echo-message-array",
                      new ActionSetRequestBuilders(_options).actionEchoMessageArray().messagesParam(messageArray).build());

    StringArray stringArray = new StringArray();
    stringArray.add("string one");
    stringArray.add("string two");

    builtRequests.put("actionset-echo-string-array",
                      new ActionSetRequestBuilders(_options).actionEchoStringArray().stringsParam(stringArray).build());

    StringMap stringMap = new StringMap();
    stringMap.put("one", "string one");
    stringMap.put("two", "string two");

    builtRequests.put("actionset-echo-string-map",
                      new ActionSetRequestBuilders(_options).actionEchoStringMap().stringsParam(stringMap).build());

    builtRequests.put("actionset-echo-typeref-url",
                      new ActionSetRequestBuilders(_options).actionEchoTyperefUrl().urlTyperefParam("http://rest.li").build());

    UnionOfPrimitives unionOfPrimitives = new UnionOfPrimitives().setPrimitivesUnion(UnionOfPrimitives.PrimitivesUnion.create(
        100l));
    builtRequests.put("actionset-echo-primitive-union",
                      new ActionSetRequestBuilders(_options).actionEchoPrimitiveUnion().primitiveUnionParam(unionOfPrimitives).build());

    UnionOfComplexTypes unionOfComplexTypes = new UnionOfComplexTypes().setComplexTypeUnion(UnionOfComplexTypes.ComplexTypeUnion.create(
        Fruits.APPLE));
    builtRequests.put("actionset-echo-complex-types-union",
                      new ActionSetRequestBuilders(_options).actionEchoComplexTypesUnion().complexTypesUnionParam(
                          unionOfComplexTypes).build());

    builtRequests.put("actionset-empty-response",
                      new ActionSetRequestBuilders(_options).actionEmptyResponse().message1Param(testMessage).message2Param(
                          anotherMessage).build());

    builtRequests.put("actionset-multiple-inputs",
                      new ActionSetRequestBuilders(_options).actionMultipleInputs().messageParam(testMessage).optionalStringParam(
                          "optional string").stringParam("string").urlTyperefParam("http://rest.li").build());

    builtRequests.put("actionset-multiple-inputs-no-optional",
                      new ActionSetRequestBuilders(_options).actionMultipleInputs().messageParam(testMessage).stringParam(
                          "string").urlTyperefParam("http//rest.li").build()
    );

    builtRequests.put("sub-collection-of-collection-get",
                      new SubcollectionRequestBuilders(_options).get().collectionIdKey(1l).id(100l).build());
    builtRequests.put("sub-simple-of-collection-get", new SubsimpleRequestBuilders(_options).get().collectionIdKey(1l).build());

    // TODO:  theis setComplexKey method should return the Union like the other builder methods
    //new KeyWithUnion.Union().setComplexKey(complexKey)

    KeyWithUnion.Union union = new KeyWithUnion.Union();
    union.setComplexKey(complexKey);
    builtRequests.put("keywithunion-get",
                      new KeywithunionRequestBuilders(_options).get().id(new ComplexResourceKey<KeyWithUnion, KeyParams>(new KeyWithUnion().setUnion(
                          union), keyParams)).build()
    );

    builtRequests.put("params-get-with-queryparams",
                      new ParamsRequestBuilders(_options).get().intParam(3).longParam(Long.MAX_VALUE).messageArrayParam(
                          messageArray)
                          //.optionalStringParam("optional string")
                          .primitiveUnionParam(unionOfPrimitives).complexTypesUnionParam(unionOfComplexTypes).stringArrayParam(
                          stringArray).stringMapParam(stringMap).stringParam("string").urlTyperefParam("http://rest.li").id(
                          100l).build()
    );

    builtRequests.put("collection-typeref-get",
                      new CollectionTyperefRequestBuilders(_options).get().id("http://rest.li").build());

    builtRequests.put("association-typeref-get",
                      new AssociationTyperefRequestBuilders(_options).get().id(new CompoundKey().append("part1",
                                                                                                "http://rest.li").append(
                                                                           "part2",
                                                                           5l)
                      ).build());

    return builtRequests;
  }
}
