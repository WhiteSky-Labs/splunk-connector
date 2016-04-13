package org.mule.modules.splunk.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.mule.modules.splunk.AbstractClient;

import com.splunk.Job;
import com.splunk.JobCollection;

/**
 * Class that provides Job specific functionality
 */
public class JobService extends AbstractService {

    public JobService(AbstractClient client) {
        super(client);
    }

    /**
     * Get the current users Jobs
     *
     * @return A List of the current user's Job objects retrieved from the Splunk Server
     */
    public List<Map<String, Object>> getJobs() {
        List<Map<String, Object>> returnList = new ArrayList<Map<String, Object>>();
        JobCollection jobs = getService().getJobs();
        for (Job job : jobs.values()) {
            returnList.add(processSet(job.entrySet()));
        }
        return returnList;
    }
}
