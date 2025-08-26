/*
 * Copyright (C) 2023-2025 Philip Helger (www.helger.com)
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

import com.helger.collection.commons.CommonsArrayList;
import com.helger.ddd.model.jaxb.syntax1.ObjectFactory;
import com.helger.ddd.model.jaxb.syntax1.SyntaxesType;
import com.helger.io.resource.ClassPathResource;
import com.helger.jaxb.GenericJAXBMarshaller;

/**
 * JAXB marshaller for DDD syntaxes list.
 *
 * @author Philip Helger
 * @since 0.4.0
 */
public class SyntaxListMarshaller extends GenericJAXBMarshaller <SyntaxesType>
{
  public static final ClassPathResource XSD = new ClassPathResource ("schemas/ddd-syntaxes-1.1.xsd",
                                                                     SyntaxListMarshaller.class.getClassLoader ());

  public SyntaxListMarshaller ()
  {
    super (SyntaxesType.class, new CommonsArrayList <> (XSD), new ObjectFactory ()::createSyntaxes);
  }
}
