/**
 *
 * (c) 2016 WhiteSky Labs, Pty Ltd. This software is protected under international
 * copyright law. All use of this software is subject to WhiteSky Labs' Master
 * Subscription Agreement (or other Terms of Service) separately entered
 * into between you and WhiteSky Labs. If such an agreement is not in
 * place, you may not use the software.
 */
package com.splunk;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.splunk.ResultsReaderJson;

public class IgnoreFieldPropertyResultsReaderJson extends ResultsReaderJson {

    public IgnoreFieldPropertyResultsReaderJson(InputStream inputStream) throws IOException {
        super(inputStream, true);
    }

    @JsonIgnore
    @Override
    public Collection<String> getFields() {
        return super.getFields();
    }

}
