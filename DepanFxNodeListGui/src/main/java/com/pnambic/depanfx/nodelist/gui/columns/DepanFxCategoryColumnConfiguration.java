package com.pnambic.depanfx.nodelist.gui.columns;

import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListTableAdapter;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxCategoryColumnData;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxBaseColumnData;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeListColumnData;
import com.pnambic.depanfx.persistence.PersistDocumentTransportBuilder;
import com.pnambic.depanfx.persistence.plugins.DocumentPersistenceContribution;
import com.pnambic.depanfx.persistence.plugins.GraphNodePersistencePluginRegistry;
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
public class DepanFxCategoryColumnConfiguration {

  private static final String CATEGORY_LABEL = "Category";

  private static final String CATEGORY_KEY = "Category";

  private static final String CATEGORY_COLUMN_LABEL = "Category Column";

  private static final String CATEGORY_COLUMN_KEY = "Category Column";

  public static final String EXTENSION =
      DepanFxCategoryColumnData.CATEGORY_COLUMN_TOOL_EXT;

  @Bean
  public DepanFxColumnRegistry.Contribution categoryColumnContribution() {
    return new CategoryColumnContribution();
  }

  @Bean
  public DepanFxResourceRegistry.Contribution
      categoryColumnFileOpenContribution() {

    return new CategoryColumnFileOpenContribution();
  }

  @Bean
  public DepanFxResourcePathMenuContribution categoryColumnPathMenu() {
    return new CategoryColumnPathContribution();
  }

  @Bean
  public DocumentPersistenceContribution categoryColumnPersistenceContribution(
      GraphNodePersistencePluginRegistry graphNodeRegistry) {
    return new CategoryColumnPersistenceContribution(graphNodeRegistry);
  }

  private static class CategoryColumnContribution
      extends DepanFxColumnRegistry.Basic {

    private CategoryColumnContribution() {
      super(
          CATEGORY_LABEL,
          DepanFxCategoryColumnData.class,
          CATEGORY_KEY,
          DepanFxCategoryColumnToolDialog.CATEGORY_COLUMN__RSRC_FILTER
          );
    }

    @Override
    public DepanFxNodeListColumn toColumn(
        DepanFxNodeListTableAdapter tableAdapter,
        DepanFxWorkspaceResource<?> columnRsrc) {
      @SuppressWarnings("unchecked")
      DepanFxWorkspaceResource<DepanFxCategoryColumnData> catRsrc =
          (DepanFxWorkspaceResource<DepanFxCategoryColumnData>) columnRsrc;
      return new DepanFxCategoryColumn(tableAdapter, catRsrc);
    }

    @Override
    public Optional<DepanFxWorkspaceResource<? extends DepanFxBaseColumnData>> getNewColumn(
        Event event,
        DepanFxWorkspace workspace,
        DepanFxDialogRunner dialogRunner,
        DepanFxNodeListTableAdapter tableAdapter) {
      DepanFxCategoryColumnData columnData =
          DepanFxCategoryColumnData.buildInitialCategoryColumnData();
      DepanFxWorkspaceResource<DepanFxCategoryColumnData> columnRsrc =
          workspace.addScratchResource(columnData);

      return DepanFxCategoryColumnToolDialog.runCreateDialog(
          columnRsrc, dialogRunner, tableAdapter)
          .getController()
          .getToolResource()
          .map(r -> r);
    }
  }

  private static class CategoryColumnFileOpenContribution
      extends DepanFxResourceRegistry.Principal<DepanFxCategoryColumnData> {

    private CategoryColumnFileOpenContribution() {
      super(
          CATEGORY_COLUMN_LABEL,
          DepanFxCategoryColumnData.class,
          DepanFxCategoryColumnData.CATEGORY_COLUMN_TOOL_EXT,
          CATEGORY_COLUMN_KEY);
    }

    @Override
    protected void runDialog(
        DepanFxDialogRunner dialogRunner,
        DepanFxWorkspaceResource<DepanFxCategoryColumnData> columnRsrc) {

      DepanFxCategoryColumnToolDialog.runEditDialog(
          columnRsrc, dialogRunner, null);
    }
  }

  private static class CategoryColumnPathContribution
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
      DepanFxCategoryColumn.addNewColumnAction(builder, dialogRunner, null);
    }

    @Override
    public String getOrderKey() {
      return CATEGORY_COLUMN_KEY;
    }
  }

  private static class CategoryColumnPersistenceContribution
      implements DocumentPersistenceContribution {

    public static final String CATEGORY_COLUMN_INFO_TAG =
        "category-column-info";

    public static final String CATEGORY_ENTRY_TAG = "category-entry";

    private static final Class<?>[] ALLOW_TYPES = new Class[] {
        DepanFxCategoryColumnData.class,
        DepanFxCategoryColumnData.CategoryEntry.class
    };

    private final GraphNodePersistencePluginRegistry graphNodeRegistry;

    public CategoryColumnPersistenceContribution(
        GraphNodePersistencePluginRegistry graphNodeRegistry) {
      this.graphNodeRegistry = graphNodeRegistry;
    }

    @Override
    public boolean acceptsDocument(Object document) {
      return DepanFxCategoryColumnData.class.isAssignableFrom(
          document.getClass());
    }

    @Override
    public boolean acceptsExt(String extText) {
      return EXTENSION.equalsIgnoreCase(extText);
    }

    @Override
    public void prepareTransport(PersistDocumentTransportBuilder builder) {
      builder.addAlias(
          CATEGORY_COLUMN_INFO_TAG, DepanFxCategoryColumnData.class);
      builder.addAlias(
          CATEGORY_ENTRY_TAG, DepanFxCategoryColumnData.CategoryEntry.class);

      builder.addAllowedType(ALLOW_TYPES);
      graphNodeRegistry.applyExtensions(
          builder, DepanFxWorkspaceResource.class);
    }
  }

}
