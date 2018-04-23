/**
 * (c) 2018 WhiteSky Labs, Pty Ltd. The software in this package is published under the terms of the CPAL v1.0 license,
 * a copy of which has been included with this distribution in the LICENSE.md file.
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
