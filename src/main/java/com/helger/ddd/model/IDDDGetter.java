package com.helger.ddd.model;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.w3c.dom.Node;

import com.helger.commons.error.list.IErrorList;

/**
 * Get a value from an XML node based on certain rules.
 *
 * @author Philip Helger
 */
public interface IDDDGetter
{
  /**
   * Get a value based on a source node
   *
   * @param aSourceNode
   *        The source node to start.
   * @param aErrorList
   *        The error list to be filled in case something goes wrong
   * @return <code>null</code> if no value was found-
   */
  @Nullable
  String getValue (@Nonnull Node aSourceNode, @Nonnull IErrorList aErrorList);
}
