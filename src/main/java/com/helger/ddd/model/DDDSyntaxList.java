/*
 * Copyright (C) 2023-2025 Philip Helger (www.helger.com)
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
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.collection.commons.CommonsHashMap;
import com.helger.collection.commons.ICommonsMap;
import com.helger.ddd.model.jaxb.SyntaxListMarshaller;
import com.helger.ddd.model.jaxb.syntax1.SyntaxType;
import com.helger.ddd.model.jaxb.syntax1.SyntaxesType;
import com.helger.io.resource.ClassPathResource;
import com.helger.io.resource.IReadableResource;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * This class manages a set of {@link DDDSyntax} objects
 *
 * @author Philip Helger
 */
public class DDDSyntaxList
{
  private static final Logger LOGGER = LoggerFactory.getLogger (DDDSyntaxList.class);

  /**
   * The resource that contains the default syntax list. Part of this JAR file.
   */
  public static final IReadableResource DEFAULT_SYNTAX_LIST_RES = new ClassPathResource ("ddd/syntaxes.xml",
                                                                                         DDDSyntaxList.class.getClassLoader ());

  private static class SingletonHolder
  {
    /**
     * Singleton accessor.
     */
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

  /**
   * @return The last modification of the syntax list.
   */
  @Nonnull
  public final LocalDate getLastModification ()
  {
    return m_aLastMod;
  }

  /**
   * @return A map with all contained syntaxes. Key is the syntax ID and value
   *         is the {@link DDDSyntax} object. Never <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  public final ICommonsMap <String, DDDSyntax> getAllSyntaxes ()
  {
    return m_aSyntaxes.getClone ();
  }

  /**
   * Find a syntax by ID
   *
   * @param sSyntaxID
   *        The syntax ID to search. May be <code>null</code>.
   * @return <code>null</code> if no such syntax exists.
   */
  @Nullable
  public DDDSyntax getSyntaxOfID (@Nullable final String sSyntaxID)
  {
    return m_aSyntaxes.get (sSyntaxID);
  }

  /**
   * Find a matching syntax based on an XML document root element namespace URI
   * and local name.
   *
   * @param aRootElement
   *        The root element of an XML document. May not be <code>null</code>.
   * @return <code>null</code> if no matching syntax was found.
   */
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

  @Override
  public String toString ()
  {
    return new ToStringGenerator (null).append ("LastModification", m_aLastMod)
                                       .append ("Syntaxes", m_aSyntaxes)
                                       .getToString ();
  }

  /**
   * Create a new {@link DDDSyntaxList} object from the internal XML
   * representation. This is primarily used to read the
   * {@link #DEFAULT_SYNTAX_LIST_RES} into memory.
   *
   * @param aRes
   *        The resource to read. Must not be <code>null</code>.
   * @return The created {@link DDDSyntaxList} and never <code>null</code>.
   * @throws IllegalArgumentException
   *         If the XML does not match the required layout
   * @throws IllegalStateException
   *         If the XML is inconsistent
   */
  @Nonnull
  public static DDDSyntaxList readFromXML (@Nonnull final IReadableResource aRes)
  {
    ValueEnforcer.notNull (aRes, "Resource");

    LOGGER.info ("Reading DDDSyntaxList from '" + aRes.getPath () + "'");

    final SyntaxesType aJaxbSyntaxes = new SyntaxListMarshaller ().read (aRes);
    if (aJaxbSyntaxes == null)
      throw new IllegalArgumentException ("Failed to read DDD syntax list");

    return createFromJaxb (aJaxbSyntaxes);
  }

  /**
   * Create a new {@link DDDSyntaxList} object from the internal Jaxb
   * representation. This is primarily used to read the
   * {@link #DEFAULT_SYNTAX_LIST_RES} into memory.
   *
   * @param aJaxbSyntaxes
   *        The JAXB object to read. Must not be <code>null</code>.
   * @return The created {@link DDDSyntaxList} and never <code>null</code>.
   * @throws IllegalStateException
   *         If the XML is inconsistent
   */
  @Nonnull
  public static DDDSyntaxList createFromJaxb (@Nonnull final SyntaxesType aJaxbSyntaxes)
  {
    ValueEnforcer.notNull (aJaxbSyntaxes, "JaxbSyntaxes");

    final LocalDate aLastMod = aJaxbSyntaxes.getLastmod ().toLocalDate ();

    final ICommonsMap <String, DDDSyntax> aSyntaxes = new CommonsHashMap <> ();
    for (final SyntaxType aJaxbSyntax : aJaxbSyntaxes.getSyntax ())
    {
      final DDDSyntax aSyntax = DDDSyntax.createFromJaxb (aJaxbSyntax);
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

  /**
   * Merge the two syntax lists into a single one. Each syntax must only be
   * contained in one list to be mergable - otherwise an exception is thrown.
   *
   * @param aSL1
   *        First syntax list. May not be <code>null</code>.
   * @param aSL2
   *        Second syntax list. May not be <code>null</code>.
   * @return The new merged syntax list. Never <code>null</code>.
   * @throws IllegalArgumentException
   *         if the syntaxes are not distinct.
   */
  @Nonnull
  public static DDDSyntaxList createMergedSyntaxList (@Nonnull final DDDSyntaxList aSL1,
                                                      @Nonnull final DDDSyntaxList aSL2)
  {
    ValueEnforcer.notNull (aSL1, "SyntaxList1");
    ValueEnforcer.notNull (aSL2, "SyntaxList2");

    // find latest last modification
    final LocalDate aLastMod = aSL1.getLastModification ()
                                   .isAfter (aSL2.getLastModification ()) ? aSL1.getLastModification ()
                                                                          : aSL2.getLastModification ();

    final ICommonsMap <String, DDDSyntax> aMergedMap = new CommonsHashMap <> ();
    for (final Map.Entry <String, DDDSyntax> aEntry1 : aSL1.m_aSyntaxes.entrySet ())
    {
      final String sKey = aEntry1.getKey ();
      if (aSL2.m_aSyntaxes.containsKey (sKey))
        throw new IllegalArgumentException ("The syntax '" +
                                            sKey +
                                            "' is contained in both syntax lists - cannot merge this");
      aMergedMap.put (sKey, aEntry1.getValue ());
    }
    // Must be distinct
    aMergedMap.putAll (aSL2.m_aSyntaxes);

    return new DDDSyntaxList (aLastMod, aMergedMap);
  }
}
