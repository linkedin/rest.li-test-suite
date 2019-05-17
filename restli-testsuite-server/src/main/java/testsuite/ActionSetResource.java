package testsuite;


import com.linkedin.data.template.StringMap;
import com.linkedin.restli.server.annotations.Action;
import com.linkedin.restli.server.annotations.ActionParam;
import com.linkedin.restli.server.annotations.Optional;
import com.linkedin.restli.server.annotations.RestLiActions;


/**
 * @author jbetz@linkedin.com
 */
@RestLiActions(name = "actionSet", namespace = "testsuite")
public class ActionSetResource
{
  @Action(name="echo")
  public String echo(@ActionParam("input") final String input)
  {
    return input;
  }

  @Action(name="returnInt")
  public int returnInt()
  {
    return 42;
  }

  @Action(name="returnBool")
  public boolean returnBool()
  {
    return true;
  }

  @Action(name="echoStringArray")
  public String[] echoStringArray(@ActionParam("strings") final String[] inputs)
  {
    return inputs;
  }

  @Action(name="echoStringMap")
  public StringMap echoStringMap(@ActionParam("strings") final StringMap inputs)
  {
    return inputs;
  }

  @Action(name="echoMessage")
  public Message echoMessage(@ActionParam("message") final Message message)
  {
    return message;
  }

  @Action(name="echoMessageArray")
  public Message[] echoMessageArray(@ActionParam("messages") final Message[] messages)
  {
    return messages;
  }

  // currently requires typeref or generated map class
  /*@Action(name="echoMessageMap")
  public Map<String, Message> echoMessageMap(@ActionParam("messages") final Map<String, Message> messages)
  {
    return messages;
  }*/

  @Action(name="echoTyperefUrl", returnTyperef = Url.class)
  public String echoTyperefUrl(@ActionParam(value = "urlTyperef", typeref = Url.class) String urlTyperef)
  {
    return urlTyperef;
  }

  // currently requires typeref
  /*@Action(name="echoTyperefUrlArray", returnTyperef = Url.class)
  public String[] echoTyperefUrlArray(@ActionParam(value = "urlTyperefs", typeref = Url.class) String[] urlTyperefs)
  {
    return urlTyperefs;
  }*/

  // currently requires typeref or generated map class
  /*@Action(name="echoTyperefUrlMap", returnTyperef = Url.class)
  public Map<String, String> echoTyperefUrlMap(@ActionParam(value = "urlTyperefs", typeref = Url.class) Map<String, String> urlTyperefs)
  {
    return urlTyperefs;
  }*/

  @Action(name="echoPrimitiveUnion")
  public UnionOfPrimitives echoPrimitiveUnion(@ActionParam(value = "primitiveUnion") UnionOfPrimitives primitiveUnion)
  {
    return primitiveUnion;
  }

  @Action(name="echoComplexTypesUnion")
  public UnionOfComplexTypes echoComplexTypesUnion(@ActionParam(value = "complexTypesUnion") UnionOfComplexTypes complexTypesUnion)
  {
    return complexTypesUnion;
  }

  @Action(name="emptyResponse")
  public void emptyResponse(@ActionParam("message1") final Message message1, @ActionParam("message2") final Message message2)
  {
  }

  @Action(name="multipleInputs")
  public boolean multipleInputs(
      @ActionParam("string") final String s,
      @ActionParam("message") final Message message,
      @ActionParam(value = "urlTyperef", typeref = Url.class) String urlTyperef,
      @ActionParam("optionalString") @Optional final String optionalString)
  {
    return true;
  }

}
