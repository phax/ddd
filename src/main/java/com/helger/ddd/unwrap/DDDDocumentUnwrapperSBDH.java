/*
 * Copyright (C) 2023-2026 Philip Helger (www.helger.com)
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
package com.helger.ddd.unwrap;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.helger.annotation.Nonempty;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.ddd.IDDDDocumentUnwrapper;

/**
 * Document unwrapper for SBDH (Standard Business Document Header) envelopes. Detects
 * {@code StandardBusinessDocument} elements in the SBDH namespace and extracts the inner business
 * document payload.
 *
 * @author Philip Helger
 * @since 0.8.4
 */
public class DDDDocumentUnwrapperSBDH implements IDDDDocumentUnwrapper
{
  /** SBDH namespace URI */
  public static final String NAMESPACE_URI = "http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader";

  @NonNull
  @Nonempty
  public String getWrappingType ()
  {
    return "SBDH";
  }

  @Nullable
  public Element unwrap (@NonNull final Element aRootElement)
  {
    ValueEnforcer.notNull (aRootElement, "RootElement");

    if (!NAMESPACE_URI.equals (aRootElement.getNamespaceURI ()) ||
        !"StandardBusinessDocument".equals (aRootElement.getLocalName ()))
      return null;

    // Find the first child element that is NOT the StandardBusinessDocumentHeader
    for (Node aChild = aRootElement.getFirstChild (); aChild != null; aChild = aChild.getNextSibling ())
    {
      if (aChild instanceof final Element aChildElement)
      {
        if (!(NAMESPACE_URI.equals (aChildElement.getNamespaceURI ()) &&
              "StandardBusinessDocumentHeader".equals (aChildElement.getLocalName ())))
        {
          return aChildElement;
        }
      }
    }
    return null;
  }
}
