# Alvarium Scala SDK POC

This repository is a POC for a Scala sdk of the Alvarium project.

There are also some enhancements ideas : 

- Applies all the optimisations as discussed in Alvarium's java sdk issue [#136](https://github.com/project-alvarium/alvarium-sdk-java/issues/136)
- Replace Alvarium's `Annotators` with `EnvironmentCheckers`, and create the annotations in the engine to avoid code duplication
- `Sdk` interface is called `AlvariumEngine`
- Annotate and publish data asynchronously
- It is easy to inject its own signer/hasher/serializer


PLEASE Open an issue if you want more documentation and more details about what was done, i'm doing this repo mostly for fun.
