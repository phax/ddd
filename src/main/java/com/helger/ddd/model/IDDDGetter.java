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

import org.w3c.dom.Node;

import com.helger.diagnostics.error.list.IErrorList;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

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
