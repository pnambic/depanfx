# View Panel Edge Filters

The view panel's edge filters provide a task-centric means to control
the visible edges in a node view.

The view panel's `Edge Visibility` choice in the context menu provides
access to many choices for controlling the display of edges in a node view.
The built-in choices for edge visibility primarily support
the control of edge display based on the relation matcher used to
select an edge rendering strategy (e.g. color, width).
This can be an effective way to see only structural relations,
or to only see field reads.

For many analysis efforts, this level of granularity is too coarse
for the extractions any insight from the node view.
There are often too many edges to see any interesting interactions.

The `Edge Visibility` > `Edge Filters` context menu item provides
additional edge filtering behavior separate from the rendering-based
grouping provided by other edge visibility tools.

If the edge filters for a view panel is empty, there are no edge filters
and all candidate edges are shown.

If the set of edge filters is not empty,
all edges that match any of the edge filters are shown.
The effect is an `OR`-operation for matched edges to the edge filters. 
