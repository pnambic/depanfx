package com.pnambic.depanfx.nodelist.model;

import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.graph_doc.model.GraphDocument;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceFactory;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class DepanFxNodeLists {

  private DepanFxNodeLists() {
    // Prevent instantiation.
  }

  public static DepanFxNodeList buildNodeList(
      String nodeListName, String nodeListDescription,
      DepanFxWorkspaceResource graphDocRsrc, Collection<GraphNode> nodes) {

    return new DepanFxNodeList(
        nodeListName, nodeListDescription, graphDocRsrc, nodes);
  }

  public static DepanFxNodeList buildNodeList(
      DepanFxWorkspaceResource graphDocRsrc) {

    GraphDocument graphDoc = (GraphDocument) graphDocRsrc.getResource();
    Collection<GraphNode> nodes = graphDoc.getGraph().getNodes().stream()
        .map(GraphNode.class::cast)
        .collect(Collectors.toList());

    String baseName =
        DepanFxWorkspaceFactory.buildDocTitle(graphDocRsrc.getDocument());
    String resultName = MessageFormat.format("{0} nodes", baseName);
    String resultDescr = MessageFormat.format(
        "From graph {0} ({1} nodes).", baseName, nodes.size());
    return buildNodeList(resultName, resultDescr, graphDocRsrc, nodes);
  }

  public static String getNameFromGraphDoc(
      DepanFxWorkspaceResource graphDocRsrc) {
   String baseName = DepanFxWorkspaceFactory.buildDocTitle(
       graphDocRsrc.getDocument());
   return MessageFormat.format("{0} nodes", baseName);
 }

  public static DepanFxNodeList remove(
      DepanFxNodeList base, DepanFxNodeList omit) {
    Set<GraphNode> omitSet = new HashSet<>(omit.getNodes());

    Collection<GraphNode> nodes = base.getNodes().stream()
        .filter(n -> !omitSet.contains(n))
        .collect(Collectors.toList());

    String baseName = guessName(base);
    String omitName = guessName(omit);
    String resultName = MessageFormat.format(
        "{0} without {1}", baseName, omitName);
    String resultDescr = MessageFormat.format(
        "List {0} with items from {1} removed.", baseName, omitName);
    return buildNodeList(
        resultName, resultDescr, base.getGraphDocResource(), nodes);
  }

  public static DepanFxNodeList buildEmptyNodeList(DepanFxNodeList baseNodes) {

    String baseName = guessName(baseNodes);
    String resultName = MessageFormat.format("{0} emptied", baseName);
    String resultDescr = MessageFormat.format(
        "List {0} with all item removed.", baseName);

    return buildNodeList(
        resultName, resultDescr,
        baseNodes.getGraphDocResource(), Collections.emptyList());
  }

  public static DepanFxNodeList buildRelatedNodeList(
      DepanFxNodeList baseNodes, Collection<GraphNode> relatedList) {
    String baseName = guessName(baseNodes);
    String resultName = MessageFormat.format("{0} altered", baseName);
    String resultDescr = MessageFormat.format(
        "Node List based on {0}.", baseName);

    return buildNodeList(
        resultName, resultDescr,
        baseNodes.getGraphDocResource(), relatedList);
  }

  private static String guessName(DepanFxNodeList nodeList) {
    if (nodeList.getNodeListName() != null) {
      return nodeList.getNodeListName();
    }
    return getNameFromGraphDoc(nodeList.getGraphDocResource());
  }
}
