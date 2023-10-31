package com.pnambic.depanfx.workspace.xstream;

import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
import com.pnambic.depanfx.workspace.DepanFxProjectMember;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import java.nio.file.Path;
import java.util.Optional;

/**
 * Very lightweight class to moderate between in memory workspace
 * resources and something that can be written to XML. * 
 */
public class XstreamWorkspaceResource {

  public String projectName;

  public String resourcePath;

  public XstreamWorkspaceResource(String projectName, String resourcePath) {
    this.projectName = projectName;
    this.resourcePath = resourcePath;
  }

  public static XstreamWorkspaceResource of(
      DepanFxWorkspaceResource wkspRsrc) {
    DepanFxProjectDocument projectDoc = wkspRsrc.getDocument();
    Path rsrcPath = projectDoc.getMemberPath();

    DepanFxProjectMember rsrcProj = projectDoc.getProject();
    Path projPath = rsrcProj.getMemberPath();
    Path pathValue = projPath.relativize(rsrcPath);
    String projectName = rsrcProj.getMemberName();
    String resourcePath = pathValue.toString();

    return new XstreamWorkspaceResource(projectName, resourcePath);
  }

  public static Optional<DepanFxWorkspaceResource> forWksp(
      DepanFxWorkspace workspace, XstreamWorkspaceResource xstreamWkspRsrc) {
    Optional<DepanFxProjectDocument> optProjDoc =
        workspace.toProjectDocument(
            xstreamWkspRsrc.projectName, xstreamWkspRsrc.resourcePath);
    return optProjDoc.flatMap(workspace::getWorkspaceResource);
  }
}
