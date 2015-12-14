Splunk Connector Release Notes
=====================================

Date: 7-December-2015

Version: 1.0.1

Supported Mule Runtime Versions: 3.5.x, 3.6.x, 3.7.x

Supported API versions
----------------------
[Splunk SDK for Java v 1.5.0](http://dev.splunk.com/view/java-sdk/SP-CAAAECN)


New Features and Functionality
------------------------------
No new features

Closed Issues in this release
-----------------------------

* Splunk SDK 1.3.0 did not support JDK 1.8 out-of-the-box, due to a change in SSL cipher handling. New release switches to TLS1.2 instead of SSLv3 as recommended by Splunk.
* testConnection was not correctly detecting expired tokens, so connections would error after the token expiry time. 

Known Issues in this release
----------------------------

* User Management APIs are not currently supported
* Realtime searches never terminate, so will run until the connector is terminated.