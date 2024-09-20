# Welcome to DepanFX

Welcome to DepanFX.
DepanFX is a direct manipulation tool for visualization, analysis,
and refactoring of dependencies in large applications.

DepanFX allows software engineers to group software components into flexible categories.
These categories can be based on analysis requirements,
not a priori classification strategies.

DepanFX is based on Java software components, and should run on many platforms.
Since a key use of DepanFX is the analysis of large software systems,
a large amount of memory is required for effective use.

## Key Features

![DepanFX initial startup](../img/Depan Startup.png "Welcome from Code Inspector Gonzo")

### User Interface

DepanFX provides a rich user interface for interacting with dependency graphs.
The interface is designed to facilitate extended interactions with the dependency data.
Dependency analysis can be a long term project,
with lots of effort over an extended period of time.

One of goals for DepanFX to coordinate and track this analysis effort.
Obtaining and displaying the data is not enough.
Software engineers need to develop analysis models incrementally,
augment component information,
and return to their work over time as requirements evolve.

## History and Authors
The original version of DepAn was developed as OSS at Google by Lee Carver,
Yohan Coppel, and a handful of other contributors (2008?).
 One of the original intentions was to explore the space of dependency analysis options.

DepAn provided a powerful visualization system based on OpenGL.
The absence of predefined structure for relations enabled the discovery of unexpected structures (e.g. the Incestuos Classes antipattern).
Its architecture of components and extensions excelled at supporting other languages.

However, DepAn was beginning to be long in the tooth.
Some of the data structures were failing to adapt to more general uses,
advanced Java structuring (e.g. modules)  was impossible,
and other components felt too heavyweight (e.g. Eclipse extensions).
Even though desktop applications are passé,
the implementation of DepanFX began in 2023.

DepanFX is largely built by Pnambic Computing,
with parts taken from the early Google version.
It remains free OSS, under an Apache 2.0 license.
The primary contributor has been Lee Carver.
Additional contributors are welcome.
