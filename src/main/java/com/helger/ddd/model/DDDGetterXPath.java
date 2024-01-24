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
package com.helger.ddd.model;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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
@Immutable
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
      final NodeList aNL = (NodeList) m_aXPathExpr.evaluate (aSourceNode, XPathConstants.NODESET);
      final int nSize = aNL.getLength ();
      if (nSize == 1)
        return aNL.item (0).getNodeValue ();
      aErrorList.add (SingleError.builderError ()
                                 .errorText ("The XPath expression '" +
                                             m_sXPath +
                                             "' returned " +
                                             (nSize == 0 ? "an empty NodeSet" : "a NodeSet with " +
                                                                                nSize +
                                                                                " elements"))
                                 .build ());
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
