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
package com.helger.ddd.model;

import javax.xml.xpath.XPathFactory;

import com.helger.annotation.concurrent.Immutable;
import com.helger.base.classloader.ClassLoaderHelper;
import com.helger.xml.EXMLParserFeature;

import jakarta.annotation.Nonnull;

/**
 * XPath helper
 *
 * @author Philip Helger
 */
@Immutable
final class XPathHelper
{
  static final XPathFactory XPATH_FACTORY;

  static
  {
    XPATH_FACTORY = createXPathFactorySaxonFirst ();
  }

  /**
   * Create a new {@link XPathFactory} trying to instantiate Saxon class
   * <code>net.sf.saxon.xpath.XPathFactoryImpl</code> first. If that fails, the default XPathFactory
   * is created.
   *
   * @return A new {@link XPathFactory} and never <code>null</code>.
   * @throws IllegalStateException
   *         In case neither Saxon nor default factory could be instantiated!
   */
  @Nonnull
  public static XPathFactory createXPathFactorySaxonFirst ()
  {
    // The XPath object used to compile the expressions
    XPathFactory aXPathFactory;
    try
    {
      // First try to use Saxon, using the context class loader
      aXPathFactory = XPathFactory.newInstance (XPathFactory.DEFAULT_OBJECT_MODEL_URI,
                                                "net.sf.saxon.xpath.XPathFactoryImpl",
                                                ClassLoaderHelper.getContextClassLoader ());
    }
    catch (final Exception ex)
    {
      // Must be Throwable because of e.g. IllegalAccessError (see issue #19)
      // Seems like Saxon is not in the class path - fall back to default JAXP
      try
      {
        aXPathFactory = XPathFactory.newInstance (XPathFactory.DEFAULT_OBJECT_MODEL_URI);
      }
      catch (final Exception ex2)
      {
        throw new IllegalStateException ("Failed to create JAXP XPathFactory", ex2);
      }
    }

    // Secure processing by default
    EXMLParserFeature.SECURE_PROCESSING.applyTo (aXPathFactory, true);
    return aXPathFactory;
  }
}
