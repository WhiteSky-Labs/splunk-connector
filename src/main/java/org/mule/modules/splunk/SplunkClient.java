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

    public void listenFinishJobs() {
        // Retrieve the collection
        JobCollection jobs = service.getJobs();
        System.out.println("There are " + jobs.size() + " jobs available to 'admin'\n");

        // List the job SIDs
        for (Job job : jobs.values()) {
            System.out.println(job.getName());
        }
    }

    public void listenRun(SplunkConnector.SoftCallback callback) throws IOException {
        while (true) {
            try {
                JobCollection jobs = service.getJobs();
                System.out.println("There are " + jobs.size() + " jobs available to 'admin'\n");
                // List the job SIDs
                for (Job job : jobs.values()) {
                    System.out.println(job.getName() + job.isFinalized());
                    // Specify JSON as the output mode for results
                    JobResultsArgs resultsArgs = new JobResultsArgs();
                    resultsArgs.setOutputMode(JobResultsArgs.OutputMode.JSON);
                    InputStream results = job.getResults(resultsArgs);
                    ResultsReaderJson resultsReader = new ResultsReaderJson(results);
                    Map<String, String> event;
                    System.out.println("\nFormatted results from the search job as JSON\n");
                    resultsReader.close();
                    callback.process(job);
                }
                Thread.sleep(1000);
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
}
