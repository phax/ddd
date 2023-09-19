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
package com.helger.ddd;

import java.io.IOException;
import java.io.UncheckedIOException;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.exception.InitializationException;
import com.helger.commons.io.resource.ClassPathResource;
import com.helger.commons.lang.NonBlockingProperties;

/**
 * DDD constants
 *
 * @author Philip Helger
 */
@Immutable
public final class DDDVersion
{
  public static final String DDD_VERSION_FILENAME = "ddd-version.properties";

  private static final String VERSION_NUMBER;
  private static final String TIMESTAMP;

  static
  {
    // Read version number
    final NonBlockingProperties aVersionProps = new NonBlockingProperties ();
    try
    {
      aVersionProps.load (ClassPathResource.getInputStream (DDD_VERSION_FILENAME));
    }
    catch (final IOException ex)
    {
      throw new UncheckedIOException (ex);
    }
    VERSION_NUMBER = aVersionProps.get ("version");
    if (VERSION_NUMBER == null)
      throw new InitializationException ("Error determining DDD version number!");
    TIMESTAMP = aVersionProps.get ("timestamp");
    if (TIMESTAMP == null)
      throw new InitializationException ("Error determining DDD build timestamp!");
  }

  private DDDVersion ()
  {}

  /**
   * @return The version number of DDD read from the internal properties file.
   *         Never <code>null</code>.
   */
  @Nonnull
  public static String getVersionNumber ()
  {
    return VERSION_NUMBER;
  }

  /**
   * @return The build timestamp of DDD read from the internal properties file.
   *         Never <code>null</code>.
   */
  @Nonnull
  public static String getBuildTimestamp ()
  {
    return TIMESTAMP;
  }
}
