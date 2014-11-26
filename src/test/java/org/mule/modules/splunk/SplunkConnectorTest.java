/**
 *
 * (c) 2003-2012 MuleSoft, Inc. This software is protected under international
 * copyright law. All use of this software is subject to MuleSoft's Master
 * Subscription Agreement (or other Terms of Service) separately entered
 * into between you and MuleSoft. If such an agreement is not in
 * place, you may not use the software.
 */

package org.mule.modules.splunk;

import com.splunk.Application;
import com.splunk.Service;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

/**
 * Test {@link org.mule.modules.splunk.SplunkConnector} internals
 * <p/>
 * TODO: Implement Unit Tests once approach is clearer
 */
public class SplunkConnectorTest {

    private SplunkConnector connector;


    @Mock
    SplunkClient client;
    @Mock
    Service service;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        this.connector = new SplunkConnector(client);
        this.connector.setHost("dummyhost");
        this.connector.setPort(8089);
    }

    @Test
    public void testGetApplications() throws Exception {
        List<Application> applist = new ArrayList<Application>();
        when(client.getApplications()).thenReturn(applist);
        assertEquals(applist, connector.getApplications());
    }


}
