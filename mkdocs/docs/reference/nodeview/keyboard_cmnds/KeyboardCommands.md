# Node View Flight Controls

The node view panel gives you several ways to change the perspective
on the graph, altering how it is displayed.
Conceptually, Depan controls a camera that is looking at the graph.
There are several ways to control what the camera is looking at
and how the camera moves through the code space.

<img alt="Gonzo Aflight" src="../Gonzo%20Flight%20184x120.png"
  title="Inspector Gonzo examining the code space"
  style="float:left; margin-right: 30px;" />
Camera motion as modeled as flight controls.
We can imagine Inspector Gonzo zooming through the code space
in his little code ship checking for interesting structures.

<br clear="both">

## Line Of Travel

Motion through the code space is based on a line of travel.
The target location for the line of travel
is indicated by the yellow-orange cross-hairs.

### Motion

DepanFX provides forward and backward movement of the camera
along the line of travel.
The throttle commands provide for continuous movement through the code space.

These actions do not change the line of travel.

Key | Modifier | Group | Action
---|---|---|---
r | none | throttle | throttle up
f | none | throttle | throttle down
x | none | throttle | cut throttle (stop)
PgUp | none | motion | move forward
PgDn | none | motion | move backward
Scroll Fwd | none | motion | move forward
Scroll Rev | none | motion | move backward
Home | none | motion | move to home

In addition to camera motion along the line of travel,
the camera can be dollied perpendicular to the line
of travel.

These actions change both the camera position and
the target location for the line of travel.

Key | Modifier | Group | Action
---|---|---|---
Up Arrow | none | shift | dolly down
Down Arrow | none | shift | dolly up
Left Arrow | none | shift | dolly left
Right Arrow | none | shift | dolly right

### Direction

The directional flight controls correspond to pitch, yaw, and roll
changes in a three dimensional code space.
These directional flight controls change the line of travel.

The directional flight controls mate naturally with a joystick.
On a keyboard, DepanFX uses the `qwe`|`asd` keys to
provide similar features.

Key | Modifier | Group | Action
---|---|---|---
w | none | pitch | nose down
s | none | pitch | nose up
q | none | yaw | turn right
e | none | yaw | turn left
a | none | roll | roll clockwise
d | none | roll | roll counter-clockwise

The numeric keypad also provides these actions with a `Ctrl` modfier

Key | Modifier | Group | Action
---|---|---|---
Up Arrow | Ctrl | pitch | nose down
Down Arrow | Ctrl | pitch | nose up
Left Arrow | Ctrl | yaw | turn right
Right Arrow | Ctrl | yaw | turn left
PgUp | Ctrl | roll | roll clockwise
PgDn | Ctrl | roll | roll counter-clockwise

# Line of Sight

Normally, the line if sight is the same as the line of travel.
However, sometimes its useful to be looking left
as our code ship moves along.

The line of sight controls follow the same pitch, yaw, and roll
abstraction used by the line of travel controls.
The `qwe`|`asd` keys map to joy-stick operations,
but include a control key modifier

Key | Modifier | Group | Action
---|---|---|---
w | Ctrl | pitch | nose down
s | Ctrl | pitch | nose up
q | Ctrl | yaw | turn right
e | Ctrl | yaw | turn left
a | Ctrl | roll | roll clockwise
d | Ctrl | roll | roll counter-clockwise
Home | Ctrl | look | look at line of travel

There are no movement actions associated with the line of sight operations.

# Field of View

The most common way to resolve more details about a graph's structure
is to move the camera close enough for the interesting details
to show themselves.

In some siuations, it is approriate to zoom in or zoom out on a detail
without changing the cameras location.

Key | Modifier | Group | Action
---|---|---|---
- (minus) | none | zoom | zoom out
+ (plus) | none | zoom | zoom in
