package com.pnambic.depanfx.perspective.workspace.controls;

import com.pnambic.depanfx.workspace.DepanFxProjectBadMember;
import com.pnambic.depanfx.workspace.DepanFxProjectContainer;
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceMember;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

public class DepanFxProjectMemberItemBuilder {

  private final DepanFxProjectContainer member;

  public DepanFxProjectMemberItemBuilder(DepanFxProjectContainer member) {
    this.member = member;
  }

  public ObservableList<TreeItem<DepanFxWorkspaceMember>> buildChildren() {
    ObservableList<TreeItem<DepanFxWorkspaceMember>> result =
        FXCollections.observableArrayList();

    member.getMembers()
        .forEach(c -> result.add(createNode(c)));
    return result;
  }

  private TreeItem<DepanFxWorkspaceMember> createNode(
      DepanFxWorkspaceMember member) {
    switch (member) {
    case DepanFxProjectContainer container:
      return new DepanFxProjectContainerItem(container);
    case DepanFxProjectDocument document:
      return new DepanFxProjectDocumentItem(document);
    default:
      // fall-through
    }
    return new DepanFxProjectBadMemberItem((DepanFxProjectBadMember) member);
  }
}
