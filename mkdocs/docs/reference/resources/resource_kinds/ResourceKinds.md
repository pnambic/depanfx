# DepanFX Resources

DepanFX uses many different tool and data types.
This provides fine control over various organizational and presentation
aspects of interesting graph features.

Many of the resource types used by DepanFX are specific to a particular
presentation panel, such as the Node List panel or the Node View panel.

The following sections provide a list of the resource types used by DepanFX.

## Graph Documents

Graph documents provide the source snapshot of a system to analyze.

Type | Ext | Type | Locations
---|---|---|---
Graph | `dgi` | Graph | Graphs/*
Git Repo | `dgrti` | Tool | Tools/*

## Node List Panel Documents

The Node List panel provides a tabular view of selected or interesting nodes
from a graph.

The Node List (`dnli`) content type is a selection of nodes from a graph.
Node Lists provide the core abstraction for node selection.

This view is the composition of several components:

* A sequence of section that each group and organize nodes.
Sections often provide hiearachries for '
* A final flat section that captures remaining nodes.
* A sequence of columns that provide additional details about each displayed nodes.

Type | Ext | Type | Locations
---|---|---|---
Node List | `dnli` | Analysis | Analysis/**
Category Column | `dccti` | Tool (column) | Tools/Node List/Columns/*
Focus Column | `dfcti` | Tool (column) | Tools/Node List/Columns/*
Node Key Column | `dnkcti` | Tool (column) | Tools/Node List/Columns/*
Flat Section | `dfsti` | Tool (section) | Tools/Node List/Sections/*
Tree Section | `dtsti` | Tool (section) | Tools/Node List/Sections/*
Table View | `dtvti` | Tool (table) | Tools/Node List/Table Views/*

## Node List Panel Documents

The Node View panel provides a graphical view of the selected nodes.
Each Node View panel corresponds to an Analysis document that
encapsulates the rendering options.

This view is the composition of several components.

* Directives for rendering displayed edges from the graph.
* Directions for rendering displayed nodes from the graph.
* View specific rendering directives and location for each node and edge.

The Node View tool information document expands a Node List to include

* A reference to the default for edge display directives.
* A reference to the default for node display directives.
* Set of visible nodes.
* Each node's location.
* Node specific rendering directives.
* Edge specific rendering directives.

The options for representation are configured
 through their association documents.

Type | Ext | Type | Locations
---|---|---|---
Link/Edge Display | `dnvedi` | Tool (view) | Analysis/*
Node Display | `dnvndi` | Tool (view) | Tools/Node View/Edge Display/**
Node View | `dnvi` | Tool (view) | Tools/Node View/Node Display/**
Radial Layout | `drlti`| Tool (layout) | Tools/Node View/Layouts/*
Shift Layout | `dslti` | Tool (layout) | Tools/Node View/Layouts/*
Tree Layout | `dtlti` | Tool (layout) | Tools/Node View/Layouts/*

## Graph Query Documents

The various graph query documents provide a variety of tools
for selecting nodes and edges or traversing elements of the graph

Type | Ext | Type | Locations
---|---|---|---
Link Matcher Sequence | `dlmsti` | Tool (filter) | Tools/Link Matchers/**
List Filter | `dlfti` | Tool (filter) | Tools/Link Matchers/**
Matcher Filter | `dmfti` | Tool (filter) | Tools/Link Matchers/**
Filter Sequence | `dnfsti` | Tool (filter) | Tools/Link Matchers/**
Node Kind Filter | `dlfti` | Tool (filter) | Tools/Link Matchers/**
Reference Filter | `drfti` | Tool (filter) | Tools/Link Matchers/**
Sequence Filter | `dsfti` | Tool (filter) | Tools/Link Matchers/**

