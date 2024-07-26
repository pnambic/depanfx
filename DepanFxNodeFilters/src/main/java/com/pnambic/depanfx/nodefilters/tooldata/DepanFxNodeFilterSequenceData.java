package com.pnambic.depanfx.nodefilters.tooldata;

import com.pnambic.depanfx.graph.context.ContextModelId;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;
import com.pnambic.depanfx.workspace.tooldata.DepanFxBaseToolData;

import java.util.List;
import java.util.stream.Stream;

public class DepanFxNodeFilterSequenceData extends DepanFxBaseToolData {

  public static final String NODE_FILTER_SEQUENCE_TOOL_EXT = "dnfsti";

  private final ContextModelId contextModelId;

  private final List<DepanFxWorkspaceResource<DepanFxBaseFilterData>> filterRefs;

  public DepanFxNodeFilterSequenceData(
      String toolName, String toolDescription,
      ContextModelId contextModelId,
      List<DepanFxWorkspaceResource<DepanFxBaseFilterData>> filterRefs) {
    super(toolName, toolDescription);
    this.contextModelId = contextModelId;
    this.filterRefs = filterRefs;
  }

  public ContextModelId getContextModelId() {
    return contextModelId;
  }

  public Stream<DepanFxWorkspaceResource<DepanFxBaseFilterData>>
  streamFilterRefs() {
    return filterRefs.stream();
  }
}
