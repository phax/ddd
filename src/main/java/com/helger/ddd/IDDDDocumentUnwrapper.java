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
package com.helger.ddd;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.w3c.dom.Element;

import com.helger.annotation.Nonempty;

/**
 * SPI interface for detecting envelope/wrapper formats (such as SBDH or XHE)
 * and extracting the inner business document payload element.
 *
 * @author Philip Helger
 * @since 0.8.4
 */
public interface IDDDDocumentUnwrapper
{
  /**
   * @return A stable identifier for the envelope format handled by this
   *         unwrapper (e.g. <code>"SBDH"</code>, <code>"XHE"</code>). This
   *         value is used as a flag in the resulting {@link DocumentDetails}.
   *         Never <code>null</code>.
   */
  @NonNull
  @Nonempty
  String getWrappingType ();

  /**
   * Check if the provided root element is an envelope format handled by this
   * unwrapper, and if so, extract the inner payload element.
   *
   * @param aRootElement
   *        The root element to check. Never <code>null</code>.
   * @return The extracted inner payload element, or <code>null</code> if this
   *         unwrapper does not handle the provided element or if no payload
   *         could be found.
   */
  @Nullable
  Element unwrap (@NonNull Element aRootElement);
}
