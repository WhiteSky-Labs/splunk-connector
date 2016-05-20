/**
 *
 * (c) 2016 WhiteSky Labs, Pty Ltd. This software is protected under international
 * copyright law. All use of this software is subject to WhiteSky Labs' Master
 * Subscription Agreement (or other Terms of Service) separately entered
 * into between you and WhiteSky Labs. If such an agreement is not in
 * place, you may not use the software.
 */
package org.mule.modules.splunk.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mule.modules.splunk.AbstractClient;
import org.slf4j.LoggerFactory;

import com.splunk.Job;
import com.splunk.SavedSearch;
import com.splunk.SavedSearchCollection;
import com.splunk.SavedSearchDispatchArgs;
import com.splunk.ServiceArgs;

/**
 * Class that provides SavedSearch specific functionality
 */
public class SavedSearchService extends AbstractService {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(SavedSearchService.class);

    public SavedSearchService(AbstractClient client) {
        super(client);
    }

    /**
     * Create a saved search
     *
     * @param searchName
     *            The name of query
     * @param searchQuery
     *            The query
     * @param searchArgs
     *            Optional Map of Key-Value Pairs of Saved Search Arguments
     * @return A map of the SavedSearch object
     */
    public Map<String, Object> createSavedSearch(String searchName, String searchQuery, Map<String, Object> searchArgs) {
        SavedSearch createdSearch;
        if (searchArgs != null && !searchArgs.isEmpty()) {
            createdSearch = getService().getSavedSearches()
                    .create(searchName, searchQuery, searchArgs);
        } else {
            createdSearch = getService().getSavedSearches()
                    .create(searchName, searchQuery);
        }
        return processSet(createdSearch.entrySet());
    }

    /**
     * Delete Saved Search
     *
     * @param searchName
     *            The name of query
     * @return Success/failure
     */
    public boolean deleteSavedSearch(String searchName) {
        try {
            SavedSearch savedSearch = getService().getSavedSearches()
                    .get(searchName);
            savedSearch.remove();
            return true;
        } catch (Exception e) {
            LOGGER.info("Invalid searchName=" + searchName, e);
            return false;
        }
    }

    /**
     * Get all the saved searches, optionally within a restricted namespace
     *
     * @param app
     *            The Application namespace to restrict the list of searches to
     * @param owner
     *            The user namespace to restrict the searches to
     * @return List of Saved Searches
     */
    public List<Map<String, Object>> getSavedSearches(String app, String owner) {
        List<Map<String, Object>> savedSearchList = new ArrayList<Map<String, Object>>();
        SavedSearchCollection savedSearches = null;
        ServiceArgs namespace = new ServiceArgs();

        if (app != null && !app.isEmpty()) {
            namespace.setApp(app);
        }
        if (owner != null && !owner.isEmpty()) {
            namespace.setOwner(owner);
        }
        savedSearches = getService().getSavedSearches(namespace);

        for (SavedSearch entity : savedSearches.values()) {
            savedSearchList.add(processSet(entity.entrySet()));
        }
        return savedSearchList;
    }

    /**
     * List the past and current instances (jobs) of the search.
     *
     * @param searchName
     *            The (Optional) name of query
     * @param app
     *            The (Optional) application of the namespace
     * @param owner
     *            The (Optional) owner of the namespace
     * @return List of Jobs as maps
     */
    public List<Map<String, Object>> getSavedSearchHistory(String searchName, String app, String owner) {
        List<Job> jobList = new ArrayList<Job>();
        SavedSearchCollection savedSearches = null;
        ServiceArgs namespace = new ServiceArgs();
        if (app != null && !app.isEmpty()) {
            namespace.setApp(app);
        }
        if (owner != null && !owner.isEmpty()) {
            namespace.setOwner(owner);
        }

        if (searchName == null || searchName.isEmpty()) {
            savedSearches = getService().getSavedSearches(namespace);
            for (SavedSearch entity : savedSearches.values()) {
                Collections.addAll(jobList, entity.history());
            }
        } else {
            SavedSearch savedSearch = getService().getSavedSearches(namespace)
                    .get(searchName);
            if (savedSearch != null) {
                Collections.addAll(jobList, savedSearch.history());
            }
        }

        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        for (Job entity : jobList) {
            result.add(processSet(entity.entrySet()));
        }
        return result;
    }

    /**
     * Modify Saved Search Properties
     *
     * @param searchName
     *            The name of query
     * @param searchProperties
     *            The map of search properties to modify
     * @return The Modified Saved Search
     */
    public Map<String, Object> modifySavedSearchProperties(String searchName, Map<String, Object> searchProperties) {
        SavedSearch savedSearch = getService().getSavedSearches()
                .get(searchName);
        savedSearch.update(searchProperties);
        return processSet(savedSearch.entrySet());
    }

    /**
     * Run a Saved Search
     *
     * @param searchName
     *            The name of query
     * @return List of Hashmaps
     * @throws InterruptedException
     *             on search interruption
     * @throws IOException
     *             on search processing error
     */
    public List<Map<String, Object>> runSavedSearch(String searchName) throws InterruptedException, IOException {
        SavedSearch savedSearch = getService().getSavedSearches()
                .get(searchName);
        Job job = savedSearch.dispatch();
        while (!job.isDone()) {
            Thread.sleep(500);
        }
        return populateEventResponse(job);
    }

    /**
     * Run a Saved Search with arguments
     *
     * @param searchName
     *            The name of the searh
     * @param customArgs
     *            Custom Arguments, Optional list of custom arguments to supply
     * @param searchDispatchArgs
     *            Optional list of search dispatch arguments
     * @return The results as a List of Hashmaps
     * @throws InterruptedException
     *             on search interruption
     * @throws IOException
     *             on search processing error
     */
    public List<Map<String, Object>> runSavedSearchWithArguments(String searchName, Map<String, Object> customArgs,
            SavedSearchDispatchArgs searchDispatchArgs) throws InterruptedException, IOException {
        SavedSearch savedSearch = getService().getSavedSearches()
                .get(searchName);
        SavedSearchDispatchArgs newSearchDispatchArgs = new SavedSearchDispatchArgs();

        if (searchDispatchArgs != null) {
            searchDispatchArgs.putAll(searchDispatchArgs);
        }
        processCustomArgs(customArgs, newSearchDispatchArgs);

        Job job = savedSearch.dispatch(newSearchDispatchArgs);
        while (!job.isDone()) {
            Thread.sleep(500);
        }
        return populateEventResponse(job);
    }

    /**
     * View Saved Search Properties
     *
     * @param searchName
     *            The Saved Search's name
     * @param app
     *            The Optional app Namespace to restrict to
     * @param owner
     *            The Optional owner namespace to restrict to
     * @return Map of the properties (the saved search's EntrySet)
     */
    public Set<Map.Entry<String, Object>> viewSavedSearchProperties(String searchName, String app, String owner) {
        ServiceArgs namespace = new ServiceArgs();
        if (owner != null && !owner.isEmpty()) {
            namespace.setOwner(owner);
        }
        if (app != null && !app.isEmpty()) {
            namespace.setApp(app);
        }
        return getService().getSavedSearches(namespace)
                .get(searchName)
                .entrySet();
    }
}
