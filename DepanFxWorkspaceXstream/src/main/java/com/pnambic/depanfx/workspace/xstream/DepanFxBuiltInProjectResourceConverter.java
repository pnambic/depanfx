package com.pnambic.depanfx.workspace.xstream;

import com.pnambic.depanfx.persistence.AbstractObjectXmlConverter;
import com.pnambic.depanfx.workspace.DepanFxProjectResource;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

import java.io.File;

public class DepanFxBuiltInProjectResourceConverter
    extends AbstractObjectXmlConverter<DepanFxProjectResource.BuiltIn> {

  private static final Class<?>[] ALLOW_TYPES = new Class[] {
      DepanFxProjectResource.BuiltIn.class
  };

  public static final String BUILT_IN = "built-in";

  public DepanFxBuiltInProjectResourceConverter() {
  }

  @Override
  public Class<?> forType() {
    return DepanFxProjectResource.BuiltIn.class;
  }

  @Override
  public Class<?>[] getAllowTypes() {
    return ALLOW_TYPES;
  }

  @Override
  public String getTag() {
    return BUILT_IN;
  }

  @Override
  public void marshal(Object source, HierarchicalStreamWriter writer,
      MarshallingContext context, Mapper mapper) {
    DepanFxProjectResource.BuiltIn projRsrc =
        (DepanFxProjectResource.BuiltIn) source;

    String builtInName = projRsrc.getBuiltInPath().toString();
    marshalValue(builtInName, context);
  }

  @Override
  public DepanFxProjectResource.BuiltIn unmarshal(
      HierarchicalStreamReader reader,
      UnmarshallingContext context,
      Mapper mapper) {

    String builtInPath = (String) unmarshalValue(context, String.class);
    return new DepanFxProjectResource.BuiltIn(
        new File((String) builtInPath).toPath());
  }
}
