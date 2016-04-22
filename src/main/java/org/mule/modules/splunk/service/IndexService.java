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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mule.modules.splunk.AbstractClient;
import org.slf4j.LoggerFactory;

import com.splunk.Args;
import com.splunk.CollectionArgs;
import com.splunk.Index;
import com.splunk.IndexCollection;
import com.splunk.IndexCollectionArgs;

/**
 * Class that provides Index specific functionality
 */
public class IndexService extends AbstractService {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(IndexService.class);

    public IndexService(AbstractClient client) {
        super(client);
    }

    /**
     * Add data to an index without an input, using HTTP to submit a string
     *
     * @param indexName
     *            The name of the index to update
     * @param stringData
     *            The data string to send
     * @param indexArgs
     *            Optional map of arguments to apply to the update
     * @return The index that has been updated
     */
    public Map<String, Object> addDataToIndex(String indexName, String stringData, Map<String, Object> indexArgs) {
        Index index = getService().getIndexes()
                .get(indexName);
        if (indexArgs != null && !indexArgs.isEmpty()) {
            Args eventArgs = new Args();
            eventArgs.putAll(indexArgs);
            index.submit(eventArgs, stringData);
        } else {
            index.submit(stringData);
        }
        return processSet(index.entrySet());
    }

    /**
     * Clean the index, which removes all events from it
     *
     * @param indexName
     *            The name of the index to clean
     * @param maxSeconds
     *            Optional how long to wait, -1 is forever (not recommended on a Connector). Default is 180s
     * @return the cleaned index
     */
    public Map<String, Object> cleanIndex(String indexName, int maxSeconds) {
        Index index = getService().getIndexes()
                .get(indexName);
        index = index.clean(maxSeconds);
        return processSet(index.entrySet());
    }

    /**
     * Creates an Index with optional arguments
     *
     * @param indexName
     *            The name of the index to create
     * @param args
     *            Optional key-value pairs of arguments to apply on creation
     * @return the new Index
     */
    public Map<String, Object> createIndex(String indexName, Map<String, Object> args) {
        Index index;
        if ((args != null) && !args.isEmpty()) {
            index = getService().getIndexes()
                    .create(indexName, args);
        } else {
            index = getService().getIndexes()
                    .create(indexName);
        }
        return processSet(index.entrySet());
    }

    /**
     * Creates an Index with optional arguments
     *
     * @param indexName
     *            The name of the index to create
     * @param args
     *            Optional key-value pairs of arguments to apply on creation
     * @return the new Index
     */
    public Map<String, Object> getIndex(String indexIdentifier) {
        Index index = getService().getIndexes()
                .get(indexIdentifier);
        Map<String, Object> mapFromSet = new HashMap<String, Object>();
        if (index != null && index.entrySet() != null) {
            mapFromSet = processSet(index.entrySet());
        }
        return mapFromSet;
    }

    /**
     * Retrieves a collection of indexes based on the criteria provided
     *
     * @param sortKey
     *            The Key to sort by
     * @param sortDirection
     *            The SortDirection to sort by
     * @param collectionParameters
     *            Optional Map of additional arguments to pass to the call
     * @return IndexCollection of indexes
     */
    public List<Map<String, Object>> getIndexes(String sortKey, CollectionArgs.SortDirection sortDirection, Map<String, Object> collectionParameters) {
        IndexCollectionArgs args = new IndexCollectionArgs();
        if ((sortKey != null) && !sortKey.isEmpty()) {
            args.setSortKey(sortKey);
        }
        if (sortDirection != null) {
            args.setSortDirection(sortDirection);
        }
        if ((collectionParameters != null) && (!collectionParameters.isEmpty())) {
            args.putAll(collectionParameters);
        }
        IndexCollection coll;
        if (args.isEmpty()) {
            coll = getService().getIndexes();
        } else {
            coll = getService().getIndexes(args);
        }
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        for (Index index : coll.values()) {
            result.add(processSet(index.entrySet()));
        }
        return result;
    }

    /**
     * Modifies an index with the properties supplied.
     *
     * @param indexName
     *            A Splunk Index to modify.
     * @param indexArgs
     *            The map of arguments to update
     * @return Returns the modified index.
     */
    public Map<String, Object> modifyIndex(String indexName, Map<String, Object> indexArgs) {
        Index index = getService().getIndexes()
                .get(indexName);
        index.update(indexArgs);
        return processSet(index.entrySet());
    }

    /**
     * Remove an index
     *
     * @param indexName
     *            The name of the index to remove
     * @return Success or Failure
     */
    public boolean removeIndex(String indexName) {
        try {
            Index index = getService().getIndexes()
                    .get(indexName);
            index.remove();
            return true;
        } catch (Exception e) {
            LOGGER.info("Invalid Index Name", e);
            return false;
        }
    }
}
