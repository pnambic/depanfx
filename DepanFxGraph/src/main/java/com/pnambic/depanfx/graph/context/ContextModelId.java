package com.pnambic.depanfx.graph.context;

/**
 * Each graph package extends this create a globally unique
 * identifier for the package.
 */
public interface ContextModelId {

  String getContextModelKey();

  /**
   *  For lookup of built-in resources.
   */
  String getContextModelPath();
}
