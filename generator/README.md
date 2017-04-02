# Devoxx France 2017 - Neo4j Lab Exercise Generator

## Build

```
 $> mvn -am clean package
```

## Run

### Command
```
 $> mvn exec:java -Dexec.mainClass="net.biville.florent.repl.generator.Main" \
    -Dexec.args="-o dump.cypher -i /path/to/descriptor.json -u neo4j -p "
Neo4j password: (password prompt)
```

This will output a Cypher file called `output.cypher` in the current directory.

This file, in turn, can replace the one versioned [here](../workshop/src/main/resources/exercises/dump.cypher).

Finally, the file, once in the classpath, will be parsed and executed by the REPL application at startup in order to import the exercises.

### Input file

The input file is a JSON file, defining the sequence of exercises (via, wait for it, a JSON array).

The order in which the exercises are solved is the definition order.

There are two variants:

 1. read-only exercise: 
```
  {
    "instructions": "Instructions to help solve the exercise go here.",
    "solutionQuery": "CYPHER QUERY EXECUTED AGAINST THE CONFIGURED DB TO SERIALIZE AND EXPORT ITS RESULT"
  }
```
 2. write exercise:
```
  {
    "instructions": "Instructions to help solve the exercise go here.",
    "solutionQuery": "CYPHER QUERY EXECUTED AGAINST THE CONFIGURED DB TO SERIALIZE AND EXPORT ITS RESULT",
    "writeQuery": "CYPHER WRITE QUERY TO RUN IN ORDER TO GET THE EXPECTED RESULT DEFINED BY solutionQuery"
  }
```

### Be careful!

First, be aware that the trainee database contents is going to change during the course of the workshop.
There are many reasons for this:

 * make sure your database contains only data required for the exercises, nothing else!
 * make sure your exercise queries are **specific** enough. 
 For instance, queries such as `MATCH (n) ...` will **not** work. 
 The database used by the generator doesn't contain the exercise nodes.
 The list of matched nodes will then be different between the generator DB (reference) and the trainee's. Therefore, 
 the serialized reference results will be wrong.
 * remember that exercise transactions run by the REPL are rollback-only. Consequence: one exercise cannot depend
 on the result of the other. The exercises must be independent (and, as a side note, preferably in difficulty 
 increasing order).
