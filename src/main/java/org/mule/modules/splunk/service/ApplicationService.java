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

import com.splunk.Application;
import com.splunk.EntityCollection;

/**
 * Class that provides Application specific functionality
 */
public class ApplicationService extends AbstractService {

    public ApplicationService(AbstractClient client) {
        super(client);
    }

    /**
     * Get All the Applications
     *
     * @return A List of the Applications installed on the splunk instance
     */
    public List<Map<String, Object>> getApplications() {
        List<Map<String, Object>> returnList = new ArrayList<Map<String, Object>>();
        EntityCollection<Application> applications = getService().getApplications();
        for (Application app : applications.values()) {
            returnList.add(processSet(app.entrySet()));
        }
        return returnList;
    }
}
