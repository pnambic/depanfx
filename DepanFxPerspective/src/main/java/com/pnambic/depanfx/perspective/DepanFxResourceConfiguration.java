package com.pnambic.depanfx.perspective;

import com.pnambic.depanfx.perspective.plugins.DepanFxResourcePathMenuContribution;
import com.pnambic.depanfx.scene.DepanFxContextMenuBuilder;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.plugins.DepanFxNewResourceRegistry;
import com.pnambic.depanfx.workspace.DepanFxProjectMember;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceMember;
import com.pnambic.depanfx.workspace.projects.DepanFxProjects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;

import javafx.scene.control.Cell;
import javafx.scene.control.Menu;

@Configuration
public class DepanFxResourceConfiguration {

  private final DepanFxNewResourceRegistry newResourceRegistry;

  @Autowired
  public DepanFxResourceConfiguration(
      DepanFxNewResourceRegistry newResourceRegistry) {
    this.newResourceRegistry = newResourceRegistry;
  }

  @Bean
  public DepanFxResourcePathMenuContribution graphPathMenu() {
    return new GraphsPathContribution(newResourceRegistry);
  }

  @Bean
  public DepanFxResourcePathMenuContribution analysisPathMenu() {
    return new AnalysisPathContribution(newResourceRegistry);
  }

  private static class AnalysisPathContribution
    implements DepanFxResourcePathMenuContribution {

    private static final String NEW_THEORY_MENU = "New Theory";

    private final DepanFxNewResourceRegistry newResourceRegistry;

    public AnalysisPathContribution(
        DepanFxNewResourceRegistry newResourceRegistry) {
      this.newResourceRegistry = newResourceRegistry;
    }

    @Override
    public boolean acceptsPath(Path rsrcPath) {
      return DepanFxProjects.ANALYSES_PATH.equals(rsrcPath);
    }

    @Override
    public void prepareCell(DepanFxDialogRunner dialogRunner,
        DepanFxWorkspace workspace, Cell<DepanFxWorkspaceMember> cell,
        DepanFxProjectMember member, DepanFxContextMenuBuilder builder) {
      builder.appendSubMenu(analysisContextMenu());
    }

    private Menu analysisContextMenu() {
      Menu newGraphMenu = new Menu(NEW_THEORY_MENU);
      newGraphMenu.getItems().addAll(newResourceRegistry.buildNewAnalysisItems());
      return newGraphMenu;
    }
  }

  private static class GraphsPathContribution
    implements DepanFxResourcePathMenuContribution {

    private static final String NEW_GRAPH_MENU = "New Graph";

    private final DepanFxNewResourceRegistry newResourceRegistry;

    public GraphsPathContribution(
        DepanFxNewResourceRegistry newResourceRegistry) {
      this.newResourceRegistry = newResourceRegistry;
    }

    @Override
    public boolean acceptsPath(Path rsrcPath) {
      return DepanFxProjects.GRAPHS_PATH.equals(rsrcPath);
    }

    @Override
    public void prepareCell(DepanFxDialogRunner dialogRunner,
        DepanFxWorkspace workspace, Cell<DepanFxWorkspaceMember> cell,
        DepanFxProjectMember member, DepanFxContextMenuBuilder builder) {
      builder.appendSubMenu(graphsContextMenu());
    }

    private Menu graphsContextMenu() {
      Menu newGraphMenu = new Menu(NEW_GRAPH_MENU);
      newGraphMenu.getItems().addAll(newResourceRegistry.buildNewResourceItems());
      return newGraphMenu;
    }
  }
}
