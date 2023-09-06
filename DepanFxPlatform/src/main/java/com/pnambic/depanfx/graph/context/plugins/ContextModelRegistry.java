package com.pnambic.depanfx.graph.context.plugins;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pnambic.depanfx.graph.context.ContextModelId;
import com.pnambic.depanfx.graph.context.GraphContextKeys;
import com.pnambic.depanfx.graph.model.GraphContextModel;
import com.pnambic.depanfx.graph.model.GraphRelation;

@Component
public class ContextModelRegistry {

  public static final GraphContextModel EMPTY_CONTEXT_MODEL =
      new EmptyContextModel();

  private final List<GraphContextModel> contextModels;

  // Cached info
  private Map<String, GraphRelation> relationMap;

  @Autowired
  public ContextModelRegistry(List<GraphContextModel> contextModels) {
    this.contextModels = contextModels;
  }

  public GraphContextModel getContextModel(String modelKey) {
    Optional<GraphContextModel> result = contextModels.stream()
        .filter(cm -> cm.getId().getContextModelKey().equals(modelKey))
        .findFirst();
    return result.orElse(EMPTY_CONTEXT_MODEL);
  }

  public GraphContextModel getContextModel(ContextModelId modelId) {
    Optional<GraphContextModel> result = contextModels.stream()
        .filter(cm -> cm.getId().equals(modelId))
        .findFirst();
    return result.orElse(EMPTY_CONTEXT_MODEL);
  }

  public GraphRelation getRelationByKey(String relationKey) {
    if (relationMap == null) {
      relationMap = buildRelationMap();
    }
    return relationMap.get(relationKey);
  }

  private Map<String, GraphRelation> buildRelationMap() {
    return contextModels.stream()
        .flatMap(m -> m.getRelations().stream())
        .collect(Collectors.toMap(
            GraphContextKeys::toRelationKey, Function.identity()));
  }
}
