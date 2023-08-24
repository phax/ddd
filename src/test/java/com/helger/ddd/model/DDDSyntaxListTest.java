/*
 * Copyright (C) 2023 Philip Helger (www.helger.com)
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Map;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import com.helger.commons.collection.impl.ICommonsMap;
import com.helger.commons.error.list.ErrorList;
import com.helger.commons.io.file.FileSystemIterator;
import com.helger.commons.io.file.IFileFilter;
import com.helger.xml.serialize.read.DOMReader;

/**
 * Test class for class {@link DDDSyntaxList}
 *
 * @author Philip Helger
 */
public final class DDDSyntaxListTest
{
  private static final Logger LOGGER = LoggerFactory.getLogger (DDDSyntaxListTest.class);

  @Test
  public void testDefault ()
  {
    final DDDSyntaxList aSL = DDDSyntaxList.readFromXML (DDDSyntaxList.DEFAULT_SYNTAX_LIST_RES);
    assertNotNull (aSL);

    final ICommonsMap <String, DDDSyntax> aMap = aSL.getAllSyntaxes ();
    assertEquals (3, aMap.size ());
    assertTrue (aMap.containsKey ("ubl2-invoice"));
    assertTrue (aMap.containsKey ("ubl2-creditnote"));
    assertTrue (aMap.containsKey ("cii"));
  }

  @Test
  public void testAllTestfile ()
  {
    final DDDSyntaxList aSL = DDDSyntaxList.readFromXML (DDDSyntaxList.DEFAULT_SYNTAX_LIST_RES);

    // For all syntaxes
    for (final Map.Entry <String, DDDSyntax> aSyntaxEntry : aSL.getAllSyntaxes ().entrySet ())
    {
      final DDDSyntax aSyntax = aSyntaxEntry.getValue ();

      // Search for positive cases for the current syntax
      for (final File f : new FileSystemIterator ("src/test/resources/external/" + aSyntaxEntry.getKey () + "/good")
                                                                                                                    .withFilter (IFileFilter.filenameEndsWith (".xml")))
      {
        LOGGER.info ("Reading as [" + aSyntax.getID () + "] " + f.toString ());

        // Read as XML
        final Document aDoc = DOMReader.readXMLDOM (f);
        assertNotNull (aDoc);

        // Test all getters
        final ErrorList aErrorList = new ErrorList ();
        for (final EDDDGetterType eGetter : EDDDGetterType.values ())
        {
          final String sValue = aSyntax.getValue (eGetter, aDoc.getDocumentElement (), aErrorList);
          if (eGetter.isMandatory ())
            assertNotNull ("Getter " + eGetter + " failed on " + f + "\n" + aErrorList.getAllErrors (), sValue);
          if (false)
            LOGGER.info ("  " + eGetter + " --> " + sValue);
        }
      }
    }
  }
}
