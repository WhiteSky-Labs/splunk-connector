package org.mule.modules.splunk.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mule.modules.splunk.SplunkClient;

import com.splunk.Input;
import com.splunk.InputCollection;
import com.splunk.InputKind;
import com.splunk.Service;
import com.splunk.TcpInput;
import com.splunk.UdpInput;

public class InputServiceTest {

    private InputService service;

    @Mock
    Service clientService;
    @Mock
    SplunkClient client;
    @Mock
    InputCollection inputCollection;
    @Mock
    TcpInput tcpInput;
    @Mock
    UdpInput udpInput;
    @Mock
    Input input;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        this.service = spy(new InputService(client));
    }

    @Test
    public void testAddDataToTcpInput() throws Exception {
        when(service.getService()).thenReturn(clientService);
        when(clientService.getInputs()).thenReturn(inputCollection);
        when(inputCollection.get(anyString())).thenReturn(tcpInput);
        doNothing().when(tcpInput)
                .submit(anyString());
        assertEquals(true, service.addDataToTcpInput("8888", "stringData"));
    }

    @Test
    public void testAddDataToTcpInputWithError() throws Exception {
        when(service.getService()).thenReturn(clientService);
        when(clientService.getInputs()).thenReturn(inputCollection);
        when(inputCollection.get(anyString())).thenReturn(tcpInput);
        doThrow(new IOException()).when(tcpInput)
                .submit(anyString());
        assertEquals(false, service.addDataToTcpInput("8888", "stringData"));
    }

    @Test
    public void testAddDataToUdpInput() throws Exception {
        when(service.getService()).thenReturn(clientService);
        when(clientService.getInputs()).thenReturn(inputCollection);
        when(inputCollection.get(anyString())).thenReturn(udpInput);
        doNothing().when(udpInput)
                .submit(anyString());
        assertEquals(true, service.addDataToUdpInput("Test", "Test"));
    }

    @Test
    public void testAddDataToUdpInputWithError() throws Exception {
        when(service.getService()).thenReturn(clientService);
        when(clientService.getInputs()).thenReturn(inputCollection);
        when(inputCollection.get(anyString())).thenReturn(udpInput);
        doThrow(new IOException()).when(udpInput)
                .submit(anyString());
        assertEquals(false, service.addDataToTcpInput("8888", "stringData"));
    }

    @Test
    public void testCreateInputWithoutProperties() {
        when(service.getService()).thenReturn(clientService);
        when(clientService.getInputs()).thenReturn(inputCollection);
        when(inputCollection.create(anyString(), any(InputKind.class))).thenReturn(input);
        assertNotNull(service.createInput("inputName", InputKind.Tcp, null) != null);
    }

    @Test
    public void testCreateInputWithProperties() {
        HashMap<String, Object> props = new HashMap<String, Object>();
        props.put("index", "text_index");
        when(service.getService()).thenReturn(clientService);
        when(clientService.getInputs()).thenReturn(inputCollection);
        when(inputCollection.create(anyString(), any(InputKind.class), eq(props))).thenReturn(input);
        when(input.entrySet()).thenReturn(props.entrySet());
        assertNotNull(service.createInput("inputName", InputKind.Tcp, props) != null);
    }

    @Test
    public void testCreateInputWithEmptyProperties() {
        when(service.getService()).thenReturn(clientService);
        when(clientService.getInputs()).thenReturn(inputCollection);
        when(inputCollection.create(anyString(), any(InputKind.class))).thenReturn(input);
        assertNotNull(service.createInput("inputName", InputKind.Tcp, null) != null);
    }

    @Test
    public void testGetInput() {
        when(service.getService()).thenReturn(clientService);
        when(clientService.getInputs()).thenReturn(inputCollection);
        when(inputCollection.get("inputIdentifier")).thenReturn(input);
        assertTrue(service.getInput("inputIdentifier")
                .size() == 0);
    }

    @Test
    public void testGetInputs() {
        List<Input> inputs = new ArrayList<>();
        inputs.add(input);
        when(service.getService()).thenReturn(clientService);
        when(clientService.getInputs()).thenReturn(inputCollection);
        when(inputCollection.values()).thenReturn(inputs);
        assertTrue(service.getInputs()
                .size() == 1);
    }

    @Test
    public void testModifyInput() {
        Map<String, Object> entry = new HashMap<>();
        entry.put("testKey", "testValue");
        when(service.getService()).thenReturn(clientService);
        when(clientService.getInputs()).thenReturn(inputCollection);
        when(inputCollection.get("inputIdentifier")).thenReturn(input);
        doNothing().when(input)
                .update(entry);
        when(input.entrySet()).thenReturn(entry.entrySet());
        assertTrue(service.modifyInput("inputIdentifier", entry)
                .size() == 1);
    }

    @Test
    public void testRemoveInputReturnTrue() {
        when(service.getService()).thenReturn(clientService);
        when(clientService.getInputs()).thenReturn(inputCollection);
        when(inputCollection.get("inputIdentifier")).thenReturn(input);
        doNothing().when(input)
                .remove();
        assertTrue(service.removeInput("inputIdentifier"));
    }

    @Test
    public void testRemoveInputReturnFalse() {
        when(service.getService()).thenReturn(clientService);
        when(clientService.getInputs()).thenReturn(inputCollection);
        assertTrue(!service.removeInput("inputIdentifier"));
    }
}
