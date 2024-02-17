package com.pnambic.depanfx.persistence;

import com.pnambic.depanfx.persistence.xstream.PersistXstreamObjectConverter;
import com.pnambic.depanfx.xstream.XstreamUnmarshalContext;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class PersistTagDataLoader {

  public static class TagDescriptor {

    private final String dataTag;

    private final Class<?> dataType;

    public TagDescriptor(String dataTag, Class<?> dataType) {
      this.dataTag = dataTag;
      this.dataType = dataType;
    }

    public String getDataTag() {
      return dataTag;
    }

    public Class<?> getDataType() {
      return dataType;
    }
  }

  private final Map<String, TagDescriptor> tagDescrips;

  private final Map<String, String> tagAliases;

  public PersistTagDataLoader(
      TagDescriptor[] tagsDescrs, Map<String, String> tagAliases) {

    this.tagDescrips = Arrays.asList(tagsDescrs).stream()
        .collect(Collectors.toMap(TagDescriptor::getDataTag, d -> d));
    this.tagAliases = tagAliases;
  }

  public Map<String, Object> loadData(
      String[] metaTags, PersistUnmarshalContext srcContext) {

    XstreamUnmarshalContext.PeekableReader peekable =
        ((PersistXstreamObjectConverter.PersistUnmarshalWrapper) srcContext)
        .getXstreamUnmarshal().getPeekableReader();
    Set<String> expectedTags = new HashSet<>(Arrays.asList(metaTags));
    Map<String, Object> result = new HashMap<>();

    while (peekable.hasMoreChildren()) {
      if (expectedTags.isEmpty()) {
        return result;
      }
      if (getDescriptor(peekable.peekNextChild()) == null) {
        return result;
      }
      srcContext.moveDown();
      TagDescriptor descr = getDescriptor(srcContext.getNodeName());
      Object loadValue = unmarshalValue(srcContext, descr.getDataType());
      srcContext.moveUp();

      result.put(descr.getDataTag(), loadValue);
      expectedTags.remove(descr.getDataTag());
    }
    return result;
  }

  private TagDescriptor getDescriptor(String tagName) {
    String loadName = tagAliases.getOrDefault(tagName, tagName);
    return tagDescrips.get(loadName);
  }

  private Object unmarshalValue(
      PersistUnmarshalContext srcContext, Class<?> childClass) {
    return childClass.cast(srcContext.convertAnother(null, childClass));
  }
}
