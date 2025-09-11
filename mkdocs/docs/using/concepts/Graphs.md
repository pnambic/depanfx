# Graphs

The mathematical notion of a graph plays a central role in DepanFX.
Graphs are the core abstraction that powers DepanFX’s display,
rendering, and analysis tools. 

Although the general notion of a graph is a common concept,
graphs come in a variety of mathematically distinct forms.
Different applications create focused terminology.

Mathematically, DepanFX uses directed graphs
with different types for both nodes and edges.
These capabilities are among the most general of graph behaviors.

Let’s unpack that dense bit of mathematics into more practical terms.

## Graph Overview

In DepanFX, all analyzes starts with a graph.
The graph is a concrete snapshot taken from a network of elements.

When a graph snapshot is created, the source network is analysed for
elements and connections.
Discovered and interesting elements in the network are translated into
graph nodes.
Discovered and interesting connections in the network are translated into
graph edges.

## DepanFX Graph Nodes

DepanFX’s graph nodes are little more than identifiers.
These identifiers provide backtracing to their  source network,
but they serve primarily  as opaque ids. 

Much of the information in a graph lies in its structure,
which is encapsulated in the edges between nodes.

The structural information inherent in a node identifiers is:
- Graph Model:  What graph model is the basis for the node?
- Node Kind: Which of the possible kinds defined by the graph model?
- Node Id: Unique identifier (as a node kind).

Note that the node id may not be globally unique within the graph.
Different nodes in a graph may have different graph models (e.g File System and Java).

Although node identifiers provide limited data about the node,
DepanFX supports attaching a variety of different infos to a node.
The attached data might  include the node’s position for node view rending,
or user provided textual annoations.


