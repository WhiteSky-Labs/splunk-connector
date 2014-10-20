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
import org.modeshape.common.text.Inflector;
import org.mule.api.ConnectionException;
import org.mule.api.ConnectionExceptionCode;
import org.mule.api.MuleEvent;
import org.mule.common.metadata.*;
import org.mule.modules.splunk.exception.SplunkConnectorException;
import org.mule.transport.polling.MessageProcessorPollingInterceptor;
import org.mule.transport.polling.watermark.Watermark;
import org.quartz.CronExpression;
import org.slf4j.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class SplunkClient {

    private static String ENTITY_PACKAGE = "com.splunk";

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(SplunkClient.class);

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
        metaDataKeyList.add(createKey(SavedSearch.class));
        metaDataKeyList.add(createKey(SavedSearchDispatchArgs.class));
        metaDataKeyList.add(createKey(Job.class));
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
     * Get All the saved searches
     *
     * @return List of Saved Searches
     */
    public List<SavedSearch> getSavedSearches() {
        List<SavedSearch> savedSearchList = new ArrayList<SavedSearch>();
        for (SavedSearch entity : service.getSavedSearches().values()) {
            savedSearchList.add(entity);
        }
        return savedSearchList;
    }

    /**
     * Create a saved search
     *
     * @param searchName  The name of query
     * @param searchQuery The query
     * @return SavedSearch
     */
    public SavedSearch createSavedSearch(String searchName, String searchQuery) throws com.splunk.HttpException, IOException {
        Validate.notEmpty(searchName, "Search Name empty.");
        Validate.notEmpty(searchQuery, "Search Query empty.");
        service.getSavedSearches().create(searchName, searchQuery);
        SavedSearch savedSearch = service.getSavedSearches().get(searchName);
        return savedSearch;
    }

    /**
     * Get Saved Search
     *
     * @param searchName The name of query
     * @return
     */
    public SavedSearch getSavedSearch(String searchName) {
        Validate.notEmpty(searchName, "Search Name empty.");
        return service.getSavedSearches().get(searchName);
    }

    /**
     * Modify Properties
     *
     * @param searchName The name of query
     * @return
     */
    public SavedSearch modifySavedSearch(String searchName, String description,
                                         boolean isSetScheduled, String cronSchedule) {
        Validate.notEmpty(searchName, "Search Name empty.");
        Validate.notEmpty(description, "Description empty.");
        Validate.notNull(isSetScheduled, "Set schedule is null.");
        Validate.notEmpty(cronSchedule, "Cron schedule is empty.");
        SavedSearch savedSearch = service.getSavedSearches().get(searchName);
        savedSearch.setDescription(description);
        savedSearch.setIsScheduled(isSetScheduled);
        savedSearch.setCronSchedule(cronSchedule);
        savedSearch.update();
        return savedSearch;
    }

    /**
     * List the past and current instances (jobs) of the search.
     *
     * @param searchName The The name of query
     * @return List of Job
     */
    public List<Job> getSavedSearchHistory(String searchName) {
        List<Job> jobList = new ArrayList<Job>();
        SavedSearch savedSearch = service.getSavedSearches().get(searchName);
        Validate.notNull(savedSearch, "SavedSearch doesn't exist.");
        for (Job job : savedSearch.history()) {
            jobList.add(job);
        }
        return jobList;
    }

    /**
     * Run a Saved Search
     *
     * @param searchName The name of query
     * @return
     * @throws InterruptedException
     */
    public List<Map<String, Object>> runSavedSearch(String searchName) throws SplunkConnectorException {
        SavedSearch savedSearch = service.getSavedSearches().get(searchName);
        JobArgs jobargs = new JobArgs();
        jobargs.setExecutionMode(JobArgs.ExecutionMode.NORMAL);
        Job job;
        try {
            job = savedSearch.dispatch();
            while (!job.isDone()) {
                Thread.sleep(500);
            }
            return populateEventResponse(job);
        } catch (InterruptedException e) {
            throw new SplunkConnectorException(e.getMessage(), e);
        }
    }

    public List<Map<String, Object>> runSavedSearchWithArguments(String searchName, String searchQuery,
                                                                 Map<String, Object> customArgs,
                                                                 SavedSearchDispatchArgs searchDispatchArgs)
            throws SplunkConnectorException {
        SavedSearch savedSearch = service.getSavedSearches().create(searchName, searchQuery);
        String queryParams = "";
        for (Map.Entry<String, Object> entry : customArgs.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue().toString();
            queryParams += " " + key + "=$args." + key + "$";
            searchDispatchArgs.add("args." + key, value);
        }
        LOGGER.debug("Query Params: " + queryParams);
        Job job;
        try {
            job = savedSearch.dispatch(searchDispatchArgs);
            while (!job.isDone()) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                LOGGER.debug("Done! The job finished with " + job.getEventCount() + " events.");

            }
            return populateEventResponse(job);
        } catch (InterruptedException e) {
            throw new SplunkConnectorException(e.getMessage(), e);
        }
    }

    private List<Map<String, Object>> populateEventResponse(Job job) throws SplunkConnectorException {
        List<Map<String, Object>> searchResponseList = new ArrayList<Map<String, Object>>();
        JobResultsArgs resultsArgs = new JobResultsArgs();
        resultsArgs.setOutputMode(JobResultsArgs.OutputMode.JSON);
        InputStream results = job.getResults(resultsArgs);
        ResultsReaderJson resultsReader;
        try {
            resultsReader = new ResultsReaderJson(results);
            HashMap<String, String> event;
            while ((event = resultsReader.getNextEvent()) != null) {
                Map<String, Object> eventData = new HashMap<String, Object>();
                for (String key : event.keySet()) {
                    System.out.println("   " + key + ":  " + event.get(key));
                    eventData.put(convertToJavaConvention(key), event.get(key));
                }
                searchResponseList.add(eventData);
            }
            resultsReader.close();
            return searchResponseList;
        } catch (IOException e) {
            throw new SplunkConnectorException(e.getMessage(), e);
        }
    }

    /**
     * Delete Saved Search
     *
     * @param searchName The name of query
     * @return
     */
    public boolean deleteSavedSearch(String searchName) {
        SavedSearch savedSearch = service.getSavedSearches().get(searchName);
        savedSearch.remove();
        return true;
    }

    public void listenRun(SplunkConnector.SoftCallback callback) throws IOException {
        while (true) {
            try {
                JobCollection jobs = service.getJobs();
                System.out.println("There are " + jobs.size() + " jobs available to 'admin'\n");
                // List the job SIDs
                for (Job job : jobs.values()) {
                    if (job.isDone()) {
                        JobResultsArgs resultsArgs = new JobResultsArgs();
                        resultsArgs.setOutputMode(JobResultsArgs.OutputMode.JSON);
                        InputStream results = job.getResults(resultsArgs);
                        ResultsReaderJson resultsReader = new ResultsReaderJson(results);
                        // Specify JSON as the output mode for results
                        HashMap<String, String> event;
                        System.out.println("\nFormatted results from the search job as JSON\n");
                        while ((event = resultsReader.getNextEvent()) != null) {
                            for (String key : event.keySet())
                                System.out.println("   " + key + ":  " + event.get(key));
                        }
                        resultsReader.close();
                        //callback.process(job);
                    }
                }
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
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
     * Convert the java key
     *
     * @param key
     * @return
     */
    private String convertToJavaConvention(String key) {
        return Inflector.getInstance().lowerCamelCase(key).replace("_", "");
    }

}
