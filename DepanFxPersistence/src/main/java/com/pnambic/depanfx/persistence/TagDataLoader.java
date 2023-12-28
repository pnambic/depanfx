package com.pnambic.depanfx.persistence;

import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.xml.AbstractPullReader;
import com.thoughtworks.xstream.mapper.Mapper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class TagDataLoader {

  public static class DataDescriptor {

    private final String dataTag;

    private final Class<?> dataType;

    public DataDescriptor(String dataTag, Class<?> dataType) {
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

  private final Map<String, DataDescriptor> tagLoader;

  private final Map<String, String> tagAliases;

  public TagDataLoader(
      DataDescriptor[] tagLoader, Map<String, String> tagAliases) {
    this.tagLoader = Arrays.asList(tagLoader).stream()
        .collect(Collectors.toMap(DataDescriptor::getDataTag, d -> d));
    this.tagAliases = tagAliases;
  }

  public Map<String, Object> loadData(
      String[] metaTags,
      HierarchicalStreamReader reader,
      UnmarshallingContext context,
      Mapper mapper) {

    AbstractPullReader peekable =
        (AbstractPullReader) reader.underlyingReader();
    Set<String> expectedTags = new HashSet<>(Arrays.asList(metaTags));
    Map<String, Object> result = new HashMap<>();

    while (peekable.hasMoreChildren()) {
      if (expectedTags.isEmpty()) {
        return result;
      }
      if (getDescriptor(peekable.peekNextChild()) == null) {
        return result;
      }
      reader.moveDown();
      DataDescriptor descr = getDescriptor(reader.getNodeName());
      Object loadValue = unmarshalValue(context, descr.getDataType());
      reader.moveUp();

      result.put(descr.getDataTag(), loadValue);
      expectedTags.remove(descr.getDataTag());
    }
    return result;
  }

  private DataDescriptor getDescriptor(String tagName) {
    String loadName = tagAliases.getOrDefault(tagName, tagName);
    return tagLoader.get(loadName);
  }

  private Object unmarshalValue(UnmarshallingContext context, Class<?> childClass) {
    return childClass.cast(context.convertAnother(null, childClass));
  }
}
