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
import com.helger.annotation.concurrent.Immutable;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.ddd.IDDDDocumentUnwrapper;

/**
 * Document unwrapper for XHE (Exchange Header Envelope) envelopes. Detects {@code XHE} elements in
 * the XHE namespace and extracts the inner business document payload from the first payload entry.
 *
 * @author Philip Helger
 * @since 0.8.4
 */
@Immutable
public class DDDDocumentUnwrapperXHE implements IDDDDocumentUnwrapper
{
  /** Default instance */
  public static final DDDDocumentUnwrapperXHE INSTANCE = new DDDDocumentUnwrapperXHE ();

  /** XHE root element namespace URI */
  public static final String NAMESPACE_URI_XHE = "http://docs.oasis-open.org/bdxr/ns/XHE/1/ExchangeHeaderEnvelope";

  /** XHE aggregate components namespace URI */
  public static final String NAMESPACE_URI_AC = "http://docs.oasis-open.org/bdxr/ns/XHE/1/AggregateComponents";

  @NonNull
  @Nonempty
  public String getWrappingType ()
  {
    return "XHE";
  }

  @Nullable
  private static Element _getFirstChildElement (@NonNull final Element aParent, @NonNull final String sLocalName)
  {
    for (Node aChild = aParent.getFirstChild (); aChild != null; aChild = aChild.getNextSibling ())
      if (aChild instanceof final Element aChildElement)
        if (sLocalName.equals (aChildElement.getLocalName ()))
          return aChildElement;
    return null;
  }

  @Nullable
  private static Element _getFirstChildElement (@NonNull final Element aParent)
  {
    for (Node aChild = aParent.getFirstChild (); aChild != null; aChild = aChild.getNextSibling ())
      if (aChild instanceof final Element aChildElement)
        return aChildElement;
    return null;
  }

  @Nullable
  public Element unwrap (@NonNull final Element aRootElement)
  {
    ValueEnforcer.notNull (aRootElement, "RootElement");

    if (!NAMESPACE_URI_XHE.equals (aRootElement.getNamespaceURI ()) || !"XHE".equals (aRootElement.getLocalName ()))
      return null;

    // Navigate: Payloads -> Payload[1] -> PayloadContent -> first child element
    final Element aPayloads = _getFirstChildElement (aRootElement, "Payloads");
    if (aPayloads == null)
      return null;

    final Element aPayload = _getFirstChildElement (aPayloads, "Payload");
    if (aPayload == null)
      return null;

    final Element aPayloadContent = _getFirstChildElement (aPayload, "PayloadContent");
    if (aPayloadContent == null)
      return null;

    // The first child element of PayloadContent is the business document
    return _getFirstChildElement (aPayloadContent);
  }
}
