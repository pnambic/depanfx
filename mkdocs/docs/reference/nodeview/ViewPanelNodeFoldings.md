# Node Foldings in a Node View

Node views are often consumers of [node foldings](../nodelist/NodeFoldings.md).
When a node folding is attached to a node view,
member nodes in the node folding move into their nest node,
focusing the display on connections between the
higher-level nest nodes.

Nodes that are not part of a node folding remain visible and individually
manipulatable via the normal actions provided by the node view panel.

## Rendering With Node Foldings

Node foldings affect both the nodes and edges of a node view.
Member nodes and internal edges are hidden,
and visible edges move to visible nest nodes.

Nest nodes can be open or shut.
A shut nest node hides its member nodes and their member descendants.
An open nest node makes its member nodes visible.

The expanded state of each nest node is indicated by the
circular node nest overlay on the rendered node.
If the circle is filled, the nest node is closed.
If the circle is open, the nest node is open

Each edge that connects a member node is altered to use
the member node's least ancestral visible nest node.
If both endpoints of a edge use the same node,
the edge is hidden.

The overall effect is as if the member node's
relations and dependencies are hoisted into their containers.

## Adding a Node Folding to a Node View

From the view panel's context menu,
select the `Node Folding` > `Node Folding...` item.
Selecting this item will bring up the node folding table editor.
This editor dialog allows the selection of existing
node foldings for use with the node view.

## Opening and Shutting a Node

Nest nodes in the node view are indicated with
nest circle overlay at each nest node's upper left corner.
This overlay signifies that the related node could be
opened or shut.

On view panel's context menu,
select the `Node Folding` > `Open Node` or > `Open Node`
for eligible selected nodes.
Selecting the action will open or shut any eligible selected.

The mouse can also be used to change a nest nodes expanded state.
Double clicking on a node will toggle it's expanded state.
This makes an easy way to incrementally explore a large system.
