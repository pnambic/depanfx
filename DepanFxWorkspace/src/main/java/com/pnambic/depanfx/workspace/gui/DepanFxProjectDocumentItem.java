package com.pnambic.depanfx.workspace.gui;

import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceMember;

import javafx.scene.control.TreeItem;

public class DepanFxProjectDocumentItem extends TreeItem<DepanFxWorkspaceMember> {

  public DepanFxProjectDocumentItem(DepanFxProjectDocument document) {
    super(document);
  }
}
