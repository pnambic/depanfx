package com.pnambic.depanfx.nodeview.persistence;

import com.pnambic.depanfx.graph.model.GraphEdge;
import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.graph_doc.docdata.NodeInfoBlock;
import com.pnambic.depanfx.graph_doc.persistence.GraphEdgeConverter;
import com.pnambic.depanfx.graph_doc.persistence.GraphNodeConverter;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeDisplayData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeLocationData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeViewData;
import com.pnambic.depanfx.persistence.PersistDocumentTransportBuilder;
import com.pnambic.depanfx.persistence.plugins.DocumentPersistenceContribution;
import com.pnambic.depanfx.persistence.plugins.GraphNodePersistencePluginRegistry;
import com.pnambic.depanfx.workspace.DepanFxProjectResource;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DepanFxNodeViewPersistenceContribution
    implements DocumentPersistenceContribution {

  public static final String EXTENSION =
      DepanFxNodeViewData.NODE_VIEW_TOOL_EXT;

  private static final String NODE_LOCATION_TAG = "node-location";

  private static final String NODE_DISPLAY_TAG = "node-display";

  private final GraphNodePersistencePluginRegistry graphNodeRegistry;

  @Autowired
  public DepanFxNodeViewPersistenceContribution(
      GraphNodePersistencePluginRegistry graphNodeRegistry) {
    this.graphNodeRegistry = graphNodeRegistry;
  }

  @Override
  public boolean acceptsDocument(Object document) {
    return DepanFxNodeViewData.class.isAssignableFrom(document.getClass());
  }

  @Override
  public boolean acceptsExt(String extText) {
    return EXTENSION.equalsIgnoreCase(extText);
  }

  @Override
  public void prepareTransport(PersistDocumentTransportBuilder builder) {
    builder.addConverter(new DepanFxNodeViewDataConverter());
    NodeInfoBlock.prepareTransport(builder);
    builder.addAlias(NODE_LOCATION_TAG, DepanFxNodeLocationData.class);
    builder.addAlias(NODE_DISPLAY_TAG, DepanFxNodeDisplayData.class);

    builder.addConverter(new GraphEdgeConverter());
    builder.addConverter(new GraphNodeConverter());

    // Apply plugins for document elements.
    graphNodeRegistry.applyExtensions(builder, GraphNode.class);
    graphNodeRegistry.applyExtensions(builder, GraphEdge.class);
    graphNodeRegistry.applyExtensions(builder, DepanFxProjectResource.class);
    graphNodeRegistry.applyExtensions(builder, DepanFxWorkspaceResource.class);
  }
}
