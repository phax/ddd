package com.helger.ddd.model;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.w3c.dom.Element;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.collection.impl.ICommonsList;

/**
 * This class manages a set of {@link DDDSyntax} objects
 *
 * @author Philip Helger
 */
public class DDDSyntaxList
{
  private final ICommonsList <DDDSyntax> m_aSyntaxes;

  public DDDSyntaxList (@Nonnull final ICommonsList <DDDSyntax> aSyntaxes)
  {
    ValueEnforcer.notNullNoNullValue (aSyntaxes, "Syntaxes");
    m_aSyntaxes = aSyntaxes;
  }

  @Nullable
  public DDDSyntax findMatchingSyntax (@Nonnull final Element aRootElement)
  {
    ValueEnforcer.notNull (aRootElement, "RootElement");

    final String sNamespaceURI = aRootElement.getNamespaceURI ();
    final String sLocalName = aRootElement.getLocalName ();

    for (final DDDSyntax aSyntax : m_aSyntaxes)
      if (aSyntax.matches (sNamespaceURI, sLocalName))
        return aSyntax;

    return null;
  }
}
