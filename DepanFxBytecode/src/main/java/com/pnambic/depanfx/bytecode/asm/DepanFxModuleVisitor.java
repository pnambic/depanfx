package com.pnambic.depanfx.bytecode.asm;

import com.pnambic.depanfx.bytecode.AsmFactory;
import com.pnambic.depanfx.graph.model.GraphEdge;
import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.graph.model.GraphRelation;
import com.pnambic.depanfx.graph_doc.builder.DepanFxGraphModelBuilder;
import com.pnambic.depanfx.java.graph.ClassNode;
import com.pnambic.depanfx.java.graph.JavaRelation;
import com.pnambic.depanfx.java.graph.ModuleNode;
import com.pnambic.depanfx.java.graph.PackageNode;
import com.pnambic.depanfx.java.graph.graphdata.ModuleEdgeInfo;
import com.pnambic.depanfx.java.graph.graphdata.ModuleInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.asm.ModuleVisitor;
import org.springframework.asm.Opcodes;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Drawn from <hr>https://docs.oracle.com/javase/specs/jls/se9/html/jls-7.html</hr>
 * Section 7.7. Module Declarations
 */
public class DepanFxModuleVisitor extends ModuleVisitor {

  private static Logger LOG =
      LoggerFactory.getLogger(DepanFxModuleVisitor.class);

  private final AsmFactory asmFactory;

  private final DepanFxGraphModelBuilder graphBuilder;

  private final ClassNodeFactory classBuilder;

  private final String moduleName;

  private final ModuleNode moduleNode;

  public DepanFxModuleVisitor(
      AsmFactory asmFactory,
      DepanFxGraphModelBuilder graphBuilder,
      ClassNodeFactory classBuilder,
      String moduleName,
      int access) {
    super(asmFactory.getApiLevel());
    this.asmFactory = asmFactory;
    this.graphBuilder = graphBuilder;
    this.classBuilder = classBuilder;
    this.moduleName = moduleName;

    this.moduleNode = getModule(moduleName);
    if (access == Opcodes.ACC_OPEN) {
      graphBuilder.addNodeInfo(moduleNode, ModuleInfo.class,
          new ModuleInfo(ModuleInfo.ModuleKind.KIND_OPEN));
    }
  }

  @Override
  public void visitMainClass(String mainFqcn) {
    super.visitMainClass(mainFqcn);

    LOG.info("module {} has main class {}", moduleName, mainFqcn);

    //$ Transform mainFqcn to class (replace / with .)?
    ClassNode mainNode = getClass(mainFqcn);
    addEdge(mainNode, JavaRelation.MODULE_MAIN);
  }

  @Override
  public void visitPackage(String packageName) {
    super.visitPackage(packageName);

    LOG.warn("module {} has package {}", moduleName, packageName);

    PackageNode packageNode = getPackage(packageName);
    addEdge(packageNode, JavaRelation.MODULE_PACKAGE);
  }

  @Override
  public void visitRequire(String requireModule, int access, String version) {
    super.visitRequire(requireModule, access, version);

    ModuleNode requireNode = getModule(requireModule);

    GraphEdge edge = addEdge(requireNode, JavaRelation.MODULE_REQUIRES);
    if (access == Opcodes.ACC_TRANSITIVE) {
      graphBuilder.addEdgeInfo(edge, ModuleInfo.class,
        new ModuleEdgeInfo(ModuleEdgeInfo.ModuleEdgeKind.KIND_TRANSITIVE));
    }
  }

  @Override
  public void visitExport(
      String exportPackage, int access, String... modules) {
    super.visitExport(exportPackage, access, modules);

    PackageNode exportNode = getPackage(exportPackage);
    addEdge(exportNode, JavaRelation.MODULE_EXPORTS);

    stream(modules)
        .map(this::getModule)
        .forEach(m ->
            addEdge(exportNode, m, JavaRelation.MODULE_EXPORTED_TO));
  }

  @Override
  public void visitOpen(String openPackage, int access, String... modules) {
    super.visitExport(openPackage, access, modules);

    PackageNode openNode = getPackage(openPackage);
    addEdge(openNode, JavaRelation.MODULE_OPENS);

    stream(modules)
        .map(this::getModule)
        .forEach(m ->
            addEdge(openNode, m, JavaRelation.MODULE_OPENED_TO));
  }

  @Override
  public void visitUse(String serviceFqcn) {
    super.visitUse(serviceFqcn);

    LOG.info("module {} uses service {}", moduleName, serviceFqcn);

    ClassNode serviceNode = getClass(serviceFqcn);
    addEdge(serviceNode, JavaRelation.MODULE_USES);
  }

  @Override
  public void visitProvide(String serviceFqcn, String... providers) {
    super.visitProvide(serviceFqcn, providers);

    LOG.info("module {} provides {}", moduleName, serviceFqcn);

    ClassNode serviceNode = getClass(serviceFqcn);
    addEdge(serviceNode, JavaRelation.MODULE_PROVIDES);

    stream(providers)
        .map(this::getClass)
        .forEach(p -> addEdge(p, serviceNode, JavaRelation.EXTENDS));
  }

  @Override
  public void visitEnd() {
    super.visitEnd();
    // Everything should already be in the builder.
  }

  private GraphEdge addEdge(GraphNode tail, GraphRelation relation) {
    return addEdge(moduleNode, tail, relation);
  }

  private GraphEdge addEdge(
      GraphNode head, GraphNode tail, GraphRelation relation) {
    return graphBuilder.addEdge(new GraphEdge(head, tail, relation));
  }

  private ClassNode getClass(String bytecodeFqcn) {
    return classBuilder.fromInternalName(bytecodeFqcn);
  }

  private ModuleNode getModule(String name) {
    return (ModuleNode) graphBuilder.mapNode(new ModuleNode(name));
  }

  private PackageNode getPackage(String packageName) {
    return (PackageNode) graphBuilder.mapNode(new PackageNode(packageName));
  }

  private Stream<String> stream(String[] elements) {
    if (elements != null) {
      return Arrays.stream(elements);
    }
    return Stream.empty();
  }
}
