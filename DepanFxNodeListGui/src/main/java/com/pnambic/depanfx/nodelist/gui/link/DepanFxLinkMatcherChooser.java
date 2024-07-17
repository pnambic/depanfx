package com.pnambic.depanfx.nodelist.gui.link;

import com.pnambic.depanfx.nodelist.tooldata.DepanFxLinkMatcherDocument;
import com.pnambic.depanfx.perspective.DepanFxResourcePerspectives;
import com.pnambic.depanfx.perspective.chooser.DepanFxResourceChooser;
import com.pnambic.depanfx.perspective.chooser.DepanFxResourceFilter;
import com.pnambic.depanfx.scene.DepanFxContextMenuBuilder;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;
import com.pnambic.depanfx.workspace.projects.DepanFxProjects;

import java.util.Optional;
import java.util.function.BiConsumer;

import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TableCell;
import javafx.scene.control.TextField;

public class DepanFxLinkMatcherChooser {

  public static final String SELECT_LINK_MATCHER = "Select Link Matcher...";

  public static final DepanFxResourceFilter LINK_MATCHER_FILTER =
      DepanFxResourceFilter.buildResourceFilter(
          "Link Matcher",
          DepanFxLinkMatcherDocument.LINK_MATCHER_TOOL_EXT,
          DepanFxLinkMatcherDocument.class);

  /**
   * Bind a pop-up to text field for the resource name.
   */
  public static class LinkMatcherControl {

    public final DepanFxWorkspace workspace;

    private final DepanFxDialogRunner dialogRunner;

    private DepanFxWorkspaceResource<DepanFxLinkMatcherDocument>
        linkMatcherRsrc;

    private final TextField linkMatcherField;

    public LinkMatcherControl(
        DepanFxWorkspace workspace,
        DepanFxDialogRunner dialogRunner,
        TextField linkMatcherField) {
      this.workspace = workspace;
      this.dialogRunner = dialogRunner;
      this.linkMatcherField = linkMatcherField;
      linkMatcherField.setContextMenu(buildContextMenu());
    }

    public void setLinkMatcherRsrc(
        DepanFxWorkspaceResource<DepanFxLinkMatcherDocument> linkMatcherRsrc) {
      this.linkMatcherRsrc = linkMatcherRsrc;
      linkMatcherField.setText(getLinkMatcherRsrcName());
    }

    public DepanFxWorkspaceResource<DepanFxLinkMatcherDocument>
        getLinkMatcherResource() {

      return linkMatcherRsrc;
    }

    public String getLinkMatcherRsrcName() {
      if (linkMatcherRsrc != null) {
        return linkMatcherRsrc.getDocument().getMemberPath().toString();
      }
      // Let the text input field show a prompt text.
      return null;
    }

    private ContextMenu buildContextMenu() {
      DepanFxContextMenuBuilder builder = new DepanFxContextMenuBuilder();
      builder.appendActionItem(
          SELECT_LINK_MATCHER, e -> runLinkMatcherFinder());
      return builder.build();
    }

    private void runLinkMatcherFinder() {
      DepanFxLinkMatcherChooser
          .runLinkMatcherFinder(
                workspace, dialogRunner, linkMatcherField.getScene())
          .ifPresent(this::setLinkMatcherRsrc);
    }
  }

  /**
   * Bind a pop-up to table cell for the resource name.
   */
  public static class LinkMatcherCell<T>
      extends TableCell<T, String> {

    private final DepanFxWorkspace workspace;

    private final DepanFxDialogRunner dialogRunner;

    private final Scene scene;

    private final BiConsumer<
            T, DepanFxWorkspaceResource<DepanFxLinkMatcherDocument>>
        matcherConsumer;

    public LinkMatcherCell(
        DepanFxWorkspace workspace,
        DepanFxDialogRunner dialogRunner,
        Scene scene,
        BiConsumer<
                T, DepanFxWorkspaceResource<DepanFxLinkMatcherDocument>>
            matcherConsumer) {
      this.workspace = workspace;
      this.dialogRunner = dialogRunner;
      this.scene = scene;
      this.matcherConsumer = matcherConsumer;
    }

    @Override
    protected void updateItem(String displayName, boolean empty) {
      super.updateItem(displayName, empty);

      if (empty) {
        setGraphic(null);
        setContextMenu(null);
        return;
      }
      setText(displayName);
      setContextMenu(buildContextMenu());
    }

    private ContextMenu buildContextMenu() {
      DepanFxContextMenuBuilder builder = new DepanFxContextMenuBuilder();
      builder.appendActionItem(
          SELECT_LINK_MATCHER, e -> runLinkMatcherFinder());
      return builder.build();
    }

    private void runLinkMatcherFinder() {
      DepanFxLinkMatcherChooser
          .runLinkMatcherFinder(workspace, dialogRunner, scene)
          .ifPresent(r -> matcherConsumer.accept(getTableRow().getItem(), r));
    }
  }

  /**
   * Provide an existing link matcher.
   */
  public static Optional<DepanFxWorkspaceResource<DepanFxLinkMatcherDocument>>
      runLinkMatcherFinder(
            DepanFxWorkspace workspace,
            DepanFxDialogRunner dialogRunner,
            Scene scene) {

    DepanFxResourceChooser chooser = prepareChooser(workspace, dialogRunner);
    return chooser.showOpenDialog(scene)
        .map(DepanFxProjectDocument.class::cast)
        .flatMap(p -> workspace.getWorkspaceResource(
            p, DepanFxLinkMatcherDocument.class));
  }

  private static DepanFxResourceChooser prepareChooser(
      DepanFxWorkspace workspace, DepanFxDialogRunner dialogRunner) {
    DepanFxResourceChooser result =
        new DepanFxResourceChooser(workspace, dialogRunner);
    DepanFxResourcePerspectives.prepareResourceFinder(
        result, DepanFxProjects.TOOLS_PATH);
    result.getExtensionFilters().add(LINK_MATCHER_FILTER);
    result.setSelectedExtensionFilter(LINK_MATCHER_FILTER);
    return result;
  }
}
