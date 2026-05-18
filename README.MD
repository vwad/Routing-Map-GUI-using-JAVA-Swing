# Routing Map Using JAVA Swing
## Repository Structure
The project has easy to follow for us structure:
<pre><code>
├───data
│   ├───closures
│   ├───db
│   ├───gtfs
│   └───tiles
├───src
│   ├───main
│   │   └───java
│   │       ├───Closure
│   │       │   ├───Basic
│   │       │   └───Distributed
│   │       ├───GUI
│   │       │   ├───Application
│   │       │   ├───Map
│   │       │   └───Painter
│   │       ├───JSON
│   │       ├───Routing
│   │       │   ├───Algorithm
│   │       │   ├───Engine
│   │       │   └───Graph
│   │       ├───SQLDatabase
│   │       │   ├───Config
│   │       │   ├───Database
│   │       │   ├───Exception
│   │       │   ├───GTFSReaders
│   │       │   └───Query
│   │       └───Utils
│   │           ├───Distance
│   │           ├───Location
│   │           ├───Others
│   │           └───Time

</code> </pre>
## Roting Engine
To run Routing Engine, you need to know the documentation, main parts of it are shown below:
### Documentation of Routing Engine
<pre><code>
Requests to be accepted by the engine are as follows:
• {"load":filenameString} — causes the engine to load GTFS data from the given filename,
which will point to a readable ZIP file somewhere on the local file system.
– In response, the engine must discard all previously-loaded data and ingest the new
GTFS dataset.
– If successful, the engine will reply with {"ok":"loaded"} and await further input.
– If an error occurs (e.g. the named file was not found, or contained invalid data,
or some other problem), the engine will reply with {"error":errorString}, where
errorString is a human-readable message describing the problem.
– Errors during loading are considered fatal: after reporting a loading error, the engine
should terminate.
– The engine must finish loading and be ready for subsequent queries within at most
two minutes of receiving a "load" request. If it takes longer than this, the engine
is considered to have failed and will likely be abruptly terminated and restarted.
• {"routeFrom":sourcePoint,"to":targetPoint,"startingAt":timeString} where both
sourcePoint and targetPoint are point objects  — causes the engine to
compute and print the best available route.
– The engine computes a public transport route between the two points, including
walking and transfers.
– The resulting route will usually, but not always, start and end with a short walk to
or from the nearest public transport stop from the active GTFS dataset.
– If successful, the engine will reply with {"ok":[routeStep, routeStep, ...]}, with at
least one routeStep.
– The returned route must be the best (i.e. shortest travel time) route the engine can
produce.
– If route computation fails for some reason, the engine should reply with
{"error":errorString}, where errorString is a human-readable message describing
the problem.
– Errors during routing are not considered fatal: after reporting such an error, the
engine should continue running normally, awaiting subsequent requests.
– The engine must reply to a routing request within at most two minutes. If it takes
longer than this, the engine is considered to have failed and will likely be abruptly
terminated and restarted.
• Any other JSON input is an erroneous request.
– In response, the engine must do nothing except report the error by replying with
{"error":errorString}, where errorString is a human-readable message describing
the problem (for example, errorString could be "Bad request").
– These invalid requests are not considered fatal: after reporting such an error, the
engine should continue running normally, awaiting subsequent requests.
• Input that does not conform to JSON syntax is a fatal error: the engine must print
{"error":errorString}, where errorString is a human-readable message describing the
problem (for example, errorString could be "Bad JSON input") and then terminate.
Format of routeStep objects
Successful routing between two locations results in an object containing a JSON array of one
or more routeStep JSON objects. Every routeStep in the array must be one of:
• {"mode":"walk", "to":point, "duration":minutesNumber, "startTime":timeString}
— a step where the traveller is to walk to another point, starting at the given time, and
taking the given number of minutes. The system always assumes a fixed walking speed of
5km/h.
• {"mode":"ride", "to":point, "duration":minutesNumber, "startTime":timeString,
"stop":stopNameString, "route":routeInfo} — a step where the traveller is to ride
to another point, starting at the given time, and taking the given number of minutes,
travelling via the public transport service described by routeInfo and
arriving at the stop named stopNameString.
Format of routeInfo objects
A routeInfo is a JSON object of the form {"operator": operatorNameString, "shortName":
shortNameString, "longName": longNameString, "headSign": headSignString}, where
• operatorNameString is agency_name from agency.txt in the active GTFS dataset;
• shortNameString is route_short_name from routes.txt;
• longNameString is route_long_name from routes.txt; and
• headSignString is trip_headsign from trips.txt.
Format of timeString values
A timeString is a JSON string representing a moment on the 24-hour clock.
Examples. Midnight would be "00:00". Noon, "12:00". Fourteen minutes past three in the
afternoon, "15:14".
Format of point objects
A point JSON object has the form {"lat":latitudeNumber, "lon":longitudeNumber}.
Example. A point near Maastricht’s “Markt” stop is {"lat":50.851383,"lon":5.69002}.</code></pre>

### How to run Routing Engine
You have 2 ways on how to run.
#### First way
You have to run a command:
```bash
    mvn compile
```
To compile the project and then, you can run routing engine by executing:
```bash
    mvn -q exec:java
```
If successful, you can input following the manual formatting.
#### Second way
Open [Routing Engine](src/main/java/Routing/Engine/RoutingEngine.java).  
Run main method of the class.

## Map
This paragraph will explain how to run the map.
### How to run it


#### First way
You have to run a command:
```bash
    mvn compile
```
To compile the project and then, you can run transit map by executing:
```bash
    mvn exec:java -Dexec.mainClass="GUI.Application.TransitMap"
```
If successful, you can input following the manual formatting.
#### Second way
Open [Transit Map](src/main/java/GUI/Application/TransitMap.java). 
Run main method of the class.

#### Route
The order of operations to display routes:
* Press 'Plan Route' button.
* Specify time and date.
* Add or remove travels via clicking on the map or adding points manually

#### Heat Map
To display heatmap:
* Press 'Display Heat Map' button.
* Specify time and date.
* Select start a point on the map from which you want to display heatmap.

### Tiles

In order for Tiles to render you have to upload the OSM Tiles manually. You have to place it in data/tiles folder and name it map_tiles.zip.
In order for Location to actually render you have to create a class extending class Location/City/Country in this [module](src/main/java/Utils/Location). You have to define boundary yourself and then change the location in [CurrentLocation.java](src/main/java/Utils/Location/CurrentLocation.java). If not, feel free to use [Global](src/main/java/Utils/Location/Global.java), but it wasn't designed to work with it and just a potential usage.

Future improvement could be to make this more config friendly.

### Questions

If the issues arrive feel free to reach out and I can explain. A lot more details that I am not sure if legally can copy here.