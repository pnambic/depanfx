package com.pnambic.depanfx.workspace.gui;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import com.pnambic.depanfx.workspace.DepanFxProjectContainer;
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
import com.pnambic.depanfx.workspace.DepanFxProjectMember;
import com.pnambic.depanfx.workspace.DepanFxProjectTree;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceMember;
import com.pnambic.depanfx.workspace.basic.BasicDepanFxProjectBadMember;
import com.pnambic.depanfx.workspace.basic.BasicDepanFxProjectContainer;
import com.pnambic.depanfx.workspace.basic.BasicDepanFxProjectDocument;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

public class DepanFxProjectMemberItemBuilder {

  private final DepanFxProjectMember member;

  public DepanFxProjectMemberItemBuilder(DepanFxProjectMember member) {
    this.member = member;
  }

  public ObservableList<TreeItem<DepanFxWorkspaceMember>> buildChildren() {

    Path filePath = member.getMemberPath();

    if (Files.isDirectory(filePath)) {
      return buildChildren(filePath);
    }

    return FXCollections.emptyObservableList();
  }

  private ObservableList<TreeItem<DepanFxWorkspaceMember>> buildChildren(Path filePath) {
    ObservableList<TreeItem<DepanFxWorkspaceMember>> result =
        FXCollections.observableArrayList();

        try (Stream<Path> paths = Files.list(filePath)) {
      paths.forEach(childPath -> result.add(createNode(childPath)));
      return result;
    } catch (IOException e) {
      // TODO: report exception
    }

    BasicDepanFxProjectBadMember badMember = new BasicDepanFxProjectBadMember(getProject(), filePath);
    result.add(new DepanFxProjectBadMemberItem(badMember));
    return result;
  }

  private TreeItem<DepanFxWorkspaceMember> createNode(Path childPath) {
    if (Files.isDirectory(childPath)) {
      DepanFxProjectContainer container =
          new BasicDepanFxProjectContainer(getProject(), childPath);
      return new DepanFxProjectContainerItem(container);
    }
    if (Files.isRegularFile(childPath)) {
      DepanFxProjectDocument document =
          new BasicDepanFxProjectDocument(getProject(), childPath);
      return new DepanFxProjectDocumentItem(document);
    }
    BasicDepanFxProjectBadMember badMember = new BasicDepanFxProjectBadMember(getProject(), childPath);
    return new DepanFxProjectBadMemberItem(badMember);
  }

  private DepanFxProjectTree getProject() {
    return member.getProject();
  }
}
