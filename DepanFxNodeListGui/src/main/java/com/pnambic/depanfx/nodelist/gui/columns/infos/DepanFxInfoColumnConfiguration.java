/*
 * Copyright 2025 The Depan Project Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pnambic.depanfx.nodelist.gui.columns.infos;

import com.pnambic.depanfx.graph.nodeinfo.DepanFxInfoRegistry;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListTableAdapter;
import com.pnambic.depanfx.nodelist.gui.columns.DepanFxColumnRegistry;
import com.pnambic.depanfx.nodelist.gui.columns.DepanFxNodeListColumn;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxNodeInfoColumnDataConverter;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxAnnotationStoreData;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxBaseColumnData;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxInfoStoreData;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeListColumnData;
import com.pnambic.depanfx.persistence.PersistDocumentTransportBuilder;
import com.pnambic.depanfx.persistence.plugins.DocumentPersistenceContribution;
import com.pnambic.depanfx.persistence.plugins.GraphNodePersistencePluginRegistry;
import com.pnambic.depanfx.perspective.plugins.DepanFxResourceRegistryContribution;
import com.pnambic.depanfx.perspective.plugins.DepanFxResourcePathMenuContribution;
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
public class DepanFxInfoColumnConfiguration {

  public static final String INFOS_LABEL = "Infos";

  public static final String INFOS_KEY = "Infos";

  public static final String INFOS_COLUMN_LABEL = "Infos Column";

  public static final String INFOS_COLUMN_KEY = "Infos Column";

  public static final String EXTENSION =
      DepanFxNodeInfoColumnData.NODE_INFO_COLUMN_TOOL_EXT;

  @Bean
  public DepanFxColumnRegistry.Contribution infoColumnContribution() {
    return new NodeInfoColumnContribution();
  }

  @Bean
  public DepanFxResourceRegistryContribution<DepanFxNodeInfoColumnData>
  infoColumnFileOpenMenu() {
    return new InfoColumnFileOpenContribution();
  }

  @Bean
  public DepanFxResourcePathMenuContribution infoColumnPathMenu() {
    return new InfoColumnPathContribution();
  }

  @Bean
  public DocumentPersistenceContribution infoColumnPersistenceContribution(
      GraphNodePersistencePluginRegistry graphNodeRegistry,
      DepanFxInfoRegistry infoRegistry) {
    return new InfoColumnPersistenceContribution(
        graphNodeRegistry, infoRegistry);
  }

  private static class NodeInfoColumnContribution
      extends DepanFxColumnRegistry.Basic {
    private NodeInfoColumnContribution() {
      super(
          INFOS_LABEL,
          DepanFxNodeInfoColumnData.class,
          INFOS_KEY,
          DepanFxInfoColumnToolDialog.INFO_COLUMN_RSRC_FILTER);
    }

    @Override
    public DepanFxNodeListColumn toColumn(
        DepanFxNodeListTableAdapter tableAdapter,
        DepanFxWorkspaceResource<?> columnRsrc) {
      @SuppressWarnings("unchecked")
      DepanFxWorkspaceResource<DepanFxNodeInfoColumnData> infoRsrc =
          (DepanFxWorkspaceResource<DepanFxNodeInfoColumnData>) columnRsrc;

      return getPropertyStore(infoRsrc.getResource(), tableAdapter)
          .map(ps -> new DepanFxInfoColumn(tableAdapter, infoRsrc, ps))
          .get();
    }

    @Override
    public Optional<DepanFxWorkspaceResource<? extends DepanFxBaseColumnData>> getNewColumn(
        Event event, DepanFxWorkspace workspace,
        DepanFxDialogRunner dialogRunner,
        DepanFxNodeListTableAdapter tableAdapter) {
      DepanFxNodeInfoColumnData columnData =
          DepanFxNodeInfoColumnData.buildInitialColumnData();
      DepanFxWorkspaceResource<DepanFxNodeInfoColumnData> columnRsrc =
          workspace.addScratchResource(columnData);

      return DepanFxInfoColumnToolDialog.runCreateDialog(
          columnRsrc, dialogRunner)
          .getController()
          .getToolResource()
          .map(r -> r);
    }

    private Optional<DepanFxInfoRegistry.PropertyStore> getPropertyStore(
        DepanFxNodeInfoColumnData infoColumn,
        DepanFxNodeListTableAdapter tableAdapter) {
      DepanFxInfoStoreData columnInfoStore =
          infoColumn.getInfoSourceResource().getResource();

      if (columnInfoStore instanceof DepanFxAnnotationStoreData annoStore) {
        return Optional.of(annoStore.getPropertyStore(infoColumn.getInfoKey()));
      }
      return infoColumn.getInfoContribution()
          .map(c -> c.getInfoStore(tableAdapter));
    }
  }

  private static class InfoColumnFileOpenContribution
      extends DepanFxResourceRegistryContribution.Principal<DepanFxNodeInfoColumnData>
      implements DepanFxResourceRegistryContribution.Dialog<DepanFxNodeInfoColumnData> {


    public InfoColumnFileOpenContribution() {
      super(
          INFOS_COLUMN_LABEL,
          DepanFxNodeInfoColumnData.class,
          DepanFxNodeInfoColumnData.NODE_INFO_COLUMN_TOOL_EXT,
          INFOS_COLUMN_KEY);
    }

    @Override
    public void runDialog(DepanFxWorkspace workspace,
        DepanFxDialogRunner dialogRunner,
        DepanFxWorkspaceResource<DepanFxNodeInfoColumnData> columnRsrc) {
      DepanFxInfoColumnToolDialog.runEditDialog(columnRsrc, dialogRunner);
    }
  }

  private static class InfoColumnPathContribution
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
      DepanFxInfoColumn.addNewColumnAction(builder, workspace, dialogRunner);
    }

    @Override
    public String getOrderKey() {
      return INFOS_COLUMN_KEY;
    }
  }

  private static class InfoColumnPersistenceContribution
      implements DocumentPersistenceContribution {

    private final GraphNodePersistencePluginRegistry graphNodeRegistry;

    private final DepanFxInfoRegistry infoRegistry;

    public InfoColumnPersistenceContribution(
        GraphNodePersistencePluginRegistry graphNodeRegistry,
        DepanFxInfoRegistry infoRegistry) {
      this.graphNodeRegistry = graphNodeRegistry;
      this.infoRegistry = infoRegistry;
    }

    @Override
    public boolean acceptsDocument(Object document) {
      return DepanFxNodeInfoColumnData.class.isAssignableFrom(document.getClass());
    }

    @Override
    public boolean acceptsExt(String extText) {
      return EXTENSION.equalsIgnoreCase(extText);
    }

    @Override
    public void prepareTransport(PersistDocumentTransportBuilder builder) {
      builder.addConverter(new DepanFxNodeInfoColumnDataConverter(infoRegistry));
      graphNodeRegistry.applyExtensions(
          builder, DepanFxWorkspaceResource.class);
    }
  }
}
