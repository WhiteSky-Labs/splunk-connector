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

import com.splunk.Application;
import com.splunk.EntityCollection;
import com.splunk.Service;

public class ApplicationServiceTest {

    private ApplicationService service;

    @Mock
    Service clientService;
    @Mock
    SplunkClient client;
    @Mock
    Application application;
    @Mock
    EntityCollection<Application> apps;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        this.service = spy(new ApplicationService(client));
    }

    @Test
    public void testGetApplications() {
        List<Application> applist = new ArrayList<Application>();
        applist.add(application);
        Map<String, Object> entry = new HashMap<>();
        entry.put("testKey", "testValue");
        Set<Map.Entry<String, Object>> set = entry.entrySet();

        when(service.getService()).thenReturn(clientService);
        when(clientService.getApplications()).thenReturn(apps);
        when(apps.values()).thenReturn(applist);
        when(application.entrySet()).thenReturn(set);
        when(service.processSet(set)).thenReturn(entry);

        List<Map<String, Object>> returnList = service.getApplications();
        assertEquals(1, returnList.size());
        assertEquals(true, returnList.contains(entry));
    }
}
