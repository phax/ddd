package com.helger.ddd.model;

import javax.annotation.Nonnull;
import javax.xml.xpath.XPathFactory;

import com.helger.commons.lang.ClassLoaderHelper;
import com.helger.xml.EXMLParserFeature;

/**
 * XPath helper
 *
 * @author Philip Helger
 */
final class XPathHelper
{
  static final XPathFactory XPATH_FACTORY;

  static
  {
    XPATH_FACTORY = createXPathFactorySaxonFirst ();
  }

  /**
   * Create a new {@link XPathFactory} trying to instantiate Saxon class
   * <code>net.sf.saxon.xpath.XPathFactoryImpl</code> first. If that fails, the
   * default XPathFactory is created.
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
