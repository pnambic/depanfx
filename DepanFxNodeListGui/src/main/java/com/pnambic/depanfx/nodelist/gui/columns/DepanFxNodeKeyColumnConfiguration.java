package com.pnambic.depanfx.nodelist.gui.columns;

import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListTableAdapter;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListTableState;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxNodeKeyColumnData;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxBaseColumnData;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeListColumnData;
import com.pnambic.depanfx.persistence.PersistDocumentTransportBuilder;
import com.pnambic.depanfx.persistence.plugins.DocumentPersistenceContribution;
import com.pnambic.depanfx.perspective.plugins.DepanFxResourcePathMenuContribution;
import com.pnambic.depanfx.perspective.plugins.DepanFxResourceRegistry;
import com.pnambic.depanfx.scene.DepanFxContextMenuBuilder;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.workspace.DepanFxProjectMember;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceMember;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.util.Optional;

import javafx.event.Event;
import javafx.scene.control.Cell;

@Configuration
public class DepanFxNodeKeyColumnConfiguration {

  public static final String NODE_KEY_LABEL = "Node Key";

  public static final String NODE_KEY_KEY = "Node Key";

  public static final String NODE_KEY_COLUMN_LABEL = "Node Key Column";

  public static final String NODE_KEY_COLUMN_KEY = "Node Key Column";

  public static final String EXTENSION =
      DepanFxNodeKeyColumnData.NODE_KEY_COLUMN_TOOL_EXT;

  @Bean
  public DepanFxColumnRegistry.Contribution
      nodeKeyColumnContribution() {
    return new NodeKeyColumnContribution();
  }

  @Bean
  public DepanFxResourceRegistry.Contribution nodeKeyColumnFileOpenMenu() {
    return new NodeKeyColumnFileOpenContribution();
  }

  @Bean
  public DepanFxResourcePathMenuContribution nodeKeyColumnPathMenu() {
    return new NodeKeyColumnPathContribution();
  }

  @Bean
  public DocumentPersistenceContribution nodeKeyColumnPersistenceContribution() {
    return new NodeKeyColumnPersistenceContribution();
  }

  private static class NodeKeyColumnContribution
      extends DepanFxColumnRegistry.Basic {
    private NodeKeyColumnContribution() {
      super(
          NODE_KEY_LABEL,
          DepanFxNodeKeyColumnData.class,
          NODE_KEY_KEY,
          DepanFxNodeKeyColumnToolDialog.NODE_KEY_COLUMN_RSRC_FILTER);
    }

    @Override
    public DepanFxNodeListColumn toColumn(
        DepanFxNodeListTableAdapter tableAdapter,
        DepanFxWorkspaceResource<?> columnRsrc) {
      @SuppressWarnings("unchecked")
      DepanFxWorkspaceResource<DepanFxNodeKeyColumnData> nodeKeyRsrc =
          (DepanFxWorkspaceResource<DepanFxNodeKeyColumnData>) columnRsrc;
      return new DepanFxNodeKeyColumn(tableAdapter, nodeKeyRsrc);
    }

    @Override
    public Optional<DepanFxWorkspaceResource<? extends DepanFxBaseColumnData>> getNewColumn(
        Event event, DepanFxWorkspace workspace,
        DepanFxDialogRunner dialogRunner,
        DepanFxNodeListTableAdapter tableAdapter,
        DepanFxNodeListTableState tableState) {
      DepanFxNodeKeyColumnData columnData =
          DepanFxNodeKeyColumn.buildInitialNodeKeyColumnData();
      DepanFxWorkspaceResource<DepanFxNodeKeyColumnData> columnRsrc =
          workspace.addScratchResource(columnData);

      return DepanFxNodeKeyColumnToolDialog.runCreateDialog(
          columnRsrc, dialogRunner)
          .getController()
          .getToolResource()
          .map(r -> r);
    }
  }

  private static class NodeKeyColumnFileOpenContribution
      extends DepanFxResourceRegistry.Principal<DepanFxNodeKeyColumnData> {

    public NodeKeyColumnFileOpenContribution() {
      super(
          NODE_KEY_COLUMN_LABEL,
          DepanFxNodeKeyColumnData.class,
          DepanFxNodeKeyColumnData.NODE_KEY_COLUMN_TOOL_EXT,
          NODE_KEY_COLUMN_KEY);
    }

    @Override
    protected void runDialog(
        DepanFxWorkspaceResource<DepanFxNodeKeyColumnData> wkspRsrc,
        DepanFxDialogRunner dialogRunner) {
      DepanFxNodeKeyColumnToolDialog.runEditDialog(wkspRsrc, dialogRunner);
    }
  }

  private static class NodeKeyColumnPathContribution
      implements DepanFxResourcePathMenuContribution {

    @Override
    public boolean acceptsPath(Path rsrcPath) {
      return DepanFxNodeListColumnData.COLUMNS_TOOL_PATH.equals(rsrcPath);
    }

    @Override
    public void prepareCell(
        DepanFxWorkspace workspace, DepanFxDialogRunner dialogRunner,
        Cell<DepanFxWorkspaceMember> cell,
        DepanFxProjectMember member, DepanFxContextMenuBuilder builder) {
      DepanFxNodeKeyColumn.addNewColumnAction(builder, workspace, dialogRunner);
    }

    @Override
    public String getOrderKey() {
      return NODE_KEY_COLUMN_KEY;
    }
  }

  private class NodeKeyColumnPersistenceContribution
      implements DocumentPersistenceContribution {

    public static final String NODE_KEY_COLUMN_INFO_TAG = "node-key-column-info";

    public static final String KEY_CHOICE_TAG = "key-choice-info";

    private static final Class<?>[] ALLOW_TYPES = new Class[] {
        DepanFxNodeKeyColumnData.class,
        DepanFxNodeKeyColumnData.KeyChoice.class
    };

    @Override
    public boolean acceptsDocument(Object document) {
      return DepanFxNodeKeyColumnData.class.isAssignableFrom(document.getClass());
    }

    @Override
    public boolean acceptsExt(String extText) {
      return EXTENSION.equalsIgnoreCase(extText);
    }

    @Override
    public void prepareTransport(PersistDocumentTransportBuilder builder) {
      builder.addAlias(NODE_KEY_COLUMN_INFO_TAG, DepanFxNodeKeyColumnData.class);
      builder.addAlias(
          KEY_CHOICE_TAG, DepanFxNodeKeyColumnData.KeyChoice.class);

      builder.addAllowedType(ALLOW_TYPES);
    }
  }
}
