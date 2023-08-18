package com.helger.ddd.model;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.w3c.dom.Node;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.collection.impl.ICommonsMap;
import com.helger.commons.error.list.IErrorList;
import com.helger.commons.name.IHasName;

/**
 * Defines a single supported syntax model
 *
 * @author Philip Helger
 */
public class DDDSyntax implements IHasName
{
  private final String m_sRootElementNamespaceURI;
  private final String m_sRootElementLocalName;
  private final String m_sName;
  private final ICommonsMap <EDDDGetterType, ICommonsList <IDDDGetter>> m_aGetters;

  public DDDSyntax (@Nonnull @Nonempty final String sRootElementNamespaceURI,
                    @Nonnull @Nonempty final String sRootElementLocalName,
                    @Nonnull @Nonempty final String sName,
                    @Nonnull @Nonempty final ICommonsMap <EDDDGetterType, ICommonsList <IDDDGetter>> aGetters)
  {
    ValueEnforcer.notEmpty (sRootElementNamespaceURI, "RootElementNamespaceURI");
    ValueEnforcer.notEmpty (sRootElementLocalName, "RootElementLocalName");
    ValueEnforcer.notEmpty (sName, "Name");
    ValueEnforcer.notEmptyNoNullValue (aGetters, "Getters");

    m_sRootElementNamespaceURI = sRootElementNamespaceURI;
    m_sRootElementLocalName = sRootElementLocalName;
    m_sName = sName;
    m_aGetters = aGetters;
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
}
