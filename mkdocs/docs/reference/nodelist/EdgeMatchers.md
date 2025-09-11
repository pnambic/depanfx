# Edge Matchers

Edge matchers are an important mechanism for the
analysis of DepanFX graphs.
Edge matchers allow direct selection of the edges
that connect nodes together.

Edge matchers provide that basic structural knowledge of the graph
that is used to build tree and to select node rendering strategies.

DepanFX currently support three basic forms of edge matcher.
1) Relation Matcher:  An edge matches if the edge's relation matches
 the relation defined in the matcher.
 The match may be forward or reverse, depending on the matcher's options.
1) Node List Matcher:  An edge matches
 if the edge's head node in the non-empty head node list
 and if the edge's tail node is in the non-empty tail node list.
 If either list empty, only the present node list determines if
 an edge is matched.
1) Composite Matcher: An edge matches a composite mather if any of
 its constituent matchers match the edge.

## Edge Matching Behavior

Edge matching is an essential activity for graph analysis,
but it rarely happens in isolation as a task on its own.

Edge matching is widely used to traverse from one set of nodes
to another set of nodes.
Sometimes this traversal is structural (all members of a package),
sometimes this traversal has semantic implications (callers of a method).
In this use, the matching of an edge also needs to provide
the direction of travel along the edge.

This means that edge matching entails two related decisiona about an edge.
For reference, remember that an edge defiens a typed relations
from a head node to a tail node.

When a condidate edge is present to an edge matcher,
the two decisions to make are:
- Does the edge meet the matching criteria?
- Which direction should the edge traversal use?

Edge matcher encode this decision as a `link` object that connects
the head and tail nodes.
Link objects works in terms of sources and targets.
If the link is in the forward direction, the sources and targes are aligned
with the edge's head and tail.
If the link is reverse, the source and target nodes will be reversed from
the edge's head and tail.

All graph models define a number of relations to categorize the
forms of edges that connect different nodes.

For example, the File System graph model defines three relations:

1) Contains directory
1) Contains file
1) Symbolic link

The Java graph model buils upon these relations and adds an additional
28 categories of edges between nodes.



## Relation Edge Matcher Behavior

Relation edge matchers match an edge if the relation for the
edge is the same as the relation for the matcher.
Relation edge matchers are predefined for every relation
defined by each of the installed graph models (File System, Java).

A relation edge matcher can match an edge in the forward or reverse
direction.

## Creating a Relation Edge Matcher

Depan does not currently allow for user defined relation edge matchers.
Each of the pre-defined graph models (File System, Java) provide
a complete set of relation based edge matchers.
These predefined edge matchers can be used by reference in the
user definable composite matchers.

## Node List Edge Matcher Behavior

Node list edge matchers allow for two node lists
to be used to match an edge:
1) A head node list, used to match the head node of an edge.
2) A tail node list, used to match the tail node of an edge.

When a node list edge matcher matches an edge,
it always provides a forward link for the edge.

If either the head or tail node list is empty,
that node list is not used to determine
if an edge is a match.
In effect, an empty node list matches all nodes.

If the head of a candidate edge is contained
in the head node list,
and the tail node list is empty, the edge is a match.
If the tail of a candidate edge is contained
in the tail node list,
and the head node list is empty, the edge is a match.
If both nodes lists are non-empty, and the head and tail
nodes are in their corresponding node lists, the edge is a match.
As a logically consistent "no-op", if both node-lists
are empty, all edges are a match.
If none of these cases apply, the edge is not match
and the matcher turns a null link.

Note that most links are from head to tail,
and the rendered arrow is normally the tail node of an edge.
If you want to see the "callers" or "users" of a node,
the node of interest should be in the tail node list.

## Creating a Node List Edge Matcher

The menu item `File` > `New` > `New Node List Matcher` brings up
the dialog for creation and editing a node list edge matcher.

The node list edge matcher dialog provides for the definition
of a head node list and a tail node list.
Use the associated `<Browse>` buttons to select the desired node lists.

After selecting the node lists that define the edge matcher,
complete the construction of the edge matcher by giving it a name,
a description, and saving the edge matcher.

As a convenience, the construction of node list edge matchers is
avaiable directly within the `Edge Visibilty` menu tree.
The `Edge Visibilty` > `Edge Filters` > `Add Node List Filter ...`
menu item also brings up the dialog for creation and editing
of node list edge filters.
Once the new node list edge matcher is created,
it is automatically added to the view panel's list of edge filters.