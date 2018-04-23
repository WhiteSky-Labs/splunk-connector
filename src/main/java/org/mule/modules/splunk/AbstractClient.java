/**
 * (c) 2018 WhiteSky Labs, Pty Ltd. The software in this package is published under the terms of the CPAL v1.0 license,
 * a copy of which has been included with this distribution in the LICENSE.md file.
 */
package org.mule.modules.splunk;

import org.mule.modules.splunk.service.ApplicationService;
import org.mule.modules.splunk.service.DataModelService;
import org.mule.modules.splunk.service.IndexService;
import org.mule.modules.splunk.service.InputService;
import org.mule.modules.splunk.service.JobService;
import org.mule.modules.splunk.service.SavedSearchService;
import org.mule.modules.splunk.service.SearchService;

import com.splunk.Service;

public abstract class AbstractClient {

    private Service service;
    protected ApplicationService applicationService;
    protected DataModelService dataModelService;
    protected IndexService indexService;
    protected InputService inputService;
    protected JobService jobService;
    protected SavedSearchService savedSearchService;
    protected SearchService searchService;

    /**
     * Get the Service
     *
     * @return The Service object
     */
    public Service getService() {
        return service;
    }

    protected void setService(Service service) {
        this.service = service;
        initServices();
    }

    protected ApplicationService getApplicationService() {
        return applicationService;
    }

    protected DataModelService getDataModelService() {
        return dataModelService;
    }

    protected IndexService getIndexService() {
        return indexService;
    }

    protected InputService getInputService() {
        return inputService;
    }

    protected JobService getJobService() {
        return jobService;
    }

    protected SavedSearchService getSavedSearchService() {
        return savedSearchService;
    }

    protected SearchService getSearchService() {
        return searchService;
    }

    private void initServices() {
        this.applicationService = new ApplicationService(this);
        this.dataModelService = new DataModelService(this);
        this.indexService = new IndexService(this);
        this.inputService = new InputService(this);
        this.jobService = new JobService(this);
        this.savedSearchService = new SavedSearchService(this);
        this.searchService = new SearchService(this);
    }
}
