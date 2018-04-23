/**
 * (c) 2018 WhiteSky Labs, Pty Ltd. The software in this package is published under the terms of the CPAL v1.0 license,
 * a copy of which has been included with this distribution in the LICENSE.md file.
 */
package org.mule.modules.splunk.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mule.modules.splunk.AbstractClient;

import com.splunk.DataModel;
import com.splunk.DataModelCollection;

/**
 * Class that provides DataModel specific functionality
 */
public class DataModelService extends AbstractService {

    public DataModelService(AbstractClient client) {
        super(client);
    }

    /**
     * Retrieve an individual data model
     *
     * @param dataModelName
     *            The data model name to get
     * @return The Data Model requested
     */
    public Map<String, Object> getDataModel(String dataModelName) {
        DataModelCollection dataModelCollection = getService().getDataModels();
        Map<String, Object> mapFromSet = new HashMap<String, Object>();
        if (dataModelCollection.get(dataModelName) != null) {
            mapFromSet = processSet(dataModelCollection.get(dataModelName)
                    .entrySet());
        }
        return mapFromSet;
    }

    /**
     * Retrieve all data models available to the user
     *
     * @return All Data Models available
     */
    public List<Map<String, Object>> getDataModels() {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        DataModelCollection dataModels = getService().getDataModels();
        for (DataModel model : dataModels.values()) {
            result.add(processSet(model.entrySet()));
        }
        return result;
    }
}
