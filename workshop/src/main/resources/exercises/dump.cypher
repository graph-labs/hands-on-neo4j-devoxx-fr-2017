MERGE (e:Exercise {instructions: 'You should count all the nodes in the database', result: 'AQEBAGphdmEudXRpbC5IYXNoTWHwAQEDAXJlc3Vs9AkI'})
MERGE (e:Exercise {instructions: 'You should create a node Person whose name is Marouani', validationQuery: 'MATCH (n:Person {name:\'Marouani\'}) RETURN LABELS(n)[0] AS label, n.name AS name', result: 'AQEBAGphdmEudXRpbC5IYXNoTWHwAQIDAW5hbeUDAU1hcm91YW7pAwFsYWJl7AMBUGVyc2/u'})
MERGE (e:Exercise {instructions: 'You should count all the nodes of label Person', result: 'AQEBAGphdmEudXRpbC5IYXNoTWHwAQEDAXJlc3Vs9AkA'})
MATCH (e:Exercise) WITH e ORDER BY ID(e) WITH COLLECT(e) AS exercises FOREACH (i IN RANGE(0, length(exercises)-2) | FOREACH (first IN [exercises[i]] | FOREACH (second IN [exercises[i+1]] | MERGE (first)-[:NEXT]->(second))))
