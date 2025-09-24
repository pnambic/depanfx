# Node Foldings

Node foldings capture a membership relation between
a nest node and a group of member nodes.

This captured relation can be based on existing relations,
such as the Java package membership hierarchy,
or it can be created individual from lists of nodes.
It can be used to persist the transient hierarchy displayed
by a tree section for other analysis tools.

One common application of node foldings are with the node view panel.
Node foldings can be used in
the [node view panel](../nodeview/ViewPanelNodeFolds.md)
to concentrate the display, with a focus on the higher-scale members.

## Node Folding Behavior

The behavior of folded nodes is similar to a tree or nesting dolls.

One node acts as a nest, with a group of nodes as its members.
This can be applied recursively,
with each member of one nest node being the nest node for its
own group of nodes.

The relation between a nest node and each of its members is captured
as a node nesting.
The node nesting identifies a single member node and a single nest node.

A node folding is a collection of node nestings.
Representing a large membership hierarchy as a node folding
creates a large number of node nestings.

Conceptually, a node folding represents a new relation
and a new set of edges over the nodes of the underlying graph.
This new, user-defined relation effectively extends the graph model
for the referenced graph.
The new relation is implicitly defined by the
persistent document that contains the node folding.

As long as a node folding can be treated as an acyclic graph,
a node folding can be used in many tree based tools.
These supported tools include node folding sections
and node view panels.
If the node folding includes cycles,
it should not be used with tools that require acyclic graphs.

An individual node may be part of multiple node foldings.
Since each node folding is tracked as separate resource,
any node's inclusion in a specific folding can be checked through
the that folding's resource.

Both node lists and node views allow for multiple node foldings.
Multiple node foldings are processed in order,
with any filtering applied on a first-recognized basis.

Since a node folding defines relations between nodes of a specific graph,
a node folding is always associated with that graph.
A newly created node folding is empty.
Both the node list panel and the node view panel provide
actions to modify an attached node folding.
New nestings can be added, existing foldings can be modified or deleted,
and their display state can be changed.

## Creating a Node Folding

A node folding is always associated with a specific graph.
In order to create a node folding for a graph,
activate the context menu for a graph (right mouse)
and select the `Create Node Folding...` item.

As with other tools, enter the name for the node folding,
a longer description for additional detail,
and the specification of a destination file.

The newly created node folding is created empty.

The editors for capture folds and fold sections also
have convenience links to create a new node folding.

## Attaching a Node Folding to a Node List

A node folding can be attached directly to a node list.
Although the editing options are limited,
this can be a quick process to capture a membership hierarchy
or other discovered relation
as a reusable node folding.

From the node list panel's context menu,
select the `Node Folding...` item.
Selecting this item will bring up the node list's
node folding table editor.

The node folding table editor manages only the directly attached
capture foldings.
Node foldings associated with fold sections are controlled
directly by the fold section.

Multiple node foldings for the current graph can be added or removed
from the node list.
Each available folding will be shown as an option
for the `Fold Into` UX actions.

Node foldings that are attached directly to a node list
can only be extended.
This can be very efficient for the construction of routine
node foldings, such as captured membership hierarchies.
The node folding table editor provides a context menu
`Save Node Folding` action to save individual node folding.
It also provides an `Save All` action to save all of the capture node folding
associated with the node list.

In order to delete relations or otherwise modify a node folding,
it should be added to the node list as a section.

## Capturing a Node Hierarchy as a Node Folding

If a node list is associated with one or more node foldings,
the `Fold Tree Into` pop up menu item is included with
the context menu for all tree items in the tree section.

The `Fold Tree Into` pop up menu item shows a list of the
attached node folding resources.
After selecting the target folding, the node folding is
extended with new node nestings that capture the nest and members relation.

The additional node nestings are not immediately saved.
Use the save actions from the node list table editor to ensure
that the node nestings from the captured hierarchy are persisted
and available to other tools.

## Node Fold Sections

A node fold section is very similar to a tree section.
Both sections present a hierarchy over some set of relations between the nodes.
For a tree section, the related nodes are defined by an edge matcher.
For a node fold section, the related nodes are directly
defined in the node fold document.

## Adding a Node Folding Section to a Node List View

Node fold sections present the relations of the node folding
as a hierarchy over its collection of node nestings.

Node fold sections provide a number of actions to modify individual
node folds.

To create a node fold section, use the node node list table context menu
to create a new fold section.
From the node list table's tab, activate the context menu and
select the items `Table View` > `Insert Section` > `Insert Fold Section` > ``.
This adds a new fold section to the node list.

The newly created fold section should be configured immediately after
it is created.
Newly created fold sections have a scratch node folding document,
and changes can easily be lost.

Activate the section's context menu, and select the `Edit Fold Section...`
item.
This will bring up the fold section editor.

The node folding resource for the fold section will be blank.
Use the `Browse` action or the `New Node Folding...` action
to associated the fold section with a persisted node folding resource.

As with other section definitions, fill in the section label,
select the display options, and set the node ordering options as desired.
As with other tools, enter the name for the folding section,
a longer description for additional detail,
select a destination file.
Upon commit, the fold section will update for the changes.

This configuration ensure that changes to the node foldings can be
saved to a persisted resource.

Changes to a node folding that is associated with a node folding section are
reflected in the node list immediately.

## Adding a Explicit Node Folding to a Node Folding Resource

If any node foldings are active,
either directly attached or attached via a node folding section,
the context menu for all container nodes is enhanced
with a `Fold Tree Into` item.
This feature allows the explicit creation of a group of nodes
between a nest node and a collection of member nodes.

Activating this menu item will bring up the fold selected nodes dialog.
The table is filled with the nodes selected (checked) from the node list table.

Select one of the candidate member nodes as the nest node
for this node folding.
Use the context menu in the edit column to choose a nest node.

Chose the target node folding to receive the new node folding.
This is chosen from the drop-down list of
node foldings associated with the node list table.

When these changes are confirmed,
the node nestings for nest node and its members are added
to the node folding resource.
Don't forget to save the node folding resource after important changes.

