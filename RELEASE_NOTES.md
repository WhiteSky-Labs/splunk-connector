---
title: Splunk Connector Release Notes
layout: post
---

Splunk Connector Release Notes
=====================================

Date: 21-January-2015

Version: 1.0.0

Supported Mule Runtime Versions: 3.5.x

Supported API versions
----------------------
[Splunk SDK for Java v 1.3.0](http://dev.splunk.com/view/java-sdk/SP-CAAAECN)


New Features and Functionality
------------------------------
Supported Operations:

* Connect to Splunk:
* Work with Saved Searches
  * List Saved Searches
  * View the History of a Saved Search
  * Create a Saved Search
  * View and Modify the Properties of a Saved Search
  * Run a Saved Search
  * Run a Saved Search with Runtime Arguments
  * Delete a Saved Search
* Work with Searches and Jobs
  * List search jobs for current user
  * Run a normal search and stream output
  * Run a blocking search and display the properties of the Job
  * Run a Oneshot search and display results
  * Run a Real-Time Search (see issues)
  * Run an Export Search
* Work With Data Models
  * Retrieve Data Model
  * Retrieve all Data Models
* Get Data into Splunk
  * List Data Inputs
  * Create a new Data Input
  * View and Modify the Properties of a Data Input
  * Create an Index
  * View and Modify the Properties of a Data Input
  * Clean Events from an Index
  * Add data directly into an Index
  * Add data directly into a Tcp Input
  * Add data directly into a Udp Input
* List a user's applications


Unsupported APIs are most effectively managed through the Splunk Administration Interface. If you have a specific need for an API that is currently unsupported, please contact the MuleSoft for more information.
* Modular Inputs are not supported.
* User and Role Management operations are not supported.
* Data Models and Pivots have limited support.


Closed Issues in this release
-----------------------------

* First Release

Known Issues in this release
----------------------------

* User Management APIs are not currently supported
* Realtime searches never terminate, so will run until the connector is terminated.