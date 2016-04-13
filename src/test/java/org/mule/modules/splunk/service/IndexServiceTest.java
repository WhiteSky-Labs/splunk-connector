package org.mule.modules.splunk.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mule.modules.splunk.SplunkClient;

import com.splunk.Args;
import com.splunk.CollectionArgs;
import com.splunk.Index;
import com.splunk.IndexCollection;
import com.splunk.IndexCollectionArgs;
import com.splunk.Service;

public class IndexServiceTest {

    private IndexService service;

    @Mock
    Service clientService;
    @Mock
    SplunkClient client;
    @Mock
    IndexCollection indexCollection;
    @Mock
    Index index;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        this.service = spy(new IndexService(client));
    }

    @Test
    public void testAddDataToIndex() {
        when(service.getService()).thenReturn(clientService);
        when(clientService.getIndexes()).thenReturn(indexCollection);
        when(indexCollection.get("indexName")).thenReturn(index);

        doNothing().when(index)
                .submit(anyString());
        Map<String, Object> result = new HashMap<String, Object>();
        assertEquals(result, service.addDataToIndex("indexName", "stringData", null));
    }

    @Test
    public void testAddDataToIndexWithProperties() {
        HashMap<String, Object> indexArgs = new HashMap<String, Object>();
        indexArgs.put("Test", "test");

        when(service.getService()).thenReturn(clientService);
        when(clientService.getIndexes()).thenReturn(indexCollection);
        when(indexCollection.get(anyString())).thenReturn(index);

        doNothing().when(index)
                .submit(any(Args.class), eq("stringData"));
        assertTrue(service.addDataToIndex("indexName", "stringData", indexArgs) != null);
    }

    @Test
    public void testAddDataToIndexWithEmptyProperties() {
        when(service.getService()).thenReturn(clientService);
        when(clientService.getIndexes()).thenReturn(indexCollection);
        when(indexCollection.get(anyString())).thenReturn(index);

        doNothing().when(index)
                .submit(any(Args.class), eq("stringData"));
        assertTrue(service.addDataToIndex("indexName", "stringData", null) != null);
    }

    @Test
    public void testCleanIndex() {
        Map<String, Object> entry = new HashMap<>();
        entry.put("testKey", "testValue");

        when(service.getService()).thenReturn(clientService);
        when(clientService.getIndexes()).thenReturn(indexCollection);
        when(indexCollection.get("indexName")).thenReturn(index);
        when(index.clean(120)).thenReturn(index);
        when(index.entrySet()).thenReturn(entry.entrySet());

        Map<String, Object> result = service.cleanIndex("indexName", 120);
        assertEquals(1, result.size());
        assertEquals(true, result.containsKey("testKey"));
        assertEquals(true, result.containsValue("testValue"));
    }

    @Test
    public void testCreateIndexWithArgs() {
        HashMap<String, Object> args = new HashMap<String, Object>();
        args.put("assureUTF8", "true");

        when(service.getService()).thenReturn(clientService);
        when(clientService.getIndexes()).thenReturn(indexCollection);
        when(indexCollection.create("indexName", args)).thenReturn(index);
        Map<String, Object> result = new HashMap<String, Object>();
        assertEquals(result, service.createIndex("indexName", args));
    }

    @Test
    public void testCreateIndexWithNoArgs() {
        when(service.getService()).thenReturn(clientService);
        when(clientService.getIndexes()).thenReturn(indexCollection);
        when(indexCollection.create("indexName")).thenReturn(index);
        Map<String, Object> result = new HashMap<String, Object>();
        assertEquals(result, service.createIndex("indexName", null));
    }

    @Test
    public void testGetIndex() {
        Map<String, Object> entry = new HashMap<>();
        entry.put("testKey", "testValue");

        when(service.getService()).thenReturn(clientService);
        when(clientService.getIndexes()).thenReturn(indexCollection);
        when(indexCollection.get("indexName")).thenReturn(index);
        when(index.entrySet()).thenReturn(entry.entrySet());

        Map<String, Object> result = service.getIndex("indexName");
        assertEquals(1, result.size());
        assertEquals(true, result.containsKey("testKey"));
        assertEquals(true, result.containsValue("testValue"));
    }

    @Test
    public void testGetIndexesWithParameters() {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("assureUTF8", "true");
        IndexCollectionArgs args = new IndexCollectionArgs();
        args.setSortDirection(CollectionArgs.SortDirection.DESC);
        args.setSortKey("test");
        args.putAll(params);

        when(service.getService()).thenReturn(clientService);
        when(clientService.getIndexes(args)).thenReturn(indexCollection);
        assertTrue(client.getIndexes("test", CollectionArgs.SortDirection.DESC, params) != null);
    }

    @Test
    public void testGetIndexesWithEmptyParameters() {
        HashMap<String, Object> params = new HashMap<String, Object>();
        IndexCollectionArgs args = new IndexCollectionArgs();
        args.setSortDirection(CollectionArgs.SortDirection.DESC);
        args.setSortKey("");
        args.putAll(params);

        when(service.getService()).thenReturn(clientService);
        when(clientService.getIndexes(any(IndexCollectionArgs.class))).thenReturn(indexCollection);
        assertTrue(client.getIndexes("", null, new HashMap<String, Object>()) != null);
    }

    @Test
    public void testModifyIndex() {
        HashMap<String, Object> props = new HashMap<String, Object>();
        props.put("assureUTF8", "true");
        Map<String, Object> entry = new HashMap<>();
        entry.put("testKey", "testValue");

        when(service.getService()).thenReturn(clientService);
        when(clientService.getIndexes()).thenReturn(indexCollection);
        when(indexCollection.get("indexName")).thenReturn(index);
        doNothing().when(index)
                .update(props);
        when(index.entrySet()).thenReturn(entry.entrySet());
        assertEquals(1, service.modifyIndex("indexName", props)
                .size());
    }

    @Test
    public void testRemoveIndex() {
        when(service.getService()).thenReturn(clientService);
        when(clientService.getIndexes()).thenReturn(indexCollection);
        when(indexCollection.get(anyString())).thenReturn(index);
        when(index.remove(anyString())).thenReturn(index);
        assertEquals(true, service.removeIndex("indexName"));
    }
}
