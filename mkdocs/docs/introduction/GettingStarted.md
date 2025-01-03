# Getting Started

When DepanFX initially starts, the session's initial scene is populated with
two panels: a Welcome panel, and a Workspace panel.  Each panel is accessible as
a tab in the initial scene's user interface.

The first tab, and the one displayed on startup, shows the Welcome panel.
The Welcome panel provides a cheery entrance to DepanFX.
Say “hi” to DepanFX’s mascot, Code Inspector (CI) Gonzo.
![DepanFX initial startup](../img/Depan Startup.png "Welcome from Code Inspector Gonzo")<br/>
The Welcome panel can be closed or ignored in most DepanFX sessions.

The second panel, labeled Workspace, is the main starting point for many DepanFX analyzes.
At the top level, a session’s workspace is a collection of projects.
Each project holds many different analysis resource,
typically grouped by system under analysis.
These include dependency graphs, lists of interesting nodes,
and assorted tools for extracting data from the graphs.

When DepanFX starts, the workspace is initialized with a “Built In” project.
This project provides a number of general purpose artifacts that can be used
in the analysis of many software systems.

## First Steps
Once DepanFX starts, it’s time to start analyzing a software system.  This requires:

1. Setting up an area to hold our analysis.
1. Obtaining the dependency graph for the system under analysis.
1. Opening the dependency graph, and searching for interesting structures.

These steps are discussed in each of the following sections.

For these steps, the examples are based on a simple implementation
of the classic “Hello, World!” application.
This implementation is available on GitHub at <https://github.com/pnambic/hello_world>.
The repository includes the source code, a build script,
and the packaged HelloWorld.jar.
If you clone the repository, you should be able to run the application with the Java interpreter.

```bash
$ java -jar HelloWorld.jar
Hello, World!
```


Although this is a simple system to analyze,
it is a good introduction to DepanFX’s capabilities.

## Create A Project
Once DepanFX starts, the first action is the creation of a new project.
The new project will hold the analysis artifacts for a system under analysis.

1. Select the Workspace tab.
1. Right click in the Workspace panel to bring up the context menu.
1. Select “New Project…”<br/>
![New Project context menu](img/New Project.png "New Project context menu")
1. In the System’s folder chooser, create a new folder (e.g. HelloWorld).
1. Select the new folder as the folder chooser’s result.
![Open new folder as new project](img/Open Project.png "Open new folder as new project")
1. DepanFX populates the new folder standard analysis artifact folder
1. The new project folder is presented in the Workspace panel’s tree of projects.
![New project with standard folders](img/New HelloWorld.png "New HelloWorld project with standard folders")

Although any folder can be used as a project,
DepanFX will configure an empty folder with some internal structures that are
practical for software analysis projects.

These internal folders for a DepanFX project are

1. Graphs:  A folder to contain the full dependency graphs for systems under analysis.  Graphs include all of the nodes and their relationships.
1. Analyzes: A folder to contain analysis artifacts based on data from the various dependency graphs.  These include node list and visualization.
1. Tools: A folder to contain tool and option settings used during system analysis.  This folder is often further subdivided for different tool categories.

Creating or opening a project brings the project's directory structure
into the tree of items shown by the Workspace panel.
The project’s name is shown in bold, with the Analyzes, Graphs,
and Tools directories available for access.

## Adding A Dependency Graph
Dependency graphs combine a collection of nodes with a collection of edges.
The elements define the basic structure of DepanFX’s approach to system analysis.
Dependency graphs are typically obtained from a snapshot of the system under analysis.
The dependency graph information is stored in a file ending `.dgi`
(an XML file that contains depan graph information).

The Graphs directory in a project provides a context menu that includes dependency graph creation.  A right-click on the Graphs folder brings up the context menu for this item.
The context menu for the Graphs folder includes several options to create a new dependency graph.
These options include:

* File System - create a File System dependency graph from a directory.
* Git Repo - create a File System dependency graph from a git repository.
* Java - create a Java dependency graph from a file or directory.

From the Graphs folder’s context menu, selecting the menu item `New Graph > Java`
![New Java dependency graph](img/New Graph Java.png "New Java dependency graph")

brings up a dialog box to create a new dependency graph based on the Java Graph Model.
![New Java Graph Dialog](img/New Java Dialog.png "New Java Graph Dialog")

The highlighted numbers and buttons correspond with the
following sequence of actions for completing the dialog.
Once the dialog is complete,
Depan builds the dependency graph for the HelloWord
application and adds it to the Hello World Project.
The packaged jar file HelloWorld.jar provides a good start for exploring the
structure of Java applications.

1. Use the File button to open a File Chooser as the dependency source.
Navigate to the hello_world git clone, and select the HelloWorld.jar file.
![Select HelloWorld.jar](img/Select HelloWorld jar.png "Select HelloWorld.jar")
1. Edit the inferred Graph Name if you want.
To follow the example, use “Hello World”.
1. Edit the inferred Graph Description if you want.
To follow the example, leave this unchanged.
1. Use the Browse button to open a File Chooser.
With the destination file empty,
Depan will infer a filename based on the Graph Name (step 2) with an autogenterated timestamp.
![Set destination for the Hello World dependency graph](img/Save HelloWorld Graph.png "Set destination for the HelloWorld dependency graph")<br/>
The interface will propose a file name in the Graphs container.
You can change the name, but the Graphs container is recommended.
To follow the example, use “Hello World” with the autogenerated timestamp.
1. Use the Confirm button to start the dependency graph construction.
![Create HelloWorld dependency graph](img/Save New HelloWorld Graph.png "Create the HelloWorld dependency graph")

For large systems, dependency graph construction may take several seconds.
There is currently no progress meter or other mechanism to interact with this
or other long running processes.

Once the dependency graph construction is complete,
there will be a new document under the Graphs directory.
![Workspace with HelloWorld dependency graph](img/Workspace HelloWorld.png "Workspace with HelloWorld dependency graph")<br/>
The file `HelloWorld yyyyMMddhhmm.dgi` contains a snapshot of the
Java nodes and their relationships from within the `HelloWorld.jar` file.
