package com.pnambic.depanfx.workspace.persistence;

import com.pnambic.depanfx.persistence.BasePersistObjectConverter;
import com.pnambic.depanfx.persistence.PersistMarshalContext;
import com.pnambic.depanfx.persistence.PersistUnmarshalContext;
import com.pnambic.depanfx.workspace.DepanFxProjectResource;
import com.pnambic.depanfx.workspace.DepanFxProjectResource.BuiltIn;

import java.io.File;

public class DepanFxBuiltInProjectResourceConverter
    extends BasePersistObjectConverter<DepanFxProjectResource.BuiltIn> {

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
  public void marshal(PersistMarshalContext dstContext, Object source) {
    DepanFxProjectResource.BuiltIn projRsrc =
        (DepanFxProjectResource.BuiltIn) source;

    String builtInName = projRsrc.getBuiltInPath().toString();
    marshalValue(dstContext, builtInName);
  }

  @Override
  public BuiltIn unmarshal(PersistUnmarshalContext srcContext) {

    String builtInPath = (String) unmarshalValue(srcContext, String.class);
    return new DepanFxProjectResource.BuiltIn(
        new File((String) builtInPath).toPath());
  }
}
