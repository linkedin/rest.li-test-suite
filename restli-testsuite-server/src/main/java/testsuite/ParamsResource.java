package testsuite;


import com.linkedin.data.template.StringMap;
import com.linkedin.restli.server.annotations.Optional;
import com.linkedin.restli.server.annotations.QueryParam;
import com.linkedin.restli.server.annotations.RestLiCollection;
import com.linkedin.restli.server.annotations.RestMethod;
import com.linkedin.restli.server.resources.CollectionResourceTemplate;


/**
 * @author jbetz@linkedin.com
 */
@RestLiCollection(name = "params", namespace = "testsuite")
public class ParamsResource extends CollectionResourceTemplate<Long, Message>
{
  @RestMethod.Get
  public Message get(Long key,
                     @QueryParam("int") Integer i,
                     @QueryParam("string") String s,
                     @QueryParam("long") Long l,

                     @QueryParam("stringArray") String[] primitiveArray,
                     @QueryParam("messageArray") Message[] recordArray,
                     @QueryParam("stringMap") StringMap stringMap,
                     //@QueryParam("messageMap") Map<String, Message> messageMap, // currently requires typeref or generated map class

                     @QueryParam("primitiveUnion") UnionOfPrimitives primitiveUnion,
                     @QueryParam("complexTypesUnion") UnionOfComplexTypes complexTypesUnion,

                     @QueryParam("optionalString") @Optional String optional,

                     @QueryParam(value = "urlTyperef", typeref = Url.class) String urlTyperef
                     // @QueryParam(value = "urlTyperefArray", typeref = Url.class) String[] urlTyperefArray // currently requires typeref
                     //@QueryParam(value = "urlTyperefMap", typeref = Url.class) Map<String, String> urlTyperefMap // currently requires typeref or generated map class
                     )
  {
    return new Message().setId(1).setMessage("test message");
  }
}
