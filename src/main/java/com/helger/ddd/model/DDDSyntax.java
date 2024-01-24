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

import java.util.Map;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.w3c.dom.Node;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.CommonsHashMap;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.collection.impl.ICommonsMap;
import com.helger.commons.error.list.IErrorList;
import com.helger.commons.id.IHasID;
import com.helger.commons.name.IHasName;
import com.helger.commons.string.StringHelper;
import com.helger.commons.string.ToStringGenerator;
import com.helger.xml.microdom.IMicroElement;

/**
 * Defines a single supported syntax model
 *
 * @author Philip Helger
 */
@Immutable
public class DDDSyntax implements IHasID <String>, IHasName
{
  private final String m_sID;
  private final String m_sRootElementNamespaceURI;
  private final String m_sRootElementLocalName;
  private final String m_sName;
  private final String m_sVersion;
  private final ICommonsMap <EDDDSourceField, ICommonsList <IDDDGetter>> m_aGetters;

  public DDDSyntax (@Nonnull @Nonempty final String sID,
                    @Nonnull @Nonempty final String sRootElementNamespaceURI,
                    @Nonnull @Nonempty final String sRootElementLocalName,
                    @Nonnull @Nonempty final String sName,
                    @Nullable final String sVersion,
                    @Nonnull @Nonempty final ICommonsMap <EDDDSourceField, ICommonsList <IDDDGetter>> aGetters)
  {
    ValueEnforcer.notEmpty (sID, "ID");
    ValueEnforcer.notEmpty (sRootElementNamespaceURI, "RootElementNamespaceURI");
    ValueEnforcer.notEmpty (sRootElementLocalName, "RootElementLocalName");
    ValueEnforcer.notEmpty (sName, "Name");
    ValueEnforcer.notEmptyNoNullValue (aGetters, "Getters");

    m_sID = sID;
    m_sRootElementNamespaceURI = sRootElementNamespaceURI;
    m_sRootElementLocalName = sRootElementLocalName;
    m_sName = sName;
    m_sVersion = sVersion;
    m_aGetters = aGetters;
  }

  /**
   * @return The ID of the syntax. Neither <code>null</code> nor empty.
   */
  @Nonnull
  @Nonempty
  public final String getID ()
  {
    return m_sID;
  }

  @Nonnull
  @Nonempty
  public final String getRootElementNamespaceURI ()
  {
    return m_sRootElementNamespaceURI;
  }

  @Nonnull
  @Nonempty
  public final String getRootElementLocalName ()
  {
    return m_sRootElementLocalName;
  }

  public boolean matches (@Nullable final String sNamespaceURI, @Nullable final String sLocalName)
  {
    return m_sRootElementNamespaceURI.equals (sNamespaceURI) && m_sRootElementLocalName.equals (sLocalName);
  }

  @Nonnull
  @Nonempty
  public final String getName ()
  {
    return m_sName;
  }

  /**
   * @return <code>true</code> if a specific syntax version is used,
   *         <code>false</code> if not.
   */
  public final boolean hasVersion ()
  {
    return StringHelper.hasText (m_sVersion);
  }

  /**
   * @return The syntax version to be used. May be <code>null</code>.
   */
  @Nullable
  public final String getVersion ()
  {
    return m_sVersion;
  }

  @Nonnull
  @Nonempty
  @ReturnsMutableCopy
  public final ICommonsMap <EDDDSourceField, ICommonsList <IDDDGetter>> getAllGetters ()
  {
    // Deep clone
    final ICommonsMap <EDDDSourceField, ICommonsList <IDDDGetter>> ret = new CommonsHashMap <> ();
    for (final Map.Entry <EDDDSourceField, ICommonsList <IDDDGetter>> e : m_aGetters.entrySet ())
      ret.put (e.getKey (), e.getValue ().getClone ());
    return ret;
  }

  @Nullable
  public String getValue (@Nonnull final EDDDSourceField eGetter,
                          @Nonnull final Node aSourceNode,
                          @Nonnull final IErrorList aErrorList)
  {
    ValueEnforcer.notNull (eGetter, "Getter");
    ValueEnforcer.notNull (aSourceNode, "SourceNode");
    ValueEnforcer.notNull (aErrorList, "ErrorList");

    final ICommonsList <IDDDGetter> aGetters = m_aGetters.get (eGetter);
    if (aGetters != null)
    {
      // Apply them all in order. First result is used
      for (final IDDDGetter aGetter : aGetters)
      {
        final String ret = aGetter.getValue (aSourceNode, aErrorList);
        if (ret != null)
          return ret;
      }
    }

    return null;
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (null).append ("ID", m_sID)
                                       .append ("RootElementNamespaceURI", m_sRootElementNamespaceURI)
                                       .append ("RootElementLocalName", m_sRootElementLocalName)
                                       .append ("Name", m_sName)
                                       .append ("Version", m_sVersion)
                                       .append ("Getters", m_aGetters)
                                       .getToString ();
  }

  @Nonnull
  public static DDDSyntax readFromXML (@Nonnull final IMicroElement eSyntax,
                                       @Nonnull final Function <String, ICommonsMap <EDDDSourceField, ICommonsList <IDDDGetter>>> aExternalGettersProvider)
  {
    final String sSyntaxID = eSyntax.getAttributeValue ("id");
    final String sLogPrefix = "Syntax with ID '" + sSyntaxID + "': ";

    // Name
    final IMicroElement eName = eSyntax.getFirstChildElement ("name");
    if (eName == null)
      throw new IllegalArgumentException (sLogPrefix + "Element 'name' is missing");

    // Version (optional)
    final IMicroElement eVersion = eSyntax.getFirstChildElement ("version");

    // Getters
    final ICommonsMap <EDDDSourceField, ICommonsList <IDDDGetter>> aGetters;
    final IMicroElement eGettersLike = eSyntax.getFirstChildElement ("getters-like");
    if (eGettersLike != null)
    {
      // Reuse other getters
      aGetters = aExternalGettersProvider.apply (eGettersLike.getTextContentTrimmed ());
      if (aGetters == null)
        throw new IllegalArgumentException (sLogPrefix +
                                            "The content of the element 'getters-like' is not a valid syntax ID");
    }
    else
    {
      aGetters = new CommonsHashMap <> ();
      for (final IMicroElement eGet : eSyntax.getAllChildElements ("get"))
      {
        // Check type
        final String sFieldID = eGet.getAttributeValue ("id");
        final EDDDSourceField eGetter = EDDDSourceField.getFromIDOrNull (sFieldID);
        if (eGetter == null)
          throw new IllegalArgumentException (sLogPrefix + "The getter ID field '" + sFieldID + "' is invalid");

        final ICommonsList <IDDDGetter> aGetterList = new CommonsArrayList <> ();
        for (final IMicroElement eChild : eGet.getAllChildElements ())
        {
          final String sTagName = eChild.getTagName ();
          switch (sTagName)
          {
            case "xpath":
              aGetterList.add (new DDDGetterXPath (eChild.getTextContentTrimmed ()));
              break;
            default:
              throw new IllegalArgumentException (sLogPrefix +
                                                  "The getter '" +
                                                  sFieldID +
                                                  "' uses the unsupported type '" +
                                                  sTagName +
                                                  "'");
          }
        }
        if (aGetterList.isEmpty ())
          throw new IllegalArgumentException (sLogPrefix + "The getter '" + sFieldID + "' contains no actual getter");
        aGetters.put (eGetter, aGetterList);
      }
    }

    // Check if all mandatory getters are present
    for (final EDDDSourceField eGetter : EDDDSourceField.values ())
      if (eGetter.isSyntaxDefinitionMandatory ())
        if (!aGetters.containsKey (eGetter))
          throw new IllegalArgumentException (sLogPrefix +
                                              "The mandatory getter '" +
                                              eGetter.getID () +
                                              "' is missing");

    return new DDDSyntax (sSyntaxID,
                          eSyntax.getAttributeValue ("nsuri"),
                          eSyntax.getAttributeValue ("root"),
                          eName.getTextContentTrimmed (),
                          eVersion == null ? null : eVersion.getTextContentTrimmed (),
                          aGetters);
  }
}
