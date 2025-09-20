# Limitations

DepanFX is a software engineer focused project in active development.

Missing are a number of features that should be present in
a mature application targeted at casual users.

## No Busy - Wait Support

Some things just take a while.

Since smaller tools are often bound to large graphs,
the first load of any tool (e.g. node list) associated with a graph load
may take several seconds.

## No Document Cache Invalidation

Writing a new content into an existing project document
will cause future readers to use the updated contents.
But previously opened documents are not refreshed.
Previously opened document can continue their reference to the stale
content.

Restarting DepanFX will clear the cache and any stale references.

## Unstable Persistence Formats

As a project in active development, the file structures for the
different resource are subject change as needed.
In the case of node view panels, successive releases often
add new required properties to their resources.

This can create problems when projects from one version of DepanFX
are used by other versions.

Although there are no guarantees, the basic graph `.dgi` has remained
unchanged since almost the beginning of time.

There are no plans to provide migration from one version
of a persistent resource to another.
Some persistent resources are more resilient to changes.
The basic format for persistent resources is XML,
allowing for manual revisions of valuable resources.

## OGL Shutdown

The OpenGL graphic library will generate error messages
if DepanFX is shutdown with an active node view panel.

The definitive sequence of event listeners and callback hooks to
close an active JavaFX application with a JOGL window are
not well documented.

## Limited Analysis Tools

DepanFX supports a number of manual inspection practice,
and node views can provide excellent insight.
But there are no tools focused on analysis,
such as loop detection.

The node filtering tools provide a number of useful analysis functions.
For example, it is straightforward to assemble a node filter sequence
that answer a question like:
"How many packages have methods that call these four methods?"