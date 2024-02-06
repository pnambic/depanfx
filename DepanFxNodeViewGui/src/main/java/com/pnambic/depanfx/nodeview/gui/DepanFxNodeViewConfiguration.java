package com.pnambic.depanfx.nodeview.gui;

import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeLists;
import com.pnambic.depanfx.perspective.plugins.DepanFxAnalysisExtMenuContribution;
import com.pnambic.depanfx.scene.DepanFxContextMenuBuilder;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxSceneController;
import com.pnambic.depanfx.workspace.DepanFxProjectMember;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceFactory;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceMember;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.util.Optional;

import javafx.scene.control.Cell;

@Configuration
public class DepanFxNodeViewConfiguration {

  private static final String OPEN_AS_VIEW = "Open as Node View";

  @Autowired
  public DepanFxNodeViewConfiguration() {
  }

  @Bean
  public DepanFxAnalysisExtMenuContribution nodeViewExtMenu() {
    return new JoglContribution();
  }

  private static class JoglContribution
      implements DepanFxAnalysisExtMenuContribution {

    private static final Logger LOG =
        LoggerFactory.getLogger(DepanFxNodeViewConfiguration.class);

    @Override
    public boolean acceptsExt(String ext) {
      return DepanFxNodeList.NODE_LIST_EXT.equals(ext);
    }

    @Override
    public void prepareCell(
        DepanFxSceneController scene,
        DepanFxDialogRunner dialogRunner, DepanFxWorkspace workspace,
        Cell<DepanFxWorkspaceMember> cell, String ext,
        DepanFxProjectMember member, DepanFxContextMenuBuilder builder) {
      Path docPath = member.getMemberPath();
      builder.appendActionItem(
          OPEN_AS_VIEW,
          e -> runOpenNodeListAction(scene, dialogRunner, workspace, docPath));
    }

    private void runOpenNodeListAction(
        DepanFxSceneController scene,
        DepanFxDialogRunner dialogRunner,
        DepanFxWorkspace workspace,
        Path docPath) {
      try {
        Optional<DepanFxWorkspaceResource> optWkspRsrc =
            workspace.toProjectDocument(docPath.toUri())
                .flatMap(r -> workspace.getWorkspaceResource(
                      r, DepanFxNodeList.class));
        optWkspRsrc.map(r -> r.getResource())
            .map(DepanFxNodeList.class::cast)
            .ifPresent(nl -> {
              String title = DepanFxWorkspaceFactory.buildDocTitle(
                  optWkspRsrc.get().getDocument()) + " View";
              addNodeViewPanelToScene(scene, dialogRunner, workspace, nl, title);
            });
      } catch (RuntimeException errCaught) {
        LOG.error("Unable to open node view for {}",
            docPath.toUri(), errCaught);
      }
    }

    private void addNodeViewPanelToScene(DepanFxSceneController scene,
        DepanFxDialogRunner dialogRunner, DepanFxWorkspace workspace,
        DepanFxNodeList nodeList, String title) {
      DepanFxNodeViewPanel nodeView =
          new DepanFxNodeViewPanel(workspace, dialogRunner, nodeList);
      scene.addTab(nodeView.createWorkspaceTab(title));
    }
  }
}
