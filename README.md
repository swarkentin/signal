# Canada Food Guide Menu 

[![Build Status](https://travis-ci.org/swarkentin/signal.svg?branch=master)](https://travis-ci.org/swarkentin/signal)

Changes to Source Data
--------------------------

Some changes were made to the source data to handle data inconsistencies

* Renamed "fgcat_id": "da" in foods-en|fr.json to "fgcat_id": "mi" to be consistent with fg_directional_satements.json
* Converted *-fr.json to UTF-8 encoding

Building
-------------

To build the service:

```bash
$./gradlew build
```

To run the service from gradle:

```bash
$./gradlew run

> Task :run
INFO  io.micronaut.runtime.Micronaut - Startup completed in 2790ms. Server Running: http://localhost:11029
```
