package com.pnambic.depanfx.workspace.xstream;

import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
import com.pnambic.depanfx.workspace.DepanFxProjectTree;
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
    DepanFxProjectTree rsrcProj = projectDoc.getProject();
    Path pathValue = rsrcProj.getRelativePath(projectDoc.getMemberPath());
    String projectName = rsrcProj.getMemberName();
    String resourcePath = pathValue.toString();

    return new XstreamWorkspaceResource(projectName, resourcePath);
  }

  public static Optional<DepanFxWorkspaceResource> forWksp(
      DepanFxWorkspace workspace, XstreamWorkspaceResource xstreamWkspRsrc) {
    Optional<DepanFxProjectDocument> optProjDoc =
        workspace.toProjectDocument(
            xstreamWkspRsrc.projectName, xstreamWkspRsrc.resourcePath);
    return optProjDoc.flatMap(d ->
        workspace.getWorkspaceResource(d, "XstreamWorkspaceResource"));
  }
}
