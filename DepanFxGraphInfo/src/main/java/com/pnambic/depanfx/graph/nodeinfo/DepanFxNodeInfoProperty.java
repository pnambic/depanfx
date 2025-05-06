/*
 * Copyright 2025 The Depan Project Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pnambic.depanfx.graph.nodeinfo;

import com.pnambic.depanfx.base.tooldata.DepanFxBaseToolData;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DepanFxNodeInfoProperty extends DepanFxBaseToolData {

  private static final Logger LOG =
      LoggerFactory.getLogger(DepanFxNodeInfoProperty.class);

  public enum PropertyKind {
    INT {
      @Override
      public String toString(Object value) {
        if (value instanceof Number numValue) {
          int intValue = numValue.intValue();
          return Integer.toString(intValue);
        }
        return null;
      }

      @Override
      public String clean(String input) {
        try {
          int value = Integer.parseInt(input);
          return Integer.toString(value);
        } catch (NumberFormatException e) {
          LOG.info("Invalid integer value {}", input);
        }
        return null;
      }
    },
    STRING {
      @Override
      public String toString(Object value) {
        if (value instanceof String strValue) {
          return strValue;
        }
        if (value != null) {
          return value.toString();
        }
        return null;
      }

      @Override
      public String clean(String source) {
        return source;
      }
    },
    POS {
      @Override
      public String toString(Object value) {
        if (value instanceof Number numValue) {
          return formatPosition(numValue.doubleValue());
        }
        return null;
      }

      @Override
      public String clean(String input) {
        try {
          double value = Double.parseDouble(input);
          return formatPosition(value);
        } catch (NumberFormatException e) {
          LOG.info("Invalid integer value {}", input);
        }
        return null;
      }
    };

    public abstract String toString(Object value);

    /**
     * Return cannonical text from the source,
     * or {@code null} if it is invalid.
     */
    public abstract String clean(String input);
  }

  public static final String NEW_PROPERTY_NAME = "Property";

  public static final String NEW_PROPERTY_DESCR = "New property.";

  private final PropertyKind propertyKind;

  private final boolean isEditable;

  public DepanFxNodeInfoProperty(
      String toolName, String toolDescription,
      PropertyKind propertyKind, boolean isEditable) {
    super(toolName, toolDescription);
    this.propertyKind = propertyKind;
    this.isEditable = isEditable;
  }

  public static DepanFxNodeInfoProperty buildStringProperty(
      String toolName, String toolDescription) {
    return new DepanFxNodeInfoProperty(
        toolName, toolDescription,
        PropertyKind.STRING, false);
  }

  public static DepanFxNodeInfoProperty buildInitialColumnData() {
    return buildStringProperty(NEW_PROPERTY_NAME, NEW_PROPERTY_DESCR);
  }

  public static String formatPosition(double value) {
    return String.format("%.2f", value);
  }

  public PropertyKind getPropertyKind() {
    return propertyKind;
  }

  public boolean isEditable() {
    return isEditable;
  }
}
