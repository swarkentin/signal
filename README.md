# Canada Food Guide Menu 

[![Build Status](https://travis-ci.org/swarkentin/foodguide.svg?branch=master)](https://travis-ci.org/swarkentin/foodguide)

[![Coverage Status](https://coveralls.io/repos/github/swarkentin/foodguide/badge.svg?branch=master)](https://coveralls.io/github/swarkentin/foodguide?branch=master)

Changes to Source Data
--------------------------

Some changes were made to the source data to handle data inconsistencies

* Renamed "fgcat_id": "da" in foods-en|fr.json to "fgcat_id": "mi" to be consistent with fg_directional_satements.json
* Made all genders English so they can map to enum without a custom dserialier
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
