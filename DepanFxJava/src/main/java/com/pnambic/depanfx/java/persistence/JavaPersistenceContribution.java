package com.pnambic.depanfx.java.persistence;

import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.java.context.JavaContextDefinition;
import com.pnambic.depanfx.java.context.JavaNodeKindId;
import com.pnambic.depanfx.java.graph.ClassNode;
import com.pnambic.depanfx.java.graph.FieldNode;
import com.pnambic.depanfx.java.graph.JavaNode;
import com.pnambic.depanfx.java.graph.MemberNode;
import com.pnambic.depanfx.java.graph.MethodNode;
import com.pnambic.depanfx.java.graph.ModuleNode;
import com.pnambic.depanfx.java.graph.PackageNode;
import com.pnambic.depanfx.java.graph.graphdata.ClassInfo;
import com.pnambic.depanfx.java.graph.graphdata.FieldInfo;
import com.pnambic.depanfx.java.graph.graphdata.MethodInfo;
import com.pnambic.depanfx.java.graph.graphdata.ModuleEdgeInfo;
import com.pnambic.depanfx.java.graph.graphdata.ModuleInfo;
import com.pnambic.depanfx.persistence.PersistDocumentTransportBuilder;
import com.pnambic.depanfx.persistence.plugins.GraphNodePersistencePluginContribution;

import org.springframework.stereotype.Component;

@Component
public class JavaPersistenceContribution
    implements GraphNodePersistencePluginContribution {

  private static final Class<?>[] ALLOWED_INFOS = new Class[] {
      ClassInfo.class, FieldInfo.class, MethodInfo.class,
      ModuleEdgeInfo.class, ModuleInfo.class
  };

  @Override
  public void extendPersist(
      PersistDocumentTransportBuilder builder, Class<?> withType) {
    if (withType.isAssignableFrom(GraphNode.class)) {
      builder.addConverter(new ClassNodeConverter());
      builder.addConverter(new FieldNodeConverter());
      builder.addConverter(new MethodNodeConverter());
      builder.addConverter(new ModuleNodeConverter());
      builder.addConverter(new PackageNodeConverter());
      builder.addAlias("class-info", ClassInfo.class);
      builder.addAlias("field-info", FieldInfo.class);
      builder.addAlias("method-info", MethodInfo.class);
      builder.addAllowedType(ALLOWED_INFOS);
    }
  }

  /////////////////////////////////////
  // Node Converters

  private static class ClassNodeConverter
      extends JavaNodeConverter<ClassNode> {

    public ClassNodeConverter() {
      super(ClassNode.class, JavaContextDefinition.CLASS_NKID);
    }

    @Override
    protected ClassNode createNode(String fqcn) {
      return new ClassNode(fqcn);
    }
  }

  private static class ModuleNodeConverter
      extends JavaNodeConverter<JavaNode> {

    public ModuleNodeConverter() {
      super(ModuleNode.class, JavaContextDefinition.MODULE_NKID);
    }

    @Override
    protected JavaNode createNode(String moduleName) {
      return new ModuleNode(moduleName);
    }
  }

  private static class PackageNodeConverter
      extends JavaNodeConverter<JavaNode> {

    public PackageNodeConverter() {
      super(PackageNode.class, JavaContextDefinition.PACKAGE_NKID);
    }

    @Override
    protected JavaNode createNode(String packagePath) {
      return new PackageNode(packagePath);
    }
  }

  private static abstract class MemberNodeConverter<T extends MemberNode>
      extends JavaNodeConverter<T> {

    public MemberNodeConverter(
        Class<?> targetType, JavaNodeKindId nodeKindId) {
      super(targetType, nodeKindId);
    }

    @Override
    protected T createNode(String memberKey) {
      int split = memberKey.lastIndexOf('.');
      String ownerFqcn = memberKey.substring(0, split);
      String memberName = memberKey.substring(split + 1);
      return createNode(ownerFqcn, memberName);
    }

    protected abstract T createNode(String ownerFcqn, String memberName);
  }

  private static class FieldNodeConverter
      extends MemberNodeConverter<FieldNode> {

    public FieldNodeConverter() {
      super(FieldNode.class, JavaContextDefinition.FIELD_NKID);
    }

    @Override
    protected FieldNode createNode(String ownerFcqn, String memberName) {
      return new FieldNode(ownerFcqn, memberName);
    }
  }

  private static class MethodNodeConverter
      extends MemberNodeConverter<MethodNode> {

    public MethodNodeConverter() {
      super(MethodNode.class, JavaContextDefinition.METHOD_NKID);
    }

    @Override
    protected MethodNode createNode(String ownerFcqn, String memberName) {
      return new MethodNode(ownerFcqn, memberName);
    }
  }
}
