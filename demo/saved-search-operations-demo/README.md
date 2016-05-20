SPLUNK ANYPOINT STUDIO DEMO
================

Mule Anypoint Studio demo for Splunk Connector Saved Search Operations.

HOW TO EXECUTE THE DEMO
-----------------------

* Make sure that you are using Mule Enterprise 3.7.3 (or above) and Java 6 (or above).
* Set username and password of the Splunk User as well as the host and management port of started Splunk instance in src/main/resources/credentials.properties.
* Run the demo and hit these endpoints for the demo of each processor.
    * **Create Saved Search** - *POST* http://host:port/createsavedsearch
        * Add the name of the Saved Search as an HTTP header with key 'searchname' 
        * Add a valid search query as an HTTP header with key 'searchquery' 
        * e.g. http://localhost:8081/createsavedsearch
    * **Get Saved Searches** - *GET* http://host:port/getsavedsearches
        * e.g. http://localhost:8081/getsavedsearches
    * **Run Saved Search** - *POST* http://host:port/runsavedsearch
        * Add the name of the Saved Search as an HTTP header with key 'searchname' 
        * e.g. http://localhost:8081/runsavedsearch
    * **Delete Saved Search** - *POST* http://host:port/deletesavedsearch
        * Add the name of the Saved Search as an HTTP header with key 'searchname' 
        * e.g. http://localhost:8081/deletesavedsearch
