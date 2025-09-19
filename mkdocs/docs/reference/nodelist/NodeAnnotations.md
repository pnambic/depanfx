# Node Annotations

Node annotations allow multiple text values to be associated with each node.

New info stores are created with a connection to a specific graph.
The info store retains any node info associated
with any node from the connected graph.

The info store is also associated with an annotation index.
The annotation index defines the potential node infos with an info store.

## Annotation Structure

An annotation represents a blob of data that is associated with a node.
The annotation index defines the ways that the blob of data should be
interpret.

Blobs of data that represent text are pretty simple.
The entire blob should be handled as an unstructured string.
For presentation, simply show the entire contents of the blob.

The presentation for other blobs can be more nuanced.
For example, a Position blob carries the x, y, and location of a node.
Presentation of a Position blob requires choosing which internal
element should displayed and changed.

## Annotation Index

An annotation index defines the retrieval keys and data type
for each annotation stored in an info store.
An annotation index can often be shared by multiple info stores,
even with different underlying graphs.

## Creating an Annotation Index

The menu option to create an annotation index is located at
`File` > `New` > `Annotation Index`.
Selecting this menu choice will bring up the dialog
for creating and editing an annotation index.

The annotation table defines the annotations that will be
available in an associated info store.

Each defined annotation has three properties:

- Annotation Label:  The string value that will be shown in menus to identify
this annotation.  Often in Title Words.
- Annotation Key:  The string value used to separate values in the info store.
Often in TitleCase.
- Annotation Info:  What kind of info is stored with this key.
The Annotation options defines a text based annotation.
Other choices for annotation info (Node Id, Position) support access
to internally managed data properties.

After defining the annotation to capture in an info store,
the annotation index can be saved.
Annotation index are saved with the same tool aware
interface that capture the name for the annotation,
a longer description for additional detail,
and the specification of a destination file.

Once the annotation index is created,
it can be used to create multiple info store.

An annotation index can be modified after it is created,
but additions-only are to avoid inaccessible data
in associated info stores.

## Creating an Info Store

Node info stores are always associated with a specific graph.
In order to create a node info store,
activate the create e context menu for a graph (right mouse)
select the Create Node Info Store ... item.
This will bring up the dialog box for creating and modifying
an info store.

After selecting the annotation index to use with the info store,
complete the construction of the info store by giving a name,
a description, and saving the info store.

Once an info store for a graph has been created,
it can be used to define info columns
where the annotation value is displayed and can be seen.

## Creating an Annotation Info Column

Annotation info columns are components of node list tables,
and can be added from the node list table panel.

From the node list table's table, activate the context menu
and select the items `Table View` > `Add Column` > `New Infos Column ...`.
Adding a node list table can also be started by activating the
context menu for any column and selecting
the `Add Column` > `New Infos Column ...` item there.
Either path brings up the dialog box for creating and modifying
an info column.

Like other columns, the node info column definition
starts with the label and width for the colum.

The Info Source section identifies which data to provide.
Based on the associated annotation index,
the info store can hold many different data blobs,
each with a variety of properties.

The first step is to select the info store to use for the data.
User defined info stores are stored in the project
and can be selected through the browse button.

DepanFX also defines two internal info stores
for Node Id Store and Node Position Store.
These psuedo-stores define access to node id details
and the x, y, z locations of nodes in a node view panel.
Although DepanFX uses these internally for some info columns,
they are rarely necessary for user defined annotation columns.

AFter selecting the info store to use as the souce of data
select the specific annotation to use as the source of data.
The available optins for annotation should mimic the
annotation label values provided when the annotation store was defined.

After selecting which node info blob to use for data,
select the individual property from the annotation blob.
For text annotation, the only property is the annotation itself.

Other annotation types, such as Position, ofter multiple properties.

## Using an Annotation Column

An annotation column displays the current value of its associated
annotation property and allows changes to the value.

Since new info stores generally start empty,
a new annotation column starts blank.

Edit the value for any node by double clicking on the cell.
A simple one line text editor will open and the new value can be entered.
Press the <Enter> key to complete the data entry.

Updated values are not saved automatically to the info store.
To avoid losing data, take advantage of the column's context
menu and `Save Node Infos ..` often.

