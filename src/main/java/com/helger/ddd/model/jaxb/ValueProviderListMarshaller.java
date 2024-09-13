package com.helger.ddd.model.jaxb;

import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.io.resource.ClassPathResource;
import com.helger.ddd.model.jaxb.vp1.ObjectFactory;
import com.helger.ddd.model.jaxb.vp1.ValueProvidersType;
import com.helger.jaxb.GenericJAXBMarshaller;

/**
 * JAXB marshaller for DDD value provider list.
 *
 * @author Philip Helger
 * @since 0.4.0
 */
public class ValueProviderListMarshaller extends GenericJAXBMarshaller <ValueProvidersType>
{
  public static final ClassPathResource XSD = new ClassPathResource ("schemas/ddd-value-providers-1.0.xsd",
                                                                     ValueProviderListMarshaller.class.getClassLoader ());

  public ValueProviderListMarshaller ()
  {
    super (ValueProvidersType.class, new CommonsArrayList <> (XSD), new ObjectFactory ()::createVps);
  }
}
