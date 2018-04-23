SPLUNK ANYPOINT STUDIO DEMO
================

Mule Anypoint Studio demo for Splunk Connector Index Operations.

HOW TO EXECUTE THE DEMO
-----------------------

* Make sure that you are using Mule Enterprise 3.7.3 (or above) and Java 6 (or above).
* Set username and password of the Splunk User as well as the host and management port of started Splunk instance in src/main/resources/credentials.properties.
* Run the demo and hit these endpoints for the demo of each processor.
    * **Create Index** - *POST* http://host:port/createindex
        * Add the name of the Index as an HTTP header with key 'indexname' 
        * e.g. http://localhost:8081/createindex
    * **Add Data To Index** - *POST* http://host:port/adddata
        * Add the name of the Index as an HTTP header with key 'indexname' 
        * Add the data to be added as an HTTP header with key 'stringdata' 
        * e.g. http://localhost:8081/adddata
    * **Get Index** - *GET* http://host:port/getindex
        * Add the name of the Index as an HTTP header with key 'indexname' 
        * e.g. http://localhost:8081/getindex
    * **Remove Index** - *POST* http://host:port/removeindex
        * Add the name of the Index as an HTTP header with key 'indexname' 
        * e.g. http://localhost:8081/removeindex
