package com.pnambic.depanfx.session.core;

import com.pnambic.depanfx.persistence.BasePersistObjectConverter;
import com.pnambic.depanfx.persistence.PersistMarshalContext;
import com.pnambic.depanfx.persistence.PersistTagDataLoader;
import com.pnambic.depanfx.persistence.PersistUnmarshalContext;
import com.pnambic.depanfx.session.tooldata.DepanFxProjectData;
import com.pnambic.depanfx.workspace.DepanFxProjectTree;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceFactory;
import com.pnambic.depanfx.workspace.projects.DepanFxFileSystemProject;

import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;

public class DepanFxProjectDataConverter
    extends BasePersistObjectConverter<DepanFxProjectData> {

  public static final String PROJECT_DATA_TAG = "project-data";

  private static final Class<?>[] ALLOWED_TYPES = new Class[] {
  };

  public static final String PROJECT_NAME_TAG = "project-name";

  public static final String PROJECT_DESCR_TAG = "project-descr";

  public static final String PROJECT_PATH_TAG = "project-path";

  private static final PersistTagDataLoader.TagDescriptor[] TAG_DATA_DESCR =
      new PersistTagDataLoader.TagDescriptor[] {
          new PersistTagDataLoader.TagDescriptor(
              PROJECT_NAME_TAG, String.class),
          new PersistTagDataLoader.TagDescriptor(
              PROJECT_DESCR_TAG, String.class),
          new PersistTagDataLoader.TagDescriptor(
              PROJECT_PATH_TAG, Path.class)
      };

  private static final String[] META_TAGS = new String[] {
      PROJECT_NAME_TAG, PROJECT_DESCR_TAG, PROJECT_PATH_TAG};

  private static final PersistTagDataLoader TAG_LOADER =
      new PersistTagDataLoader(TAG_DATA_DESCR, Collections.emptyMap());

  public DepanFxProjectDataConverter() {
    super();
  }

  @Override
  public Class<?> forType() {
    return DepanFxProjectData.class;
  }

  @Override
  public Class<?>[] getAllowTypes() {
    return ALLOWED_TYPES;
  }

  @Override
  public String getTag() {
    return PROJECT_DATA_TAG;
  }

  @Override
  public void marshal(PersistMarshalContext dstContext, Object source) {
    DepanFxProjectData projectData = (DepanFxProjectData) source;

    marshalObject(dstContext, PROJECT_NAME_TAG, projectData.getToolName());
    marshalObject(dstContext, PROJECT_DESCR_TAG, projectData.getToolDescription());
    marshalObject(dstContext, PROJECT_PATH_TAG, projectData.getProjectPath());
  }

  @Override
  public DepanFxProjectData unmarshal(PersistUnmarshalContext srcContext) {
    Map<String, Object> metaData =
        TAG_LOADER.loadData(META_TAGS, srcContext);

    String projectName = (String) metaData.get(PROJECT_NAME_TAG);
    String projectDescr = (String) metaData.get(PROJECT_DESCR_TAG);
    Path projectPath = (Path) metaData.get(PROJECT_PATH_TAG);

    DepanFxProjectData projectData =
        new DepanFxProjectData(projectName, projectDescr, projectPath);

    // Add it to the workspace immediately,
    // so it is available for scene viewer resources.
    DepanFxFileSystemProject projectSpi =
        new DepanFxFileSystemProject(projectName, projectPath);
    DepanFxProjectTree projectTree =
        DepanFxWorkspaceFactory.createDepanFxProjectTree(projectSpi);
    DepanFxWorkspace workspace =
        (DepanFxWorkspace) srcContext.getContextValue(DepanFxWorkspace.class);
    workspace.addProject(projectTree);

    return projectData;
  }
}
