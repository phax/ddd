/*
 * Copyright (C) 2023-2024 Philip Helger (www.helger.com)
 * philip[at]helger[dot]com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
