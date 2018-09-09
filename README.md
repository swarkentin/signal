# Canada Food Guide Menu 

[![Build Status](https://travis-ci.org/swarkentin/signal.svg?branch=master)](https://travis-ci.org/swarkentin/signal)

[![Coverage Status](https://coveralls.io/repos/github/swarkentin/signal/badge.svg?branch=master)](https://coveralls.io/github/swarkentin/signal?branch=master)

Changes to Source Data
--------------------------

Some changes were made to the source data to handle data inconsistencies

* Renamed "fgcat_id": "da" in foods-en|fr.json to "fgcat_id": "mi" to be consistent with fg_directional_satements.json
* Made all genders English so they can map to enum without a custom dserialier
* Converted *-fr.json to UTF-8 encoding

Building and Running
-------------

## Build and run tests
To build the application and run tests:

```bash
$./gradlew check
```

## Build and run application with Gradle
To build and run the service from gradle locally on a random port:

```bash
$./gradlew run

> Task :run
INFO  io.micronaut.runtime.Micronaut - Startup completed in 2790ms. Server Running: http://localhost:11029
```

## Build a Docker image:
To build the service and assemble the Docker image:

```bash
$./gradlew --no-daemon assemble && docker build -t swarkentin/foodguide .

$docker images
REPOSITORY             TAG                 IMAGE ID            CREATED             SIZE
swarkentin/foodguide   latest              dcc394683e89        1 minutes ago       117MB
```

### Run the Docker image with a specific port:
To run the assembled Docker image on port 8080:

```bash
$docker run -e JAVA_OPTS='-Dmicronaut.server.port=8080' -p 8080:8080 swarkentin/foodguide
```

API Usage
-------------

_When running with gradlew, take note of the random port on which the server is running for 
the following queries._

Both individual and family member queries use a common path syntax:

```
<NAME>:<GENDER>:<AGE>
```

For example, a 36 year old male named 'Steve' will use the syntax:

```
Steve:M:36
```
Because the Canada food guide offers a variety of suggestions, each request may return with a different menu as randomized selections are made for individuals based on their gender and age.

### Common properties:

`tipsByFoodGroup`: are randomized tips from the Canadian food guide for each food group.
`foodsByFoodGroup`: is a curated list of all foods recommended for one day by the Canadian food guide.

## User daily menu

To get Steve's daily menu, use the following REST endpoint:

```
GET /foodguide/my-daily-menu/Steve:M:36 HTTP/1.1
Host: localhost:{PORT}
Accept: application/json

{
  "name" : "Steve",
  "age" : 36,
  "tipsByFoodGroup" : {
    "Milk and Alternatives" : "Drink skim, 1%, or 2% milk each day.",
    "Grains" : "Make at least half of your grain products whole grain each day.",
    "Meat and Alternatives" : "Have meat alternatives such as beans, lentils and tofu often.",
    "Vegetables and Fruit" : "Have vegetables and fruit more often than juice."
  },
  "foodsByFoodGroup" : {
    "Milk and Alternatives" : [ {
      "categoryDescription" : "Milk",
      "servingSize" : "250 mL, 1 cup reconstitued",
      "food" : "Milk, powdered"
    }, {
      "categoryDescription" : "Milk",
      "servingSize" : "250 mL, 1 cup",
      "food" : "Milk, lactose reduced"
    } ],
    "Grains" : [ {
      "categoryDescription" : "Non whole grain",
      "servingSize" : "125 mL, &frac12; cup cooked",
      "food" : "Rice, white"
    }, ...
```

## Family daily menu

A family daily menu uses the same path syntax as an individual menu, with identifiers separated by commas. To get Steve's family menu, use the following REST endpoint:

```
GET /foodguide/family-daily-menu/Steve:M:36,Jill:F:34,Mary-Anne:F:12  HTTP/1.1
Host: localhost:{PORT}
Accept: application/json

{
  "tipsByFoodGroup" : {
    "Milk and Alternatives" : "Select lower fat milk alternatives.",    
  },
  "menuPerPerson" : [ {
    "name" : "Steve",
    "age" : 36,
    "foodsByFoodGroup" : {
      "Milk and Alternatives" : [ {
        "categoryDescription" : "Milk",
        "servingSize" : "125 mL, &frac12; cup undiluted",
        "food" : "Milk, evaporated, canned"
      },
      ...
    },
    "name" : "Jill",
    "age" : 34,
    "foodsByFoodGroup" : {...
    }
  }
  ...
 ],
  "foodsByFoodGroup" : {
    "Milk and Alternatives" : [ {
      "categoryDescription" : "Milk",
      "servingSize" : "125 mL, &frac12; cup undiluted",
      "food" : "Milk, evaporated, canned"
    }, {
      "categoryDescription" : "Milk Alternatives",
      "servingSize" : "250 mL, 1 cup",
      "food" : "Fortified soy beverage (unsweetened)"
    },...
```
