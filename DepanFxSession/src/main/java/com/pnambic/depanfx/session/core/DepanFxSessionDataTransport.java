/*
 * Copyright 2024 The Depan Project Authors
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
package com.pnambic.depanfx.session.core;

import com.pnambic.depanfx.persistence.PersistDocumentTransport;
import com.pnambic.depanfx.persistence.PersistDocumentTransportBuilder;
import com.pnambic.depanfx.persistence.plugins.GraphNodePersistencePluginRegistry;
import com.pnambic.depanfx.scene.DepanFxSceneController;
import com.pnambic.depanfx.scene.DepanFxSceneControls;
import com.pnambic.depanfx.scene.DepanFxSceneViewer;
import com.pnambic.depanfx.scene.plugins.DepanFxSceneStarterRegistry;
import com.pnambic.depanfx.session.plugins.DepanFxSceneViewerRegistry;
import com.pnambic.depanfx.session.tooldata.DepanFxProjectData;
import com.pnambic.depanfx.session.tooldata.DepanFxSceneData;
import com.pnambic.depanfx.session.tooldata.DepanFxSessionData;
import com.pnambic.depanfx.session.viewdata.DepanFxBaseViewerData;
import com.pnambic.depanfx.workspace.DepanFxProjectTree;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceFactory;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;
import com.pnambic.depanfx.workspace.projects.DepanFxFileSystemProject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Window;

/**
 * Encapsulate knowledge about Session serialization and the structure
 * of basic sessions.
 */
@Component
public class DepanFxSessionDataTransport {

  private static final Logger LOG =
      LoggerFactory.getLogger(DepanFxSessionDataTransport.class);

  /** Base name for session data files */
  public static final String DEPAN_FX_SESSION_LABEL = "DepanFX Session";

  public static final String YAML_EXT = "yml";

  public static final ExtensionFilter YAML_FILTER =
      DepanFxSceneControls.buildExtFilter("Session", YAML_EXT);

  private static final Class<?>[] ALLOWED_TYPES = new Class[] {
    DepanFxSessionData.class,
    DepanFxProjectData.class, DepanFxSceneData.class };

  private final DepanFxWorkspace workspace;

  private final GraphNodePersistencePluginRegistry graphNodeRegistry;

  private DepanFxSceneViewerRegistry viewerRegistry;

  private final DepanFxSceneStarterRegistry starterRegistry;

  @Autowired
  public DepanFxSessionDataTransport(
      DepanFxWorkspace workspace,
      GraphNodePersistencePluginRegistry graphNodeRegistry,
      DepanFxSceneStarterRegistry starterRegistry,
      DepanFxSceneViewerRegistry viewerRegistry) {
    this.workspace = workspace;
    this.graphNodeRegistry = graphNodeRegistry;
    this.starterRegistry = starterRegistry;
    this.viewerRegistry = viewerRegistry;
  }

  public DepanFxSessionConfig defaultSessionConfig() {;
    List<DepanFxSceneViewer> defaultViewers =
        starterRegistry.getStarterViews();

    DepanFxSceneConfig sceneInfo = new DepanFxSceneConfig(
        "Initial Startup Scene",
        "Initial scene created at DepanFX startup.",
        -1.0d, -1.0d, -1.0d, -1.0d,
        defaultViewers);

    List<DepanFxSceneConfig> scenes = new ArrayList<>(1);
    scenes.add(sceneInfo);

    DepanFxSessionConfig result = new DepanFxSessionConfig(
        Collections.emptyList(), scenes);
    return result;
  }

  public DepanFxSessionConfig loadSessionConfig(Path sessionPath) {
    DepanFxSessionData sessionData = loadSessionData(sessionPath);

    List<DepanFxProjectTree> projectTrees =
        sessionData.getProjects().stream()
            .map(this::toProjectTree)
            .collect(Collectors.toList());

    Collection<DepanFxSceneConfig> sessionConfigs =
        sessionData.getScenes().stream()
            .map(this::toSceneConfig)
            .collect(Collectors.toList());

    return new DepanFxSessionConfig(projectTrees, sessionConfigs);
  }

  public void saveSession(Path sessionPath, DepanFxSession section) {
    PersistDocumentTransport transport = prepareTransport();
    DepanFxSessionData sectionInfo = toSessionData(section);

    try (Writer saver = openForSave(sessionPath)) {
      transport.save(saver, sectionInfo);
    } catch (Exception errAny) {
      throw new RuntimeException(
          "Unable to save section " + sectionInfo.getToolName(),
          errAny);
    }
  }

  public DepanFxSessionData loadSessionData(Path sessionPath) {
    PersistDocumentTransport transport = prepareTransport();

    try (Reader importer = openForLoad(sessionPath)) {
      return (DepanFxSessionData) transport.load(importer);
    } catch (IOException errIo) {
      throw new RuntimeException(
          "Unable to load session data at " + sessionPath.toString(), errIo);
    }
  }

  public void saveSessionData(
      Path sessionPath, DepanFxSessionData sectionInfo) {
    PersistDocumentTransport transport = prepareTransport();

    try (Writer saver = openForSave(sessionPath)) {
      transport.save(saver, sectionInfo);
    } catch (Exception errAny) {
      throw new RuntimeException(
          "Unable to save section " + sectionInfo.getToolName(),
          errAny);
    }
  }

  public DepanFxSessionData toSessionData(DepanFxSession session) {
    List<DepanFxProjectData> projectInfo = buildSessionProjects(session);
    Collection<DepanFxSceneData> sceneInfo = buildSessionScenes(session);
    return new DepanFxSessionData(
        "DepanFX Session", "DepanFX session.", projectInfo, sceneInfo);
  }

  private List<DepanFxProjectData> buildSessionProjects(
      DepanFxSession session) {
    DepanFxProjectTree builtInTree =
        session.getWorkspace().getBuiltInProjectTree();

    return session.getWorkspace().getProjectList().stream()
        .filter(p -> p != builtInTree)
        .map(this::buildProjectData)
        .collect(Collectors.toList());
  }

  private DepanFxProjectData buildProjectData(DepanFxProjectTree tree) {
    return new DepanFxProjectData(
      tree.getMemberName(), "DepanFX project.", tree.getMemberPath());
  }

  private Collection<DepanFxSceneData> buildSessionScenes(
      DepanFxSession session) {
    return session.getScenes().stream()
        .map(c -> buildSceneData(c))
        .collect(Collectors.toList());
  }

  private DepanFxSceneData buildSceneData(DepanFxSceneController scene) {
    List<DepanFxBaseViewerData> viewersInfo = scene.streamViewers()
        .flatMap(v -> viewerRegistry.getViewerData(v).stream())
        .collect(Collectors.toList());
    Window window = scene.getScene().getWindow();

    return new DepanFxSceneData("DepanFX", "DepanFX scene.",
        (int) window.getX(), (int) window.getY(),
        (int) window.getWidth(), (int) window.getHeight(),
        viewersInfo);
  }

  private PersistDocumentTransport prepareTransport() {
    PersistDocumentTransportBuilder transportBuilder =
        new PersistDocumentTransportBuilder();
    transportBuilder.addAllowedType(ALLOWED_TYPES);
    transportBuilder.addAlias("projectData", DepanFxProjectData.class);
    transportBuilder.addAlias("sceneData", DepanFxSceneData.class);
    transportBuilder.addConverter(new DepanFxProjectDataConverter());

    graphNodeRegistry.applyExtensions(
        transportBuilder, DepanFxWorkspaceResource.class);
    viewerRegistry.prepareTransport(transportBuilder);

    PersistDocumentTransport transport =
        transportBuilder.buildDocumentXmlPersist();

    transport.addContextValue(DepanFxWorkspace.class, workspace);
    return transport;
  }

  private FileReader openForLoad(Path sessionPath)
      throws FileNotFoundException {
    return new FileReader(sessionPath.toFile());
  }

  private Writer openForSave(Path sessionPath) throws IOException {
    return new FileWriter(sessionPath.toFile());
  }

  private DepanFxProjectTree toProjectTree(DepanFxProjectData projectInfo) {
    DepanFxFileSystemProject projectSpi = new DepanFxFileSystemProject(
        projectInfo.getToolName(), projectInfo.getProjectPath());
    return DepanFxWorkspaceFactory.createDepanFxProjectTree(projectSpi);
  }

  private DepanFxSceneConfig toSceneConfig(DepanFxSceneData sceneInfo) {
    sceneInfo.getViewers();
    List<DepanFxSceneViewer> viewers = sceneInfo.getViewers().stream()
        .flatMap(d -> toSceneViewer(d).stream())
        .collect(Collectors.toList());

    DepanFxSceneConfig result = new DepanFxSceneConfig(
        sceneInfo.getToolName(), sceneInfo.getToolDescription(),
        (double) sceneInfo.getTop(), (double) sceneInfo.getLeft(),
        (double) sceneInfo.getWidth(), (double) sceneInfo.getHeight(),
        viewers);
    return result;
  }

  private Optional<DepanFxSceneViewer> toSceneViewer(
      DepanFxBaseViewerData viewerInfo) {
    return viewerRegistry.buildViewer(viewerInfo);
  }
}
