# Lists and Views

The node list panel and node view panel provide two important perspectives
on the nodes in a dependency graph.

A node list panel provides a tabular presentation for selected nodes
in the dependency graph.
This perspective is very effective for software analysis tasks.

The node view panel provides a graphical presentation for selected nodes
and their relationships.
This perspective is very effective for graph inspection and discovery tasks.

## Our Goal

The following sections provide an introduction to these two perspectives
through a simple goal - obtain a dependency graph image that can be
include in another document.

Fortunately, DepanFX can capture a snapshot of a dependency graph as a
png image which is saved to a file.
The resulting file can be include in other documents where helpful.

For this task, we will use the node list panel to focus the attention
onto the Java elements of the HelloWorld application.
The construction of the `.jar` file from the HelloWorld application
includes a large number of discovered artifacts,
only some of which are explicitly Java.
The node list panel allows us to quickly select these components
and save that selection for later analysis.

The node view panel provides a graphical view of the selected nodes
that is enriched with explicit presentations of their multiple relationships.
By pulling the selected Java nodes into a node view,
the structure of the components is revealed.
The Take Screenshot menu item allow the currently rendered
image to be saved as our PNG file.