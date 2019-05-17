Rest.li Client Test Suite
=========================

This directory contains a language independent Rest.li client test suite.

The suite is composed of:

* Files in various language independent formats such as .json, .pdsc and .restspec.json.
* A manifest.json file containing a listing of all the test files to help drive automated test execution.
* Guidelines on how to use the test suite to validate a rest.li client implementation.
* An Java testNG suite that uses this test data to validate the Java Rest.li client implementation.

This suite is intended to cover:

* Wire protocol - HTTP Requests and Responses: Serializing/deserializing of URLs, Headers and bodies.  Escaping/encoding, batch formats,
  projections, and partial updates.
* Data Schemas - Schema types (records, unions, enums, typerefs, ...), primitive types, optionals and defaults.  Backward compatibility
  rules are also covered.
* Data - JSON corner cases such as large numbers,  special characters and encodings.

Why?
----

This test suite will help us achieve consistently high quality implementations of the rest.li client in multiple
languages.

* Reduce the work required to add a new rest.li client implementation
* Provide a common ground where test cases can be shared amongst multiple rest.li client implementations
* "Certify" rest.li client implementations with a quantifiable measure of quality

References
----------

This suite is intended to complement this below reference documentation.  Implementers are encouraged to explore the
test data and reference documentation in tandem to learn the details of rest.li.

* .pdscs - https://github.com/linkedin/rest.li/wiki/DATA-Data-Schema-and-Templates
* .restspec.json - https://github.com/linkedin/rest.li/wiki/Rest.li-.restspec.json-Format
* wire protocol - https://github.com/linkedin/rest.li/wiki/Rest.li-Protocol

manifest.json
-------------

This manifest file provides machine readable information about all the tests data provided in this suite.

This file is intended to help drive portions of the test suite execution.  Although it does help, writing additional
assertions by hand will still be needed in most languages to validate correctness of in-memory representations and language
bindings.

The file is broken down into a few main sections:

* jsonTestData - list of JSON data files.
* schemaTestData - list of Rest.li data schema files (.pdsc) as well as JSON data files matching the schemas.
* wireProtocolTestData - list of Rest.li interface definition files (.restspec.json) as well as test HTTP requests and responses,
  in the form of files, for operations supported by the interface definition.

How to use this test suite
--------------------------
This section explains how to use the test suite to validate a general Rest.li client implementation.
For a specific example, refer to the Java testNG suite, which uses the test suite to validate a Java Rest.li client implementation.

### Getting Started

1.  Select a test framework.  You should use the same test framework you usually use for the language your rest.li client is written in.
2.  In your test framework, load the manifest.json file into a convenient in-memory representation before running the tests.
3.  Setup utility methods to read in the files referenced by the mainifest.json files.
4.  If the rest.li client includes code generators,  run them against all the files in the 'schemas' directory (for data
    bindings) and/or the 'restspecs' directory (for rest client bindings).

### 'jsonTestData' (.json files)

Test for correct serialization/deserialization of all the provided 'data' files.

One possible approach, which is used by the java implementation:

1. deserializing a JSON file to an in-memory representation
2. serializing the in memory representation back to bytes (or a file)
3. deserializing the bytes again into another in memory representation
4. comparing the two in-memory representations
(Note that comparing the serialized representations isn't always an option since JSON map entries are unordered)

###  'schemaTestData' (.pdsc files)

For implementations that use data schemas to provide convenience data bindings from data schemas (either dynamically or
via code generation):

1.  construct data bindings for all the 'schema' files.
2.  load instance data for each accessor according to the 'data' fields of the 'schemas' in manifest.json.
3.  verify the accessor is correctly constructed and provides access to the data correctly, writing custom asserts as needed
4.  if the schema contains default fields, validate that they are defaulted correctly

For implementations that provide validation:

1. load instance data for each 'schemas.data' file and validate it against the 'schemas.schema' files.  Note that some data
   files should fail to validate (and be marked as such in manifest.json).

### 'wireProtocolTestData' (.restspec.json, .req, .res files)

To test requests and responses, flat files containing http requests and responses are provided in 'requests' and 'responses',
which are for Rest.li protocol 1.0.0.
These flat files have counterparts in 'requests-v2' and 'responses-v2' that use Rest.li protocol version 2.0.0.
but are otherwise identical.

A few possible approaches one might take to use these test files:

1. Organize the rest.li client code such that a http request can be produced and tested without actually sending it
   and such that a http response stored in a flat file can be handled as though it was received in response to a request.
2. Create a simple mock http server that uses the flat files to send the appropriate response back for each request, and
   returns a clear error message back if the client sends an incorrect request.

The java implementation uses approach 1.

For implementations that use restspecs to provide convenience rest client bindings from restspecs (either via dynamically
or code generation):

1. Construct rest client bindings for all the 'restspec' files.
2. Use the constructed rest client bindings to produce http requests.  Do this for all the 'restspecs.operations.request'
   entries in manifest.json.  Instead of sending the requests,  keep the request in a in-memory representation (a string,
   or basic http representation) or write it to a file.
3. Verify the http request created by the rest client bindings matches the expected http as found in the
   'restspecs.operations.request' files.  This may require writing a routine to compare http requests that ignores header
   order.
4. For each of the 'restspecs.operations.response' files,  verify the constructed rest client bindings are able to
   handle the response correctly (as if it were sent as an actual HTTP response).  Write custom assertions to verify all the
   fields in the HTTP response are correctly marshaled to response representation provided by the constructed rest client
   bindings.

For implementations that do not provide convenience rest client bindings:

1. At a minimum, utilities to serialize data to request URLs and deserialize data from response headers and batch map keys
   must be provided.   Write tests to validate that all the .request files can be produced correctly using these utilities.

Running the tests in Java
-------------------------

To validate that these tests all pass using the java implementation of the rest.li client, *and* to demonstrate how
to use the test suite.   This project contains a testNG test suite that can be easily run.

To run:

```
gradle test
```

Adding a test to the test suite
-------------------------------

To add a test,  add the necessary .json or .pdsc files.  Please do not modify existing tests, multiple language implementations
are using these test and changing existing tests may break their test suites.

Keep tests simple!  They should test a single case and test it well.

The java suite contains a convenience tool to generate .restspec.json, .req and .res files.  To use it:

1. Add or update the *Resource.java classes in the restli-testsuite-server project under src/main/java/testsuite.
2. Re-generate the .restspec.json and .snapshot.json files:

    ```
    gradle publishRestIdl
    gradle publishRestSnapshot
    ```

3. Run the test server using:

    ```
    gradle JettyRunWar
    ```
    This will generate or update the appropriate RequestBuilders, which are used to make the requests in the following step.

4. Update RequestResponseTestCases in client-testsuite by adding a new request and test name to the builtRequests map.

    For the java implementation, this is done by modifying the builtRequests map in the buildRequests() function,
    in client-testsuite/src/test/java/com/linkedin/pegasus/testsuite/RequestResponseTestCases.

5. Regenerate the request and response files.  Files will be written to the "requests" and "responses" directories:

    ```
    gradle generateRequestAndResponseFiles
    ```

6. Update manifest.json to include test data references to all the files you've added.
7. Update the tests in TestRestClientWithManualAssertions with any manual assertions you would like to test.

Development Status
------------------

* [x] Basic JSON tests added
* [x] Basic schema tests added
* [x] Basic collection request/response tests added
* [x] Basic complex key request/response tests added
* [x] Basic association request/response tests added
* [x] Basic simple request/response tests added
* [x] Basic actionset request/response tests added
* [x] 404, validation error and other client side error tests added
* [x] 500 and other server side error tests added
* [x] Batch tests added
* [x] Partial update tests added
* [x] Projection tests added
* [x] Subresource tests added
* [x] Unions and maps and lists in complex keys tests added
* [x] Finder tests added
* [x] complex action tests added
* [x] Query params on methods tests added
* [x] Unions and maps and lists in query params tests added
* [x] Error details tests added
* [x] Batch results with errors tests added
* [x] partial update of a nested field
* [x] special chars in strings tests added
* [x] test finder that uses association entity key part
* [x] test finder with metadata
* [x] Resource Typeref support tests added
* [x] generate 2.0.0 wire protocol requests for all tests into separate directories
* [x] Union of same types in complex types added
* [x] Basic request/response test for collection with returning entity in create method added
* [ ] generate 2.0.0 wire protocol responses for all tests into separate directories
* [ ] add test for tunneled query params
* [ ] add test that includes unicode (non-ascii) characters on wire
