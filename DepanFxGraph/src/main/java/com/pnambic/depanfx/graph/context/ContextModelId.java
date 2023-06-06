package com.pnambic.depanfx.graph.context;

/**
 * Each graph package extends this create a globally unique
 * indentifier for the package.
 */
public interface ContextModelId {

  String getContextModelKey();
}
