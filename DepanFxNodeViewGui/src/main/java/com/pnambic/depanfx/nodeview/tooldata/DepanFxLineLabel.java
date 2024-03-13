package com.pnambic.depanfx.nodeview.tooldata;

/**
 * Define the source for the line label.
 * 
 * Other label sources are feasible, such as using the underlying relation
 * name.
 */
public enum DepanFxLineLabel {
  /* No label for line*/
  NONE,

  /* Use the label */
  LABEL;

  public static final DepanFxLineLabel DEFAULT = NONE;
}
