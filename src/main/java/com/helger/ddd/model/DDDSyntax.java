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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.w3c.dom.Node;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.CommonsHashMap;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.collection.impl.ICommonsMap;
import com.helger.commons.error.list.IErrorList;
import com.helger.commons.id.IHasID;
import com.helger.commons.name.IHasName;
import com.helger.xml.microdom.IMicroElement;

/**
 * Defines a single supported syntax model
 *
 * @author Philip Helger
 */
public class DDDSyntax implements IHasID <String>, IHasName
{
  private final String m_sID;
  private final String m_sRootElementNamespaceURI;
  private final String m_sRootElementLocalName;
  private final String m_sName;
  private final ICommonsMap <EDDDGetterType, ICommonsList <IDDDGetter>> m_aGetters;

  public DDDSyntax (@Nonnull @Nonempty final String sID,
                    @Nonnull @Nonempty final String sRootElementNamespaceURI,
                    @Nonnull @Nonempty final String sRootElementLocalName,
                    @Nonnull @Nonempty final String sName,
                    @Nonnull @Nonempty final ICommonsMap <EDDDGetterType, ICommonsList <IDDDGetter>> aGetters)
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
    m_aGetters = aGetters;
  }

  @Nonnull
  @Nonempty
  public String getID ()
  {
    return m_sID;
  }

  @Nonnull
  @Nonempty
  public String getRootElementNamespaceURI ()
  {
    return m_sRootElementNamespaceURI;
  }

  @Nonnull
  @Nonempty
  public String getRootElementLocalName ()
  {
    return m_sRootElementLocalName;
  }

  public boolean matches (@Nullable final String sNamespaceURI, @Nullable final String sLocalName)
  {
    return m_sRootElementNamespaceURI.equals (sNamespaceURI) && m_sRootElementLocalName.equals (sLocalName);
  }

  @Nonnull
  @Nonempty
  public String getName ()
  {
    return m_sName;
  }

  @Nullable
  public String getValue (@Nonnull final EDDDGetterType eGetter,
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

  @Nonnull
  public static DDDSyntax readFromXML (@Nonnull final IMicroElement eSyntax)
  {
    // Name
    final IMicroElement eName = eSyntax.getFirstChildElement ("name");
    if (eName == null)
      throw new IllegalArgumentException ("Element 'name' is missing");

    // Getters
    final ICommonsMap <EDDDGetterType, ICommonsList <IDDDGetter>> aGetters = new CommonsHashMap <> ();
    for (final IMicroElement eGet : eSyntax.getAllChildElements ("get"))
    {
      // Check type
      final String sID = eGet.getAttributeValue ("id");
      final EDDDGetterType eGetterType = EDDDGetterType.getFromIDOrNull (sID);
      if (eGetterType == null)
        throw new IllegalArgumentException ("The getter ID value '" + sID + "' is invalid");

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
            throw new IllegalArgumentException ("The getter '" +
                                                sID +
                                                "' uses the unsupported type '" +
                                                sTagName +
                                                "'");
        }
      }
      if (aGetterList.isEmpty ())
        throw new IllegalArgumentException ("The getter '" + sID + "' contains no actual getter");
      aGetters.put (eGetterType, aGetterList);
    }

    return new DDDSyntax (eSyntax.getAttributeValue ("id"),
                          eSyntax.getAttributeValue ("nsuri"),
                          eSyntax.getAttributeValue ("root"),
                          eName.getTextContentTrimmed (),
                          aGetters);
  }
}
