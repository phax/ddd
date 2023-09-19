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

import java.time.LocalDate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.collection.impl.CommonsHashMap;
import com.helger.commons.collection.impl.ICommonsMap;
import com.helger.commons.io.resource.ClassPathResource;
import com.helger.commons.io.resource.IReadableResource;
import com.helger.xml.microdom.IMicroDocument;
import com.helger.xml.microdom.IMicroElement;
import com.helger.xml.microdom.serialize.MicroReader;

/**
 * This class manages a set of {@link DDDSyntax} objects
 *
 * @author Philip Helger
 */
public class DDDSyntaxList
{
  private static final Logger LOGGER = LoggerFactory.getLogger (DDDSyntaxList.class);

  public static final IReadableResource DEFAULT_SYNTAX_LIST_RES = new ClassPathResource ("ddd/syntaxes.xml",
                                                                                         DDDSyntaxList.class.getClassLoader ());

  private static class SingletonHolder
  {
    static final DDDSyntaxList INSTANCE = readFromXML (DEFAULT_SYNTAX_LIST_RES);
  }

  private final LocalDate m_aLastMod;
  private final ICommonsMap <String, DDDSyntax> m_aSyntaxes;

  public DDDSyntaxList (@Nonnull final LocalDate aLastMod, @Nonnull final ICommonsMap <String, DDDSyntax> aSyntaxes)
  {
    ValueEnforcer.notNull (aLastMod, "LastMod");
    ValueEnforcer.notNullNoNullValue (aSyntaxes, "Syntaxes");
    m_aLastMod = aLastMod;
    m_aSyntaxes = aSyntaxes;
  }

  @Nonnull
  public final LocalDate getLastModification ()
  {
    return m_aLastMod;
  }

  @Nonnull
  public final ICommonsMap <String, DDDSyntax> getAllSyntaxes ()
  {
    return m_aSyntaxes.getClone ();
  }

  @Nullable
  public DDDSyntax findMatchingSyntax (@Nonnull final Element aRootElement)
  {
    ValueEnforcer.notNull (aRootElement, "RootElement");

    final String sNamespaceURI = aRootElement.getNamespaceURI ();
    final String sLocalName = aRootElement.getLocalName ();

    for (final DDDSyntax aSyntax : m_aSyntaxes.values ())
      if (aSyntax.matches (sNamespaceURI, sLocalName))
        return aSyntax;

    return null;
  }

  @Nonnull
  public static DDDSyntaxList readFromXML (@Nonnull final IReadableResource aRes)
  {
    LOGGER.info ("Reading DDDSyntaxList from '" + aRes.getPath () + "'");

    final IMicroDocument aDoc = MicroReader.readMicroXML (aRes);
    if (aDoc == null)
      throw new IllegalArgumentException ("Failed to read DDD syntax list");

    final LocalDate aLastMod = aDoc.getDocumentElement ().getAttributeValueWithConversion ("lastmod", LocalDate.class);
    if (aLastMod == null)
      throw new IllegalArgumentException ("The DDD syntax list is missing a valid 'lastmod' attribute");

    final ICommonsMap <String, DDDSyntax> aSyntaxes = new CommonsHashMap <> ();
    for (final IMicroElement eSyntax : aDoc.getDocumentElement ().getAllChildElements ("syntax"))
    {
      final DDDSyntax aSyntax = DDDSyntax.readFromXML (eSyntax);
      if (aSyntaxes.containsKey (aSyntax.getID ()))
        throw new IllegalStateException ("Another DDD syntax with ID '" + aSyntax.getID () + "' is already contained");

      aSyntaxes.put (aSyntax.getID (), aSyntax);
    }
    return new DDDSyntaxList (aLastMod, aSyntaxes);
  }

  /**
   * @return The syntax list from the default syntax list file. Never
   *         <code>null</code>.
   * @see #readFromXML(IReadableResource)
   * @see #DEFAULT_SYNTAX_LIST_RES
   */
  @Nonnull
  public static DDDSyntaxList getDefaultSyntaxList ()
  {
    return SingletonHolder.INSTANCE;
  }
}
