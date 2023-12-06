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

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.annotation.ReturnsMutableObject;
import com.helger.commons.collection.impl.CommonsHashMap;
import com.helger.commons.collection.impl.ICommonsMap;
import com.helger.commons.io.resource.ClassPathResource;
import com.helger.commons.io.resource.IReadableResource;
import com.helger.commons.string.ToStringGenerator;
import com.helger.xml.microdom.IMicroDocument;
import com.helger.xml.microdom.IMicroElement;
import com.helger.xml.microdom.serialize.MicroReader;

/**
 * This class manages a list of {@link DDDValueProviderPerSyntax} objects. The
 * key is the syntax ID. A default mapping is provided as part of the library.
 * See {@link #getDefaultValueProviderList()}.
 *
 * @author Philip Helger
 */
public class DDDValueProviderList
{
  private static final Logger LOGGER = LoggerFactory.getLogger (DDDValueProviderList.class);

  /** The resource that contains the default value provide list */
  public static final IReadableResource DEFAULT_VALUE_PROVIDER_LIST_RES = new ClassPathResource ("ddd/value-providers.xml",
                                                                                                 DDDValueProviderList.class.getClassLoader ());

  private static class SingletonHolder
  {
    static final DDDValueProviderList INSTANCE = readFromXML (DEFAULT_VALUE_PROVIDER_LIST_RES);
  }

  private final LocalDate m_aLastMod;
  private final ICommonsMap <String, DDDValueProviderPerSyntax> m_aVPPerSyntaxes;

  public DDDValueProviderList (@Nonnull final LocalDate aLastMod,
                               @Nonnull final ICommonsMap <String, DDDValueProviderPerSyntax> aSyntaxes)
  {
    ValueEnforcer.notNull (aLastMod, "LastMod");
    ValueEnforcer.notNull (aSyntaxes, "Syntaxes");
    m_aLastMod = aLastMod;
    m_aVPPerSyntaxes = aSyntaxes;
  }

  @Nonnull
  public final LocalDate getLastModification ()
  {
    return m_aLastMod;
  }

  @Nonnull
  @ReturnsMutableObject
  public final ICommonsMap <String, DDDValueProviderPerSyntax> valueProvidersPerSyntaxes ()
  {
    return m_aVPPerSyntaxes;
  }

  @Nonnull
  @ReturnsMutableCopy
  public final ICommonsMap <String, DDDValueProviderPerSyntax> getAllValueProvidersPerSyntaxes ()
  {
    return m_aVPPerSyntaxes.getClone ();
  }

  @Nullable
  public DDDValueProviderPerSyntax getValueProviderPerSyntax (@Nullable final String sSyntaxID)
  {
    return m_aVPPerSyntaxes.get (sSyntaxID);
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (null).append ("LastModification", m_aLastMod)
                                       .append ("VPPerSyntaxes", m_aVPPerSyntaxes)
                                       .getToString ();
  }

  @Nonnull
  public static DDDValueProviderList readFromXML (@Nonnull final IReadableResource aRes)
  {
    ValueEnforcer.notNull (aRes, "Resource");

    LOGGER.info ("Reading DDDValueProviderList from '" + aRes.getPath () + "'");

    final IMicroDocument aDoc = MicroReader.readMicroXML (aRes);
    if (aDoc == null)
      throw new IllegalArgumentException ("Failed to read DDD syntax list");

    final LocalDate aLastMod = aDoc.getDocumentElement ().getAttributeValueWithConversion ("lastmod", LocalDate.class);
    if (aLastMod == null)
      throw new IllegalArgumentException ("The DDD syntax list is missing a valid 'lastmod' attribute");

    final ICommonsMap <String, DDDValueProviderPerSyntax> aSyntaxes = new CommonsHashMap <> ();
    for (final IMicroElement eSyntax : aDoc.getDocumentElement ().getAllChildElements ("syntax"))
    {
      final DDDValueProviderPerSyntax aVesidPerSyntax = DDDValueProviderPerSyntax.readFromXML (eSyntax);
      if (aSyntaxes.containsKey (aVesidPerSyntax.getSyntaxID ()))
        throw new IllegalStateException ("Another DDD syntax with ID '" +
                                         aVesidPerSyntax.getSyntaxID () +
                                         "' is already contained");

      aSyntaxes.put (aVesidPerSyntax.getSyntaxID (), aVesidPerSyntax);
    }
    return new DDDValueProviderList (aLastMod, aSyntaxes);
  }

  /**
   * @return The value provider list from the default file. Never
   *         <code>null</code>.
   * @see #readFromXML(IReadableResource)
   * @see #DEFAULT_VALUE_PROVIDER_LIST_RES
   */
  @Nonnull
  public static DDDValueProviderList getDefaultValueProviderList ()
  {
    return SingletonHolder.INSTANCE;
  }
}
