Installation and Usage
----------------------

[Purpose](#purpose)

[Prerequisites](#prerequisites)

[Step 1: Create Concur Developer Sandbox and Partner Application](#step-1-create-concur-developer-sandbox-account)

[Step 2: Install Mule Concur Connector from Update Site](#step-2-install-mule-concur-connector-from-update-site)

[Step 3: Create Demo Project](#step-3-create-demo-project)

[Step 4: Add Global Elements](#step-4-add-global-elements)

[Step 5: Create Get List of Lists Flow](#step-5-create-get-list-of-lists-flow)

[Step 6: Test Get List of Lists Flow](#step-6-test-get-list-of-lists-flow)

[Step 6: Test Get List of Lists Flow](#step-6-test-get-list-of-lists-flow)

[Step 7: Create Get Quick Expenses Flow](#step-7-create-get-quick-expenses-flow)

[Step 8: Test Get Quick Expenses Flow](#step-8-test-get-quick-expenses-flow)

[Flow XML](#flowXML)

[Other Resources](#other)





### Purpose



This document provides detailed instructions on how to install MuleSoft's Concur connector and demonstrates how to build and run a simple demo application that uses this connector.



### Prerequisites



In order to build and run this project you'll need:



* [Developer Sandbox or other valid Concur](http://developer.concur.com) account.

* [MuleStudio](http://www.mulesoft.org/download-mule-esb-community-edition).

* Web browser.





### Step 1: Create Concur Developer Sandbox account


NOTE: You can skip this step if you already have a Concur account set up.

* Create an account on [https://developer.concur.com/register](https://developer.concur.com/register). When you create a Sandbox for the first time, you will be run through the Setup Wizard to configure your instance of Concur to meet your specific requirements. The details will vary from person to person, so advising on how to configure Concur is out of the remit of this document. However, there are some specific requirements to ensure that all the APIs provided with this Connector work correctly:
    * Any expenses you want to use when submitting Expense Reports MUST have Expense Codes configured.
    * The user that you use to configure the Connector's Credentials must have a manager, who is configured to Approve Expenses, if you want to be able to submit Expense Reports

* Log in to [https://https://developer.concur.com/en-us/login](https://developer.concur.com/en-us/login) using your newly created account (or your previously existing Concur account if you are not using a developer sandbox).

* In the Concur Dashboard, choose the **Administration** menu, and then "**Web Services**"

![Choose "Web Services"](images/Step1-1.png)

* Choose "**Register Partner Application**"

* Choose **New** and provide a name and description. Make sure the partner application is set to "Active", and tick the APIs you want to enable access to. It is important that you have enabled access for the APIs you want to use. In this case, we will select them all. Note down the **Key** and **Secret** values and select **OK**

![Note Key and Secret fields](images/Step1-2.png)

### Step 2: Install Mule Connector Connector from update Site



*    In Mule Studio select **Help** \> **Install New Software...**.

*    Select **MuleStudio Cloud Connectors Update Site** in **Work With** drop-down.

*    Check one items from Community folder: **Mule Concur Cloud Connector Mule Studio Extension**  and click **Next**.

*    Follow installation steps.


### Step 3: Create Demo project



*    Run Mule Studio and select **File \> New \> Mule Project** menu item.

*    Type **Demo** as a project name and click **Next**.



![Create Demo project](images/Step3-1.png)



Accept default values on the next screen of project creation wizard and click **Finish**.



![Create Demo project](images/Step3-2.png)



### Step 4: Add Global Elements



*    Double click on **src/main/app/Demo.xml** to open it, select **Global Elements** tab in view.



Add one Global Element.



*    Click on **Create** button and add **Concur Connector** to the configuration.



![Add Concur Connector](images/Step4-1.png)


*     Set Concur Connector parameters that you wrote down before: your username and password (the same ones you used to login to the developer sandbox), the API URL (typically https://www.concursolutions.com for the Developer Sandbox) and your Consumer Key (the value of "Key" when registering the Partner application.



![Concur Connector Parameters](images/Step4-2.png)



### Step 5: Create Get List of Lists Flow



*    Add a new flow by dragging it from the palette and name it **getlists**.



![Create List Users Flow](images/Step5-1.png)



*    Add **HTTP Endpoint** to your flow by dragging it from the palette. Double click it to display properties and enter **getlists** as a path value.



![Create HTTP Endpoint](images/Step5-2.png)



*    Add Concur connector to the new flow and configure its properties according to the following images:



![Create Concur Connector](images/Step5-3.png)


*    Finally, add **Object to JSON** transformer to the flow.



![Object to JSON transformer](images/Step5-4.png)



### Step 6: Test Get List of Lists Flow


* Right-click on **flows/Demo.mflow > Run As / Mule Application

![Run As Mule Application](images/Step6-1.png)

* Check the console to see when the application starts.

            ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

            + Started app 'demo'                                       +

            ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

* Open browser and go to **http://localhost:8081/getlists


* You should receive JSON response like this



![Get Lists response](images/Step6-2.png)



*    Stop mule server.

### Step 7: Create Get Quick Expenses Flow



*    Add a new flow by dragging it from the palette and name it **getquickexpenses**.



![Create Get QuickExpenses Flow](images/Step7-1.png)



*    Add **HTTP Endpoint** to your flow by dragging it from the palette. Double click it to display properties and enter **getquickexpenses** as a path value.



![Create HTTP Endpoint](images/Step7-2.png)



*    Add Concur connector to the new flow and configure its properties according to the following images:



![Create Concur Connector](images/Step7-3.png)


*    Finally, add **Object to JSON** transformer as with the previous example.


### Step 8: Test Search Flow


* Right-click on **flows/Demo.mflow > Run As / Mule Application

![Run As Mule Application](images/Step6-1.png)

* Check the console to see when the application starts.

            ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

            + Started app 'demo'                                       +

            ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

* Open browser and go to **http://localhost:8081/getquickexpenses


* You should receive JSON response like this



![Get Quick Expenses response](images/Step8-2.png)



*    Stop mule server.




### Flow XML



The final flow XML should look like this:



        <?xml version="1.0" encoding="UTF-8"?>

        <mule xmlns:concur="http://www.mulesoft.org/schema/mule/concur" xmlns:http="http://www.mulesoft.org/schema/mule/http" xmlns:json="http://www.mulesoft.org/schema/mule/json" xmlns:tracking="http://www.mulesoft.org/schema/mule/ee/tracking" xmlns="http://www.mulesoft.org/schema/mule/core" xmlns:doc="http://www.mulesoft.org/schema/mule/documentation" xmlns:spring="http://www.springframework.org/schema/beans" version="EE-3.4.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-current.xsd
        http://www.mulesoft.org/schema/mule/core http://www.mulesoft.org/schema/mule/core/current/mule.xsd
        http://www.mulesoft.org/schema/mule/concur http://www.mulesoft.org/schema/mule/concur/1.0/mule-concur.xsd
        http://www.mulesoft.org/schema/mule/ee/tracking http://www.mulesoft.org/schema/mule/ee/tracking/current/mule-tracking-ee.xsd
        http://www.mulesoft.org/schema/mule/http http://www.mulesoft.org/schema/mule/http/current/mule-http.xsd
        http://www.mulesoft.org/schema/mule/json http://www.mulesoft.org/schema/mule/json/current/mule-json.xsd">
            <concur:config name="ConcurConnector" username="your email" password="your password" apiUrl="https://www.concursolutions.com" consumerKey="your key" doc:name="Concur">
                <concur:connection-pooling-profile initialisationPolicy="INITIALISE_ONE" exhaustedAction="WHEN_EXHAUSTED_GROW"/>
            </concur:config>
            <flow name="getlists" doc:name="getlists">
                <http:inbound-endpoint exchange-pattern="request-response" host="localhost" port="8081" path="getlists" doc:name="HTTP"/>
                <concur:get-list-of-lists config-ref="ConcurConnector" doc:name="Concur"/>
                <json:object-to-json-transformer doc:name="Object to JSON"/>
            </flow>
            <flow name="getquickexpenses" doc:name="getquickexpenses">
                <http:inbound-endpoint exchange-pattern="request-response" host="localhost" port="8081" path="getquickexpenses" doc:name="HTTP"/>
                <concur:quick-expense-list config-ref="ConcurConnector" doc:name="GetQuickExpenses"/>
                <json:object-to-json-transformer doc:name="Object to JSON"/>
            </flow>
        </mule>


API Scope
==========

Developer, Standard, Professional and Premium
-----------

The Concur platform is made available to users in a variety of configurations, and further it is also highly configurable. The Concur Connector for Mule has been developed using the Concur Developer Sandbox, and as such has not had access to all APIs and functions, as Concur only offers these to premier partners, or partners who have a specific status.
A good example of this is Travel Requests. While any user can get a list of travel requests, it is not possible for a Developer to create a Travel Request, the user privileges are not sufficient.

Therefore, the endpoints made available by this connector are limited to those which are made available publicly to Developers, as well as those which work according to specification. The various APIs and their availability are described here, and this usage guide is designed to be read in concert with the Concur API Specifications available at https://developer.concur.com/docs-and-resources/documentation.

Attendee
---------

* Attendee List: POST

This API is fully supported, as "Batch Attendee List". All Attendee List operations are managed in batches (including, for example, a "batch" of one), with a supplied Batch Type parameter determining if the batch should be CREATEed or UPDATEed. Batches have a maximum size of 1000, and anything above 1000 in the batch will be ignored. This is confusing, inconsistent behaviour (a "success" response where items in the batch are discarded) so the Connector will throw an exception if a batch of size > 1000 items is submitted.

* Attendee: GET

Fully supported as GET Attendee Details endpoint.

* Attendee Type: GET

This API is not supported at this time.

Expense Report
---------

Posting expense report information is a multi-stage process. Refer to the Processes section of the Expense Report Web Service page for the steps required to post new expense reports and entries.
Note that v1.1 APIs use different API formats from v2.0 APIs, and translation may be required.
An ID for a v1.1 API is of the format "nOlmsYX2xcsvI7b$p$snbhLUZq19M7jxRtk", whereas a 2.0 ID uses a shorter ID without special characters, in the format "425FE2ADB4954FCA90CD".
Unfortunately, APIs are not available in both versions, so the user should be aware of this behaviour.

* Company Card Transaction: GET

This API is not supported at this time.

* Expense Delegator: GET

This API is not supported at this time.

* Expense Entry Attendee: GET

V1.1 of this API is partially supported, but V2.0 is not supported at this time.
GET List of Attendees and GET Attendee Details are both supported.

* Expense Entry Attendee: POST

v1.1 of this API is supported, and operates in a Batch.

* Expense Entry Itemization: POST

This API is not supported at this time

* Expense Entry: GET

Get Expense Entry Details is supported, but note that Report and Entry ID fields returned from some APIs are not completely compatible across endpoints. Concur's behaviour is inconsistent in this area, for example "URI Source: The reportId value is returned in the RptKey element and the entryId value is returned in the RpeKey element by the function Get Full Report Details v1.1. The full URL is provided within the itemurl query string for the Request for the Launch External URL callout, and in the Report-Entry-Details-Url element by the Post Expense Entry function response." Do not expect a Report ID from one Web Service to work with another unless the documentation specifically states so.

* Expense Entry: POST

Posts an expense entry for a given report, after a report header has been created. EntryID is optional, and is only required when a specific entry must be updated.

NOTE: Concur recommends that you post one expense entry per request. Future versions of this endpoint will require this behavior.

* Expense Entry Form Field: GET

This API is not supported at this time.

* Expense Form: GET

This API is not supported at this time.

* Expense Group Configuration: GET

This API is not supported at this time.

* Expense Report Header: POST

This API works in both single header (post Expense Report Header) and batch (post Expense Report Header Batch) modes. Report ID is only needed when updating an existing report.
Note that the input types are different for single headers versus batches.

* Expense Report: GET

V2.0 of this API is supported. GET List of Reports is supported, with a large number of (all optional) search filters as parameters. GET Report Details is supported, but may have inconsistent behaviour based on Concur instance configuration, e.g.
NOTE: Some elements will appear only if the OAuth consumer has the Web Services Admin role. These include: The ReportKey element, the employee's credit card information, and the employee's bank account information, VAT information, Journal entries.
The Mule Connector does not support any of these items, as it has not been reviewed by Concur for security.

* Expense Report: POST

Expense Report Exceptions and Workflow actions are not supported at this time. Expense Report Submit is supported.

* Integration Status: POST

This API is not supported at this time.

* Location: GET

This API is not supported at this time.

Extract
---------

Extracts are not available in the Mule Connector, as they are an alternative integration means. There are no plans to support this resource.

Imaging
---------

The imaging v3.0 APIs are mostly supported in JSON mode. PUT and DELETE Operations are not supported at this time as during development the endpoints did not work as documented. Once the APIs are functional they may be supported.

Itinerary
---------

Itineraries are only partially supported. POST Itinerary Cancellations do not return valid XML, and therefore cannot be parsed and thus are not supported. Other API endpoints are supported as documented.

Bookings
---------

Bookings are only partially supported. POST Booking Cancellations returns HTTP 404, and therefore cannot be parsed and thus are not supported. Other API endpoints are supported as documented.

*Booking: POST

The bookings endpoints are currently not supported.

*Itinerary: GET

All APIs are supported: Get List of Itineraries, Get Itinerary Details

*Itinerary: POST

POST operations are not supported for Itineraries at this time.

List Items
---------

* List: GET

All APIs are supported: Get List of Lists, Get List Details, Get List Items.

* List: POST

List updates are managed in batches, with a batch type parameter determining if the list change will be Create, Update or Delete. Batch limits are not discussed or tested, but it is safe to assume that batches must be less than 1000 or will be ignored, as with other batch API endpoints.

Meeting
---------

Meeting endpoints are only supported in Travel for Concur Professional/Premium. These APIs are not supported by the Mule Connector.

Payment Batch File
---------

GET List of Payment Batches is supported, with an optional status filter parameter. POST Payment Batch Close is also supported, requiring the appropriate BatchID to be supplied.

Purchase Order Web Service
---------

Purchase Order endpoints are only supported in Invoice for Concur Professional/Premium. These APIs are not supported by the Mule Connector.

Quick Expenses
--------

Quick Expense v3.0 APIs are supported, using JSON as the interchange format. All endpoints are supported: GET all quickexpenses, GET QuickExpense by ID, Create a new QuickExpense (POST), Update a QuickExpense by ID (PUT), and DELETE a QuickExpense by ID.

Travel Request
--------

Travel Requests are only partially supported, as an integrator must partner with Concur as an appropriate organisation type (i.e. a Travel Agency, for example). Without the ability to create a travel request, an ID cannot be fetched, so GET Travel Request Details is not supported, nor is POST Travel Request Workflow Action. GET List of Travel Requests _is_ supported, however.

Travel Profile
--------

Travel Profile APIs are fully supported.

Trip Approval
--------

Only POST Trip Approval is supported (the only API). This updates a Trip Approval as either approved or rejected.

User
--------

* GET Employee Form Field

Get List of Employee Form Fields is supported.

* User Password: POST

Update user passwords is not supported at this time.

* User: GET

Get User Information is fully supported.

* User: POST

POST New or Updated users is fully supported. The batch can support up to 500 users only.

TripIt from Concur
--------

TripIt from Concur has not been considered in building the Mule Connector for Concur.

Developer Preview APIs
--------

There are some APIs which are considered "Developer Previews". Connector support for these has not been added at this time due to the likelihood of significant API changes.

Callouts
---------

Callouts are not available in the Concur Connector for Mule. They require extensive specific configuration and cannot be easily "genericised". Using the standard endpoint tools available in Mule ESB, you will be able to integrate callouts, but a Connector cannot assist here.

Connecting to Concur
=========

The Concur Connector uses OAuth Native Flow to connect to Concur. This is described in detail at https://developer.concur.com/api-documentation/oauth-20-0.
In order to allow OAuth connectivity, the Connector must be configured with a username, password, API URL (typically https://www.concursolutions.com) and an API Key.

If you are using a licensed version of Concur, you will be able to setup connectivity by creating an appropriate partner application, in the "Web Services" section of your Concur instance. Details are found at https://developer.concur.com/api-documentation/web-services/core-concepts/partner-applications

With a partner application set up and the appropriate configuration details available, you can use the Concur Connector in your Mule flows. When you add a Concur Connector object to your flow, you can configure it to use a global config reference for your login details the same way as for any other Mule Connector.


### Other Resources



For more information on:



- Concur connector, please visit [http://www.mulesoft.org/connectors/concur-cloud-connector](http://www.mulesoft.org/connectors/concur-cloud-connector)

- Mule AnyPoint™ connectors, please visit [http://www.mulesoft.org/connectors](http://www.mulesoft.org/connectors)

- Mule platform and how to build Mule apps, please visit [http://www.mulesoft.org/documentation/display/current/Home](http://www.mulesoft.org/documentation/display/current/Home)