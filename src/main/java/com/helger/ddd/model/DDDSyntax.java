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

import java.util.Map;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.w3c.dom.Node;

import com.helger.annotation.Nonempty;
import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.id.IHasID;
import com.helger.base.name.IHasName;
import com.helger.base.string.StringHelper;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.CommonsHashMap;
import com.helger.collection.commons.ICommonsList;
import com.helger.collection.commons.ICommonsMap;
import com.helger.ddd.model.jaxb.syntax1.GetType;
import com.helger.ddd.model.jaxb.syntax1.SyntaxType;
import com.helger.diagnostics.error.list.IErrorList;

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

  public DDDSyntax (@NonNull @Nonempty final String sID,
                    @NonNull @Nonempty final String sRootElementNamespaceURI,
                    @NonNull @Nonempty final String sRootElementLocalName,
                    @NonNull @Nonempty final String sName,
                    @Nullable final String sVersion,
                    @NonNull @Nonempty final ICommonsMap <EDDDSourceField, ICommonsList <IDDDGetter>> aGetters)
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
  @NonNull
  @Nonempty
  public final String getID ()
  {
    return m_sID;
  }

  @NonNull
  @Nonempty
  public final String getRootElementNamespaceURI ()
  {
    return m_sRootElementNamespaceURI;
  }

  @NonNull
  @Nonempty
  public final String getRootElementLocalName ()
  {
    return m_sRootElementLocalName;
  }

  public boolean matches (@Nullable final String sNamespaceURI, @Nullable final String sLocalName)
  {
    return m_sRootElementNamespaceURI.equals (sNamespaceURI) && m_sRootElementLocalName.equals (sLocalName);
  }

  @NonNull
  @Nonempty
  public final String getName ()
  {
    return m_sName;
  }

  /**
   * @return <code>true</code> if a specific syntax version is used, <code>false</code> if not.
   */
  public final boolean hasVersion ()
  {
    return StringHelper.isNotEmpty (m_sVersion);
  }

  /**
   * @return The syntax version to be used. May be <code>null</code>.
   */
  @Nullable
  public final String getVersion ()
  {
    return m_sVersion;
  }

  @NonNull
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
  public String getValue (@NonNull final EDDDSourceField eGetter,
                          @NonNull final Node aSourceNode,
                          @NonNull final IErrorList aErrorList)
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
        {
          // Trim the result to avoid leading/trailing whitespace
          return ret.trim ();
        }
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

  @NonNull
  public static DDDSyntax createFromJaxb (@NonNull final SyntaxType aSyntax)
  {
    final String sSyntaxID = aSyntax.getId ();
    final String sLogPrefix = "Syntax with ID '" + sSyntaxID + "': ";

    // Name
    final String sName = aSyntax.getName ().trim ();
    if (StringHelper.isEmpty (sName))
      throw new IllegalArgumentException (sLogPrefix + "Element 'name' may not be empty");

    // Version (optional)
    final String sVersion = aSyntax.getVersion ();

    // Getters
    final ICommonsMap <EDDDSourceField, ICommonsList <IDDDGetter>> aGetters = new CommonsHashMap <> ();
    for (final GetType aGet : aSyntax.getGet ())
    {
      // Resolve source field
      final String sFieldID = aGet.getId ();
      final EDDDSourceField eGetter = EDDDSourceField.getFromIDOrNull (sFieldID);
      if (eGetter == null)
        throw new IllegalArgumentException (sLogPrefix + "The getter ID field '" + sFieldID + "' is invalid");

      // Build XPath getters
      final ICommonsList <IDDDGetter> aGetterList = new CommonsArrayList <> ();
      for (String sXPath : aGet.getXpath ())
        aGetterList.add (new DDDGetterXPath (sXPath.trim ()));
      if (aGetterList.isEmpty ())
        throw new IllegalArgumentException (sLogPrefix + "The getter '" + sFieldID + "' contains no actual getter");

      aGetters.put (eGetter, aGetterList);
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
                          aSyntax.getNsuri ().trim (),
                          aSyntax.getRoot ().trim (),
                          sName,
                          sVersion == null ? null : sVersion.trim (),
                          aGetters);
  }
}
