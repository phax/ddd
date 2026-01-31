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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.helger.json.IJsonObject;
import com.helger.peppolid.factory.SimpleIdentifierFactory;

/**
 * Test class for class {@link DocumentDetailsJsonHelper}.
 *
 * @author Philip Helger
 */
public final class DocumentDetailsJsonHelperTest
{
  @Test
  public void testSimple ()
  {
    // Test null
    assertNull (DocumentDetailsJsonHelper.getAsDocumentDetails (null, SimpleIdentifierFactory.INSTANCE));

    // Test empty object
    final DocumentDetails aDDEmpty = DocumentDetails.builder ().build ();
    final IJsonObject aJson = aDDEmpty.getAsJson ();
    final DocumentDetails aDD2 = DocumentDetailsJsonHelper.getAsDocumentDetails (aJson,
                                                                                 SimpleIdentifierFactory.INSTANCE);
    assertEquals (aDDEmpty, aDD2);
  }
}
