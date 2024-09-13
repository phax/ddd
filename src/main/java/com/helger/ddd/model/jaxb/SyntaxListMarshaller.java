package com.helger.ddd.model.jaxb;

import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.io.resource.ClassPathResource;
import com.helger.ddd.model.jaxb.syntax1.ObjectFactory;
import com.helger.ddd.model.jaxb.syntax1.SyntaxesType;
import com.helger.jaxb.GenericJAXBMarshaller;

/**
 * JAXB marshaller for DDD syntaxes list.
 *
 * @author Philip Helger
 * @since 0.4.0
 */
public class SyntaxListMarshaller extends GenericJAXBMarshaller <SyntaxesType>
{
  public static final ClassPathResource XSD = new ClassPathResource ("schemas/ddd-syntaxes-1.0.xsd",
                                                                     SyntaxListMarshaller.class.getClassLoader ());

  public SyntaxListMarshaller ()
  {
    super (SyntaxesType.class, new CommonsArrayList <> (XSD), new ObjectFactory ()::createSyntaxes);
  }
}
