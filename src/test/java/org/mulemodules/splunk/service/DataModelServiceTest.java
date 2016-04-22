/**
 *
 * (c) 2016 WhiteSky Labs, Pty Ltd. This software is protected under international
 * copyright law. All use of this software is subject to WhiteSky Labs' Master
 * Subscription Agreement (or other Terms of Service) separately entered
 * into between you and WhiteSky Labs. If such an agreement is not in
 * place, you may not use the software.
 */
package org.mule.modules.splunk.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mule.modules.splunk.SplunkClient;

import com.splunk.DataModel;
import com.splunk.DataModelCollection;
import com.splunk.Service;

public class DataModelServiceTest {

    private DataModelService service;

    @Mock
    Service clientService;
    @Mock
    SplunkClient client;
    @Mock
    DataModel model;
    @Mock
    DataModelCollection dataModelCollection;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        this.service = spy(new DataModelService(client));
    }

    @Test
    public void testGetDataModel() {
        Map<String, Object> map = new HashMap<>();
        map.put("testKey", "testValue");
        Set<Map.Entry<String, Object>> entrySet = map.entrySet();

        when(service.getService()).thenReturn(clientService);
        when(clientService.getDataModels()).thenReturn(dataModelCollection);
        when(dataModelCollection.get("dataModelName")).thenReturn(model);
        when(model.entrySet()).thenReturn(entrySet);

        Map<String, Object> result = service.getDataModel("dataModelName");
        assertEquals(1, result.size());
        assertEquals(true, result.containsKey("testKey"));
        assertEquals(true, result.containsValue("testValue"));
    }

    @Test
    public void testGetDataModels() {
        List<DataModel> modelList = new ArrayList<>();
        modelList.add(model);
        Map<String, Object> entry = new HashMap<>();
        entry.put("testKey", "testValue");
        Set<Map.Entry<String, Object>> set = entry.entrySet();

        when(service.getService()).thenReturn(clientService);
        when(clientService.getDataModels()).thenReturn(dataModelCollection);
        when(dataModelCollection.values()).thenReturn(modelList);
        when(model.entrySet()).thenReturn(set);
        when(service.processSet(set)).thenReturn(entry);

        List<Map<String, Object>> returnList = service.getDataModels();
        assertEquals(1, returnList.size());
        assertEquals(true, returnList.contains(entry));
    }
}
