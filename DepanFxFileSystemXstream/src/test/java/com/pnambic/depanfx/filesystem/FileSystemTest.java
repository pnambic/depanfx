package com.pnambic.depanfx.filesystem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.pnambic.depanfx.filesystem.context.FileSystemContextDefinition;
import com.pnambic.depanfx.filesystem.graph.DirectoryNode;
import com.pnambic.depanfx.filesystem.graph.DocumentNode;
import com.pnambic.depanfx.filesystem.graph.FileSystemRelation;
import com.pnambic.depanfx.filesystem.xstream.FileSystemXstreamPlugin;
import com.pnambic.depanfx.graph.model.GraphEdge;
import com.pnambic.depanfx.graph.model.GraphModel;
import com.pnambic.depanfx.graph_doc.xstream.GraphDocPersistenceContribution;
import com.pnambic.depanfx.graph_doc.xstream.plugins.GraphDocPluginContribution;
import com.pnambic.depanfx.graph_doc.xstream.plugins.GraphDocPluginRegistry;
import com.pnambic.depanfx.graph_doc.builder.DepanFxGraphModelBuilder;
import com.pnambic.depanfx.graph_doc.builder.SimpleGraphModelBuilder;
import com.pnambic.depanfx.graph_doc.model.GraphDocument;
import com.pnambic.depanfx.persistence.DocumentXmlPersist;

public class FileSystemTest {
  private static final String EXPECTED_STRING =
      "<graph-doc>\r\n" + //
          "  <context-key>FileSystem</context-key>\r\n" + //
          "  <graph-model>";

  @Test
  public void test2LayerSerialization() throws IOException {

    GraphModel graph = buildTestGraph();
    GraphDocument document = new GraphDocument(FileSystemContextDefinition.MODEL_ID, graph);

    DocumentXmlPersist persist =
        buildFileSystemPersist().getDocumentPersist(document);

    StringWriter output = new StringWriter();
    persist.save(output, document);
    String result = output.toString();
    // assertTrue(result.startsWith(EXPECTED_STRING));
    assertEquals(EXPECTED_STRING, result);
  }

  public GraphDocPersistenceContribution buildFileSystemPersist() {
    FileSystemXstreamPlugin plugin = new FileSystemXstreamPlugin();
    List<GraphDocPluginContribution> extensions = Collections.singletonList(plugin);
    GraphDocPluginRegistry registry = new GraphDocPluginRegistry(extensions);
    return new GraphDocPersistenceContribution(registry);
  }

  public GraphModel buildTestGraph() {
    DepanFxGraphModelBuilder builder = new SimpleGraphModelBuilder();
    File baseFile = new File("base/path");
    File rootFile = new File(baseFile, "root");
    File doc1File = new File(rootFile, "doc1");
    File doc2File = new File(rootFile, "doc2");

    DirectoryNode rootNode = new DirectoryNode(rootFile.toPath());
    DocumentNode doc1Node = new DocumentNode(doc1File.toPath());
    DocumentNode doc2Node = new DocumentNode(doc2File.toPath());
    builder.newNode(rootNode);
    builder.newNode(doc1Node);
    builder.newNode(doc2Node);

    GraphEdge rootToDoc1 = new GraphEdge(rootNode, doc1Node, FileSystemRelation.CONTAINS_FILE);
    GraphEdge rootToDoc2 = new GraphEdge(rootNode, doc2Node, FileSystemRelation.CONTAINS_FILE);
    builder.addEdge(rootToDoc1);
    builder.addEdge(rootToDoc2);

    File nestFile = new File(baseFile, "nest");
    File doc3File = new File(rootFile, "doc3");
    File doc4File = new File(rootFile, "doc4");

    DirectoryNode nestNode = new DirectoryNode(nestFile.toPath());
    DocumentNode doc3Node = new DocumentNode(doc3File.toPath());
    DocumentNode doc4Node = new DocumentNode(doc4File.toPath());
    builder.newNode(nestNode);
    builder.newNode(doc3Node);
    builder.newNode(doc4Node);

    GraphEdge rootToNest = new GraphEdge(rootNode, nestNode, FileSystemRelation.CONTAINS_DIR);
    GraphEdge nestToDoc3 = new GraphEdge(nestNode, doc3Node, FileSystemRelation.CONTAINS_FILE);
    GraphEdge nestToDoc4 = new GraphEdge(nestNode, doc4Node, FileSystemRelation.CONTAINS_FILE);
    builder.addEdge(rootToNest);
    builder.addEdge(nestToDoc3);
    builder.addEdge(nestToDoc4);

    return builder.createGraphModel();
  }
}
