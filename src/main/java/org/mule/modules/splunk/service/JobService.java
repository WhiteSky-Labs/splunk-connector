/**
 *
 * (c) 2016 WhiteSky Labs, Pty Ltd. This software is protected under international
 * copyright law. All use of this software is subject to WhiteSky Labs' Master
 * Subscription Agreement (or other Terms of Service) separately entered
 * into between you and WhiteSky Labs. If such an agreement is not in
 * place, you may not use the software.
 */
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
