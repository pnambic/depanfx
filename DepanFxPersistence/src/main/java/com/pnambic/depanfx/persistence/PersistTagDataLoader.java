package com.pnambic.depanfx.persistence;

import com.pnambic.modxstream.XstreamUnmarshalContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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

  private static final Logger LOG =
      LoggerFactory.getLogger(PersistTagDataLoader.class);

  private final Map<String, TagDescriptor> tagDescrips;

  private final Map<String, String> tagAliases;

  public PersistTagDataLoader(
      TagDescriptor[] tagsDescrs, Map<String, String> tagAliases) {

    this.tagDescrips = Arrays.asList(tagsDescrs).stream()
        .collect(Collectors.toMap(TagDescriptor::getDataTag, d -> d));
    this.tagAliases = tagAliases;
  }

  public Collection<String> getTags() {
    return new ArrayList<>(tagDescrips.keySet());
  }

  public Map<String, Object> loadData(
      String[] metaTags, XstreamUnmarshalContext srcContext) {

    XstreamUnmarshalContext.PeekableReader peekable =
        srcContext.getPeekableReader();
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
      Object loadValue = unmarshalValue(srcContext, descr);
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

  /**
   * Uses lightweight null for bad values on an internal API.
   * Avoid extra Optional creation for normal case of valid values.
   * @return {@code null} if unmarshalling fails
   */
  private Object unmarshalValue(
      XstreamUnmarshalContext srcContext, TagDescriptor descr) {
    try {
      Class<?> childClass = descr.getDataType();
      return childClass.cast(srcContext.convertAnother(null, childClass));
    } catch (Exception errAny) {
      LOG.error("Failed to unmarshal value for tag {}",
          descr.getDataTag(), errAny);
    }
    return null;
  }
}
