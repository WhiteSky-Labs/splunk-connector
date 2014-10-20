/**
 * (c) 2003-2012 MuleSoft, Inc. This software is protected under international
 * copyright law. All use of this software is subject to MuleSoft's Master
 * Subscription Agreement (or other Terms of Service) separately entered
 * into between you and MuleSoft. If such an agreement is not in
 * place, you may not use the software.
 **/
package org.mule.modules.splunk;

import com.splunk.*;
import org.apache.commons.lang.Validate;
import org.mule.api.ConnectionException;
import org.mule.api.ConnectionExceptionCode;
import org.mule.common.metadata.*;

import javax.validation.Valid;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SplunkClient {

    private static String ENTITY_PACKAGE = "com.splunk";

    private SplunkConnector splunkConnector;

    public SplunkClient(SplunkConnector splunkConnector) {
        this.splunkConnector = splunkConnector;
    }


    private Service service;

    /**
     * Connect to splunk instance
     *
     * @param username
     * @param password
     */
    public void connect(String username, String password) throws ConnectionException {
        try {
            ServiceArgs loginArgs = new ServiceArgs();
            loginArgs.setUsername(username);
            loginArgs.setPassword(password);
            loginArgs.setHost(splunkConnector.getHost());
            loginArgs.setPort(splunkConnector.getPort());
            service = Service.connect(loginArgs);

        } catch (com.splunk.HttpException splunkException) {
            switch (splunkException.getStatus()) {
                case 401:
                case 402:
                case 403:
                    // credentials no good
                    throw new ConnectionException(
                            ConnectionExceptionCode.INCORRECT_CREDENTIALS,
                            Integer.toString(splunkException.getStatus()),
                            splunkException.getMessage());
                case 404:
                    // can't reach
                    throw new ConnectionException(
                            ConnectionExceptionCode.CANNOT_REACH,
                            Integer.toString(splunkException.getStatus()),
                            splunkException.getMessage());
                case 405:
                case 409:
                case 500:
                case 503:
                    throw new ConnectionException(
                            ConnectionExceptionCode.UNKNOWN_HOST,
                            Integer.toString(splunkException.getStatus()),
                            splunkException.getMessage());
                default:
                    // unknown error
                    throw new ConnectionException(
                            ConnectionExceptionCode.UNKNOWN,
                            Integer.toString(splunkException.getStatus()),
                            splunkException.getMessage());
            }

        }

    }

    /**
     * This will return all the metadata
     *
     * @return List of MetaData
     */
    public List<MetaDataKey> getMetadata() {
        List<MetaDataKey> metaDataKeyList = new ArrayList<MetaDataKey>();
        metaDataKeyList.add(createKey(Application.class));
        return metaDataKeyList;
    }

    /**
     * Get the MetaData
     *
     * @param key
     * @return
     */
    public MetaData getMetaDataKey(MetaDataKey key) throws ClassNotFoundException {
        Class<?> clazz = Class.forName(String.format("%s.%s", ENTITY_PACKAGE, key.getId()));
        return new DefaultMetaData(new DefaultPojoMetaDataModel(clazz));
    }

    /**
     * Metadata Key creator, takes the ClassName as key
     *
     * @param cls
     * @return
     */
    private MetaDataKey createKey(Class<?> cls) {
        return new DefaultMetaDataKey(cls.getSimpleName(), cls.getSimpleName());
    }

    /**
     * Get All the Applications
     *
     * @return
     */
    public List<Application> getApplications() {
        return (List<Application>) service.getApplications().values();
    }

    /**
     * Get the Service
     *
     * @return
     */
    public Service getService() {
        return service;
    }

    /**
     * Set the service
     *
     * @param service
     */
    public void setService(Service service) {
        this.service = service;
    }

    /**
     * @return
     */
    public List<Job> getJobs() {
        JobCollection jobs = service.getJobs();
        List<Job> jobList = new ArrayList<Job>();
        for (Job job : jobs.values()) {
            jobList.add(job);
        }
        return jobList;
    }

    /**
     * Run a  search and wait for response
     *
     * @param searchQuery   The sea
     * @param executionMode
     * @return
     */
    public Map<String, Object> runSearch(String searchQuery, JobArgs.ExecutionMode executionMode) {
        Validate.notEmpty(searchQuery, "Search Query is empty.");
        Validate.notNull(executionMode, "Execution mode is empty.");
        JobArgs jobargs = new JobArgs();
        jobargs.setExecutionMode(executionMode);
        Job job = service.getJobs().create(searchQuery, jobargs);
        // Wait for the search to finish
        while (!job.isDone()) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {

            }
        }
        JobResultsArgs resultsArgs = new JobResultsArgs();
        resultsArgs.setOutputMode(JobResultsArgs.OutputMode.JSON);
        InputStream resultsNormalSearch = job.getResults(resultsArgs);
        ResultsReaderJson resultsReader;
        try {
            resultsReader = new ResultsReaderJson(resultsNormalSearch);
            Map<String, String> event;
            while ((event = resultsReader.getNextEvent()) != null) {
                System.out.println("\n****************EVENT****************\n");
                for (String key : event.keySet())
                    System.out.println("   " + key + ":  " + event.get(key));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Get properties of the completed job
        System.out.println("\nSearch job properties\n---------------------");
        System.out.println("Search job ID:         " + job.getSid());
        System.out.println("The number of events:  " + job.getEventCount());
        System.out.println("The number of results: " + job.getResultCount());
        System.out.println("Search duration:       " + job.getRunDuration() + " seconds");
        System.out.println("This job expires in:   " + job.getTtl() + " seconds");

        return null;
    }

    /**
     * Run a basic oneshot search and display results
     * @param searchQuery The search query
     * @param earliestTime The earliest time
     * @param latestTime The latest time
     * @return
     */
    public Map<String, Object> runOneShotSearch(String searchQuery, String earliestTime, String latestTime) {
        Args oneshotSearchArgs = new Args();

        oneshotSearchArgs.put("earliest_time", earliestTime);
        oneshotSearchArgs.put("latest_time", latestTime);
        // The search results are returned directly
        InputStream results_oneshot = service.oneshotSearch(searchQuery, oneshotSearchArgs);

        JobResultsArgs resultsArgs = new JobResultsArgs();
        resultsArgs.setOutputMode(JobResultsArgs.OutputMode.JSON);
        ResultsReaderJson resultsReader;
        try {
             resultsReader = new ResultsReaderJson(results_oneshot);
            System.out.println("Searching everything in a 24-hour time range starting June 19, 12:00pm and displaying 10 results in XML:\n");
            Map<String, String> event;
            while ((event = resultsReader.getNextEvent()) != null) {
                System.out.println("\n********EVENT********");
                for (String key : event.keySet())
                    System.out.println("   " + key + ":  " + event.get(key));
            }
            resultsReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
