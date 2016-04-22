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

import com.splunk.Input;
import com.splunk.InputCollection;
import com.splunk.InputKind;
import com.splunk.TcpInput;
import com.splunk.UdpInput;

/**
 * Class that provides Input specific functionality
 */
public class InputService extends AbstractService {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(InputService.class);

    public InputService(AbstractClient client) {
        super(client);
    }

    /**
     * Add data to a tcp input on a given port
     * 
     * @param portNumber
     *            The TCP Port Number to use
     * @param stringData
     *            The data string to add
     * @return Success or Failure
     */
    public boolean addDataToTcpInput(String portNumber, String stringData) {
        try {
            TcpInput input = (TcpInput) getService().getInputs()
                    .get(portNumber);
            input.submit(stringData);
            return true;
        } catch (Exception e) {
            LOGGER.info("Unable to add data to TCP Port", e);
            return false;
        }
    }

    /**
     * Add data to a udp input on a given port
     *
     * @param portNumber
     *            The UDP Port Number to use
     * @param data
     *            The data string to add
     * @return Success or Failure
     */
    public boolean addDataToUdpInput(String portNumber, String data) {
        try {
            UdpInput input = (UdpInput) getService().getInputs()
                    .get(portNumber);
            input.submit(data);
            return true;
        } catch (Exception e) {
            LOGGER.info("Unable to submit to that UDP Port", e);
            return false;
        }
    }

    /**
     * Creates an Input with a given identifier and kind
     *
     * @param inputIdentifier
     *            The name of the domain controller
     * @param kind
     *            The InputKind
     * @param args
     *            An Optional Key-Value Map of Properties to set
     * @return The input
     */
    public Map<String, Object> createInput(String inputIdentifier, InputKind kind, Map<String, Object> args) {
        InputCollection myInputs = getService().getInputs();
        Input input;
        if ((args != null) && (!args.isEmpty())) {
            input = myInputs.create(inputIdentifier, kind, args);
        } else {
            input = myInputs.create(inputIdentifier, kind);
        }
        return processSet(input.entrySet());
    }

    /**
     * Retrieves an Input with the given identifier
     *
     * @param inputIdentifier
     *            The identifier, for example a file path if it is a Monitor Input
     * @return The Input specified.
     */
    public Map<String, Object> getInput(String inputIdentifier) {
        Input input = getService().getInputs()
                .get(inputIdentifier);
        Map<String, Object> mapFromSet = new HashMap<String, Object>();
        if (input != null && input.entrySet() != null) {
            mapFromSet = processSet(input.entrySet());
        }
        return mapFromSet;
    }

    /**
     * Get the Collection of Inputs
     *
     * @return InputCollection of all inputs available to the user
     */
    public List<Map<String, Object>> getInputs() {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        InputCollection inputs = getService().getInputs();
        for (Input input : inputs.values()) {
            result.add(processSet(input.entrySet()));
        }
        return result;
    }

    /**
     * Modifies an input with the properties supplied.
     *
     * @param inputIdentifier
     *            The identifier of the Input to modify
     * @param inputArgs
     *            The map of properties to update
     * @return Returns the modified input.
     */
    public Map<String, Object> modifyInput(String inputIdentifier, Map<String, Object> inputArgs) {
        Input input = getService().getInputs()
                .get(inputIdentifier);
        input.update(inputArgs);
        return processSet(input.entrySet());
    }

    /**
     * Remove an Input
     *
     * @param inputIdentifier
     *            the identifier of the input to remove, for example a port number or filename
     * @return Success or Failure
     */
    public boolean removeInput(String inputIdentifier) {
        try {
            Input input = getService().getInputs()
                    .get(inputIdentifier);
            input.remove();
            return true;
        } catch (Exception e) {
            LOGGER.info("Invalid Input Identifier", e);
            return false;
        }
    }
}
