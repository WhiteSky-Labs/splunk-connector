/**
 * (c) 2003-2012 MuleSoft, Inc. This software is protected under international
 * copyright law. All use of this software is subject to MuleSoft's Master
 * Subscription Agreement (or other Terms of Service) separately entered
 * into between you and MuleSoft. If such an agreement is not in
 * place, you may not use the software.
 **/
package org.mule.modules.splunk;

import com.splunk.Application;
import com.splunk.Service;
import com.splunk.ServiceArgs;
import org.mule.common.metadata.*;

import java.util.LinkedList;
import java.util.List;

public class SplunkClient {

    private String ENTITY_PACKAGE = "";

    private SplunkConnector splunkConnector;

    public SplunkClient(SplunkConnector splunkConnector) {
        this.splunkConnector = splunkConnector;
    }


    private Service service;

    /**
     * Connect to splunk instance
     *
     * @param username
     * @param password
     */
    public void connect(String username, String password) {
        ServiceArgs loginArgs = new ServiceArgs();
        loginArgs.setUsername(username);
        loginArgs.setPassword(password);
        loginArgs.setHost(splunkConnector.getHost());
        loginArgs.setPort(splunkConnector.getPort());
        service = Service.connect(loginArgs);
    }

    /**
     * This will return all the metadata
     *
     * @return List of MetaData
     */
    public List<MetaDataKey> getMetadata() {
        List<MetaDataKey> metaDataKeyList = new LinkedList<MetaDataKey>();
        //metaDataKeyList.add(createKey(Account.class));
        return metaDataKeyList;
    }

    /**
     * Get the MetaData
     *
     * @param key
     * @return
     */
    public MetaData getMetaDataKey(MetaDataKey key) throws ClassNotFoundException {
        Class<?> clazz = Class.forName(String.format("%s.%s", ENTITY_PACKAGE, key.getId()));
        return new DefaultMetaData(new DefaultPojoMetaDataModel(clazz));
    }

    /**
     * Metadata Key creator, takes the ClassName as key
     *
     * @param cls
     * @return
     */
    private MetaDataKey createKey(Class<?> cls) {
        return new DefaultMetaDataKey(cls.getSimpleName(), cls.getSimpleName());
    }

    /**
     * Get All the Applications
     * @return
     */
    public java.util.Collection<Application> getApplications() {
       return service.getApplications().values();
    }

    /**
     * Get the Service
     *
     * @return
     */
    public Service getService() {
        return service;
    }

    /**
     * Set the service
     *
     * @param service
     */
    public void setService(Service service) {
        this.service = service;
    }
}
