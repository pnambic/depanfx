# Node View Panel Commands

The node view panel provides a number of actions that may be triggered
by user actions within the view rendering panel.

## Node View Motion

DepanFX provides a number of ways to change the view of node graph.
Many of the view changing commands act on the viewpoint, or camera.
The actions of these commands can be broken done into a number of categories.

* dolly: move the camera on the indicated direction,
typically one of the three standard axes.
* rotate: rotate the camera on the indicated axis.
* forward: move the camera in the direction it is looking.
* reverse: move the camera in the away from the direction it is looking.

Note that forward or reverse have effects that resemble zooming in or out.
Zooming in changes the cameras field of view,
effectively magnifying the included items.
Moving forward brings the camera closer,
which also magnifies the included items.

## Keyboard Commands

The node view panel provides a number of keyboard commands.

Key | Ctrl | Action
---|---|---
Home | no | home
Up | no | dolly y one unit positive
   | yes | rotate about x 10° negative
Down | no | dolly y one unit negative
     |yes | rotate about x 10° positive
Left | no | dolly x one unit positive
     | yes | rotate about y 10° negative
Right | no | dolly x one unit negative
      | yes | rotate about y 10° positive
Page Up | no | dolly z one unit positive
        | yes | forward one unit
Page Down | no | dolly z one unit negative
          | yes | reverse one unit
Plus ("+") | no | zoom in (90%)
Minus ("-") | no | zoom out (110%)


## Mouse Commands

Gesture | Shift | Ctrl | Alt | Action
---|---|---|---|---
Mouse Drag | no | no | no | Dolly camera
Mouse Drag  |   |    |    | Move selection
Mouse Drag | yes | no | no | Selection rectangle
Mouse Click | no | no | no | Select node
Mouse Click | no | yes | no | Add selected node
Mouse Click | no | no | yes | Remove selected node
Mouse Release | X - drag option | no | no | Select nodes
Mouse Release | X - drag option | yes | no | Add selected nodes
Mouse Release | X - drag option | no | yes | Remove selected nodes