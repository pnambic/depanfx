package com.pnambic.depanfx.graph_doc.persistence;

import com.pnambic.depanfx.graph.context.GraphContextKeys;
import com.pnambic.depanfx.graph.model.GraphRelation;
import com.pnambic.depanfx.graph_doc.model.GraphContextDocument;
import com.pnambic.depanfx.persistence.BasePersistObjectConverter;
import com.pnambic.depanfx.persistence.PersistMarshalContext;
import com.pnambic.depanfx.persistence.PersistUnmarshalContext;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.projects.DepanFxBuiltInContribution;
import com.pnambic.depanfx.workspace.projects.DepanFxProjects;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GraphRelationConverter
    extends BasePersistObjectConverter<GraphRelation> {

  private static final Class<?>[] ALLOW_TYPES = new Class[] {
    GraphRelation.class
  };

  public static final String GRAPH_RELATION_TAG = "relation";

  private Map<String, GraphRelation> relationMap;

  public GraphRelationConverter() {
  }

  @Override
  public Class<?> forType() {
    return GraphRelation.class;
  }

  @Override
  public Class<?>[] getAllowTypes() {
    return ALLOW_TYPES;
  }

  @Override
  public String getTag() {
    return GRAPH_RELATION_TAG;
  }

  @Override
  public void marshal(PersistMarshalContext dstContext, Object source) {
    GraphRelation relation = (GraphRelation) source;
    marshalValue(dstContext, GraphContextKeys.toRelationKey(relation));
  }

  @Override
  public GraphRelation unmarshal(PersistUnmarshalContext srcContext) {
    String value = srcContext.getValue();
    DepanFxWorkspace workspace =
        (DepanFxWorkspace) srcContext.getContextValue(DepanFxWorkspace.class);
    return getRelationByKey(workspace, value);
  }

  private GraphRelation getRelationByKey(
      DepanFxWorkspace workspace, String relationKey) {
    if (relationMap  == null) {
      relationMap = buildRelationMap(workspace);
    }
    return relationMap.get(relationKey);
  }

  private Map<String, GraphRelation> buildRelationMap(
      DepanFxWorkspace workspace) {

    return DepanFxProjects
        .streamBuiltIns(workspace, GraphContextDocument.class)
        .map(DepanFxBuiltInContribution::getDocument)
        .map(GraphContextDocument.class::cast)
        .flatMap(d -> d.getGraphContext().getRelations().stream())
        .collect(Collectors.toMap(
            GraphContextKeys::toRelationKey, Function.identity()));
  }
}
