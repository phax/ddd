package com.helger.ddd.model;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.transform.dom.DOMSource;
import javax.xml.xpath.XPathEvaluationResult;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Node;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.error.SingleError;
import com.helger.commons.error.list.IErrorList;
import com.helger.commons.string.ToStringGenerator;

/**
 * Specific implementation of {@link IDDDGetter} based on XPath
 *
 * @author Philip Helger
 */
public class DDDGetterXPath implements IDDDGetter
{
  private final String m_sXPath;
  private final XPathExpression m_aXPathExpr;

  public DDDGetterXPath (@Nonnull @Nonempty final String sXPath)
  {
    ValueEnforcer.notEmpty (sXPath, "XPath");
    m_sXPath = sXPath;

    // Compile once in the constructor
    try
    {
      m_aXPathExpr = XPathHelper.XPATH_FACTORY.newXPath ().compile (sXPath);
    }
    catch (final XPathExpressionException ex)
    {
      throw new IllegalArgumentException ("The provided XPath expression '" + sXPath + "' is invalid", ex);
    }
  }

  @Nullable
  public String getValue (@Nonnull final Node aSourceNode, @Nonnull final IErrorList aErrorList)
  {
    ValueEnforcer.notNull (aSourceNode, "SourceNode");
    ValueEnforcer.notNull (aErrorList, "ErrorList");

    try
    {
      final XPathEvaluationResult <?> aRes = m_aXPathExpr.evaluateExpression (new DOMSource (aSourceNode));
      switch (aRes.type ())
      {
        case STRING:
          return String.class.cast (aRes.value ());
        default:
          aErrorList.add (SingleError.builderError ()
                                     .errorText ("The XPath expression '" +
                                                 m_sXPath +
                                                 "' returned the unsupported type '" +
                                                 aRes.type () +
                                                 "'")
                                     .build ());
          break;
      }
    }
    catch (final Exception ex)
    {
      aErrorList.add (SingleError.builderError ()
                                 .errorText ("The Failed to apply XPath expression '" + m_sXPath + "' on XML node")
                                 .linkedException (ex)
                                 .build ());
    }

    // Fallback for everything
    return null;
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (null).append ("XPath", m_sXPath).getToString ();
  }
}
