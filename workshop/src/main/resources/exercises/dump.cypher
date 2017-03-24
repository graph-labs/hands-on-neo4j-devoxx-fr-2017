MERGE (e:Exercise {statement: 'This is a very simple exercise', result: 'AQEBAGphdmEudXRpbC5IYXNoTWHwAQEDAXJlc3VsdLEJhNkH'})
MERGE (e:Exercise {statement: 'This is another simple exercise', result: 'AQEBAGphdmEudXRpbC5IYXNoTWHwAQEDAXJlc3VsdLIJhNkH'})
MERGE (e:Exercise {statement: 'This is yet another very simple exercise', result: 'AQEBAGphdmEudXRpbC5IYXNoTWHwAQEDAXJlc3VsdLMJhNkH'})
MATCH (e:Exercise) WITH e ORDER BY ID(e) WITH COLLECT(e) AS exercises FOREACH (i IN RANGE(0, length(exercises)-2) | FOREACH (first IN [exercises[i]] | FOREACH (second IN [exercises[i+1]] | MERGE (first)-[:NEXT]->(second))))
