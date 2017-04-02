# Devoxx France 2017 - Neo4j Lab

## Build

```
 $> mvn -pl workshop -am clean package
```

This should produce `zip`, `tar.gz` and `tar.bz2` archives.

## Run

```
 $> cd $(mktemp -d)
 $> unzip /path/to/this/repo/workshop/target/devoxx-fr-2017-neo4j-lab.zip
 $> ./devoxx-fr-2017-neo4j-lab/bin/hands-on-neo4j -u neo4j -p
Neo4j password: (password prompt)

Starting session now...
... done!

Welcome to Devoxx France 2017 Hands on Neo4j!
Please make sure your Cypher statements end with a semicolon.
Available commands can be displayed with ':commands'

(:Devoxx)-[:`<3`]-(:Cypher)> :commands
:show - shows current exercise instructions
:exit - exits REPL
:reset - resets progression, you'll start over at the first exercise
:commands - displays this list of commands
(:Devoxx)-[:`<3`]-(:Cypher)>
```

## Debug

Same as before, just run this first:
```
 $> export JAVA_OPTS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=5005"
```

When you don't need it anymore:
```
 $> unset JAVA_OPTS
```

## Dataset

You should make sure to have a non-empty database:

 * get the raw "Cineasts" dataset [here](http://example-data.neo4j.org/3.0-datasets/cineasts.tgz)
 * configure your DB to point to it

The exercises are automatically imported from the [classpath](./workshop/src/main/resources/exercises/dump.cypher) in
an idempotent way (thus preserving the user progression between each re-run).

## Generating the exercises

Please read the generator [README](./generator/README.md).

For convenience purposes, you should rely on and edit as needed the generator input file versioned [here](./workshop/raw_exercises/exercises.json).
