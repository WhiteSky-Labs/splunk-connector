---
layout: post
title: Splunk Connector 1.0.1 Release Notes
redirect_from: "/release-notes/"
---


**MuleSoft-Certified Connector**

The Splunk Enterprise Server Connector provides the ability to perform most common tasks against the Splunk API in an easy, consistent way. It allows you to not only directly send data from the AnyPoint Platform into the Splunk Index but also to perform a wide variety of searches and indexing operations.

This is the first release version of the Splunk Connector, with comprehensive support for most APIs out of the box.

## Compatibility

<table>
    <tr>
        <th>Application/Service</th>
        <th>Version</th>
    </tr>
    <tr>
        <td>Mule Runtime</td>
        <td>Mule 3.5.3 and above</td>
    </tr>
    <tr>
        <td>Splunk Enterprise Server</td>
        <td>v6.2 and above</td>
    </tr>
    <tr>
        <td>Splunk SDK for Java</td>
        <td>v1.5.0</td>
    </tr>
	<tr>
        <td>Java Runtime</td>
        <td>Java 6 and above</td>
    </tr>
</table>

## Supported Splunk Operations

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

## Fixed Issues

* Splunk SDK 1.3.0 did not support JDK 1.8 out-of-the-box, due to a change in SSL cipher handling. New release switches to TLS1.2 instead of SSLv3 as recommended by Splunk.

## New Features in this Release

* None

## Support Resources

* [Overview Documentation](http://whitesky-labs.github.io/splunk-connector/2016/04/22/splunk-connector/)
* [Splunk SDK for Java Documentation](http://dev.splunk.com/view/java-sdk/SP-CAAAEFH)
* API Documentation is available at [http://whitesky-labs.github.io/splunk-connector/apidocs](http://whitesky-labs.github.io/splunk-connector/apidocs)
* You can report new issues by emailing [support@whiteskylabs.com](mailto:support@whiteskylabs.com).