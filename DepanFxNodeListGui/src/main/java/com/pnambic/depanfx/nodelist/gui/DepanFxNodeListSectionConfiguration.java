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
package com.pnambic.depanfx.nodelist.gui;

import com.pnambic.depanfx.nodelist.gui.sections.DepanFxFlatSection;
import com.pnambic.depanfx.nodelist.gui.sections.DepanFxFlatSectionToolDialog;
import com.pnambic.depanfx.nodelist.gui.sections.DepanFxTreeSection;
import com.pnambic.depanfx.nodelist.gui.sections.DepanFxTreeSectionToolDialog;
import com.pnambic.depanfx.nodelist.link.DepanFxLinkMatcherBuiltIns;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxFlatSectionData;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxLinkMatcherDocument;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeListSectionData;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeListSectionData.OrderBy;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeListSectionData.OrderDirection;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxTreeSectionData;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxTreeSectionData.ContainerOrder;
import com.pnambic.depanfx.perspective.plugins.DepanFxResourceExtMenuContribution;
import com.pnambic.depanfx.perspective.plugins.DepanFxResourcePathMenuContribution;
import com.pnambic.depanfx.scene.DepanFxContextMenuBuilder;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
import com.pnambic.depanfx.workspace.DepanFxProjectMember;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
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
public class DepanFxNodeListSectionConfiguration {

  public static final String FLAT_SECTION_KEY = "Flat Section";

  public static final String TREE_SECTION_KEY = "Tree Section";

  @Autowired
  public DepanFxNodeListSectionConfiguration() {
  }

  @Bean
  public DepanFxResourceExtMenuContribution flatSectionExtMenu() {
    return new FlatSectionExtContribution();
  }

  @Bean
  public DepanFxResourcePathMenuContribution flatSectionPathMenu() {
    return new FlatSectionPathContribution();
  }

  @Bean
  public DepanFxResourceExtMenuContribution treeSectionExtMenu() {
    return new TreeSectionExtContribution();
  }

  @Bean
  public DepanFxResourcePathMenuContribution treeSectionPathMenu() {
    return new TreeSectionPathContribution();
  }

  /////////////////////////////////////
  // Flat section contributions

  private static class FlatSectionExtContribution
      extends DepanFxResourceExtMenuContribution.Basic<DepanFxFlatSectionData> {

    public FlatSectionExtContribution() {
      super(DepanFxFlatSectionData.class, FLAT_SECTION_KEY,
          DepanFxFlatSection.EDIT_FLAT_SECTION_DATA,
          DepanFxFlatSectionData.FLAT_SECTION_TOOL_EXT);
    }

    @Override
    protected void runDialog(
        DepanFxWorkspaceResource<DepanFxFlatSectionData> wkspRsrc,
        DepanFxDialogRunner dialogRunner) {
      DepanFxFlatSectionToolDialog.runEditDialog(wkspRsrc, dialogRunner);
    }
  }

  private static class FlatSectionPathContribution
      implements DepanFxResourcePathMenuContribution {

    private static final String NEW_FLAT_SECTION_NAME = "New Flat Section";

    private static final String NEW_FLAT_SECTION_DESCR = "New flat section.";

    private static Logger LOG =
        LoggerFactory.getLogger(TreeSectionPathContribution.class);

    @Override
    public boolean acceptsPath(Path rsrcPath) {
      return DepanFxNodeListSectionData.SECTIONS_TOOL_PATH.equals(rsrcPath);
    }

    @Override
    public void prepareCell(
        DepanFxWorkspace workspace, DepanFxDialogRunner dialogRunner,
        Cell<DepanFxWorkspaceMember> cell,
        DepanFxProjectMember member, DepanFxContextMenuBuilder builder) {
      builder.appendActionItem(
          DepanFxFlatSection.NEW_FLAT_SECTION_DATA,
          e -> runNewFlatSectionDataAction(workspace, dialogRunner));
    }

    private void runNewFlatSectionDataAction(
        DepanFxWorkspace workspace, DepanFxDialogRunner dialogRunner) {
      try {
        DepanFxFlatSectionData sectionData =
            new DepanFxFlatSectionData(
                NEW_FLAT_SECTION_NAME, NEW_FLAT_SECTION_DESCR,
                DepanFxFlatSectionData.BASE_SECTION_LABEL, true,
                OrderBy.NODE_KEY, OrderDirection.FORWARD);

        DepanFxWorkspaceResource<DepanFxFlatSectionData> sectionRsrc =
            workspace.addScratchResource(sectionData);
        DepanFxFlatSectionToolDialog.runCreateDialog(
            sectionRsrc, dialogRunner);
      } catch (RuntimeException errCaught) {
        LOG.error("Unable to create flat section data", errCaught);
      }
    }

    @Override
    public String getOrderKey() {
      return FLAT_SECTION_KEY;
    }
  }

  /////////////////////////////////////
  // Tree section GUI contributions

  private static class TreeSectionExtContribution
      extends DepanFxResourceExtMenuContribution.Basic<DepanFxTreeSectionData> {

    public TreeSectionExtContribution() {
      super(DepanFxTreeSectionData.class, TREE_SECTION_KEY,
          DepanFxTreeSection.EDIT_TREE_SECTION_DATA,
          DepanFxTreeSectionData.TREE_SECTION_TOOL_EXT);
    }

    @Override
    protected void runDialog(
        DepanFxWorkspaceResource<DepanFxTreeSectionData> wkspRsrc,
        DepanFxDialogRunner dialogRunner) {
      DepanFxTreeSectionToolDialog.runEditDialog(wkspRsrc, dialogRunner);
    }
  }

  private static class TreeSectionPathContribution
      implements DepanFxResourcePathMenuContribution {

    private static final String NEW_TREE_SECTION_NAME = "New Tree Section";

    private static final String NEW_TREE_SECTION_DESCR = "New tree section.";

    private static final String NEW_TREE_SECTION_LABEL = "Tree";

    private static Logger LOG =
        LoggerFactory.getLogger(TreeSectionPathContribution.class);

    @Override
    public boolean acceptsPath(Path rsrcPath) {
      return DepanFxNodeListSectionData.SECTIONS_TOOL_PATH.equals(rsrcPath);
    }

    @Override
    public void prepareCell(
        DepanFxWorkspace workspace, DepanFxDialogRunner dialogRunner,
        Cell<DepanFxWorkspaceMember> cell,
        DepanFxProjectMember member, DepanFxContextMenuBuilder builder) {
      builder.appendActionItem(
          DepanFxTreeSection.NEW_TREE_SECTION_DATA,
          e -> runNewTreeSectionDataAction(workspace, dialogRunner));
    }

    private void runNewTreeSectionDataAction(
        DepanFxWorkspace workspace, DepanFxDialogRunner dialogRunner) {
      try {
        DepanFxWorkspaceResource<DepanFxTreeSectionData> sectionRsrc =
            workspace.addScratchResource(buildInitialTreeSection(workspace));
        DepanFxTreeSectionToolDialog.runCreateDialog(sectionRsrc, dialogRunner);
      } catch (RuntimeException errCaught) {
        LOG.error("Unable to create tree section data", errCaught);
      }
    }

    private DepanFxTreeSectionData buildInitialTreeSection(
        DepanFxWorkspace workspace) {
      Optional<DepanFxProjectDocument> matcherProjPath =
          workspace.getBuiltInProjectTree().asProjectDocument(
              DepanFxLinkMatcherBuiltIns.MEMBER_MATCHER_PATH);
      return  workspace.getWorkspaceResource(
          matcherProjPath.get(), DepanFxLinkMatcherDocument.class)
          .map(m -> new DepanFxTreeSectionData(
                NEW_TREE_SECTION_NAME, NEW_TREE_SECTION_DESCR,
                NEW_TREE_SECTION_LABEL, true, m, true,
                OrderBy.NODE_LEAF, ContainerOrder.LAST, OrderDirection.FORWARD))
          .get();
    }

    @Override
    public String getOrderKey() {
      return TREE_SECTION_KEY;
    }
  }
}
