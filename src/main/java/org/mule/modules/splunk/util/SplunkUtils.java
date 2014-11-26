/**
 *
 * (c) 2003-2012 MuleSoft, Inc. This software is protected under international
 * copyright law. All use of this software is subject to MuleSoft's Master
 * Subscription Agreement (or other Terms of Service) separately entered
 * into between you and MuleSoft. If such an agreement is not in
 * place, you may not use the software.
 */

package org.mule.modules.splunk.util;

import com.splunk.JobArgs;
import com.splunk.SavedSearch;
import org.apache.commons.lang.WordUtils;
import org.modeshape.common.text.Inflector;
import org.mule.modules.splunk.exception.SplunkConnectorException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by conorcurlett on 13/11/14.
 */
public class SplunkUtils {

    private static final char[] delimiters = new char[]{'_', '.'};

    private static final Set<String> BOOLEAN_PARAMETERS = new HashSet<String>(Arrays.asList(
            new String[]{
                    "action.email.inline",
                    "action.email.reportServerEnabled",
                    "action.email.sendpdf",
                    "action.email.sendresults",
                    "action.email.track_alert",
                    "action.email.width_sort_columns",
                    "action.populate_lookup.track_alert",
                    "action.rss.track_alert",
                    "action.script.track_alert",
                    "action.summary_index.inline",
                    "action.summary_index.track_alert",
                    "alert.digest_mode",
                    "alert.suppress",
                    "disabled",
                    "dispatch.lookups",
                    "dispatch.rt_backfill",
                    "dispatch.spawn_process",
                    "is_scheduled",
                    "is_visible",
                    "realtime_schedule",
                    "restart_on_searchpeer_add",
                    "run_on_startup",
                    "enable_lookups",
                    "force_bundle_replication",
                    "rt_blocking",
                    "rt_indexfilter",
                    "reload_macros",
                    "spawn_process",
                    "sync_bundle_replication"
            }
    ));

    private static final Set<String> INTEGER_PARAMETERS = new HashSet<String>(Arrays.asList(
            new String[]{
                    "action.email.maxresults",
                    "action.populate_lookup.maxresults",
                    "action.rss.maxresults",
                    "action.script.maxresults",
                    "action.summary_index.maxresults",
                    "alert.severity",
                    "dispatch.buckets",
                    "dispatch.max_count",
                    "dispatch.max_time",
                    "dispatch.reduce_freq",
                    "max_concurrent",
                    "auto_cancel",
                    "auto_finalize_ec",
                    "auto_pause",
                    "max_count",
                    "max_time",
                    "rt_maxblocksecs",
                    "rt_queue_size",
                    "reduce_freq",
                    "status_buckets",
                    "timeout"
            }
    ));

    private static final Map<String, String> SPECIAL_CASES = new HashMap<String, String>();

    static {
        SPECIAL_CASES.put("Maxresults", "MaxResults");
        SPECIAL_CASES.put("Sendpdf", "SendPdf");
        SPECIAL_CASES.put("Sendemail", "SendEmail");
        SPECIAL_CASES.put("Rt", "Realtime");
        SPECIAL_CASES.put("Indexfilter", "IndexFilter");
        SPECIAL_CASES.put("Maxblocksecs", "MaximumBlockSeconds");
        SPECIAL_CASES.put("ReduceFreq", "ReduceFrequency");
        SPECIAL_CASES.put("SyncBundleReplication", "SynchronizeBundleReplication");

    }

    private SplunkUtils() {
        super();
    }

    /**
     * Set the Search Properties by reflection
     *
     * @param searchProperties The Properties to set
     * @param search           The SavedSearch to set the properties to
     * @return the SavedSearch with modified properties
     * @throws SplunkConnectorException
     */
    @SuppressWarnings("unchecked")
    public static SavedSearch setSearchProperties(Map<String, Object> searchProperties, SavedSearch search) throws SplunkConnectorException {
        try {
            Class cls = Class.forName("com.splunk.SavedSearch");
            Method method;

            for (Map.Entry<String, Object> entry : searchProperties.entrySet()) {
                Object value = null;
                if (BOOLEAN_PARAMETERS.contains(entry.getKey())) {
                    value = entry.getValue() instanceof String ? Boolean.parseBoolean((String) entry.getValue()) : entry.getValue();
                    method = cls.getDeclaredMethod("set" + convertToIncompleteMethodString(entry.getKey()), boolean.class);
                } else if (INTEGER_PARAMETERS.contains(entry.getKey())) {
                    value = entry.getValue() instanceof String ? Integer.parseInt((String) entry.getValue()) : entry.getValue();
                    method = cls.getDeclaredMethod("set" + convertToIncompleteMethodString(entry.getKey()), int.class);
                } else {
                    value = (String) entry.getValue();
                    method = cls.getDeclaredMethod("set" + convertToIncompleteMethodString(entry.getKey()), String.class);

                }
                method.invoke(search, value);
            }
            return search;
        } catch (IllegalAccessException iae) {
            throw new SplunkConnectorException("Error reflecting Search Properties", iae);
        } catch (InvocationTargetException ite) {
            throw new SplunkConnectorException("Error reflecting Search Properties", ite);
        } catch (NoSuchMethodException nsme) {
            throw new SplunkConnectorException("No matching method, check your properties are correct", nsme);
        } catch (ClassNotFoundException e) {
            throw new SplunkConnectorException("Class not found for com.splunk.SavedSearch - check your build path", e);
        }

    }

    /**
     * Set the JobArgs by reflection
     *
     * @param jobProperties The Properties to set
     * @return the JobArgs with modified properties
     * @throws SplunkConnectorException
     */
    @SuppressWarnings("unchecked")
    public static JobArgs setJobArgs(Map<String, Object> jobProperties) throws SplunkConnectorException {
        try {
            JobArgs jobArgs = new JobArgs();
            Class cls = Class.forName("com.splunk.JobArgs");
            Method method;

            for (Map.Entry<String, Object> entry : jobProperties.entrySet()) {
                Object value = null;
                if (BOOLEAN_PARAMETERS.contains(entry.getKey())) {
                    value = entry.getValue() instanceof String ? Boolean.parseBoolean((String) entry.getValue()) : entry.getValue();
                    method = cls.getDeclaredMethod("set" + convertToIncompleteMethodString(entry.getKey()), boolean.class);
                } else if (INTEGER_PARAMETERS.contains(entry.getKey())) {
                    value = entry.getValue() instanceof String ? Integer.parseInt((String) entry.getValue()) : entry.getValue();
                    method = cls.getDeclaredMethod("set" + convertToIncompleteMethodString(entry.getKey()), int.class);
                } else {
                    value = (String) entry.getValue();
                    method = cls.getDeclaredMethod("set" + convertToIncompleteMethodString(entry.getKey()), String.class);
                }
                method.invoke(jobArgs, value);
            }
            return jobArgs;
        } catch (IllegalAccessException iae) {
            throw new SplunkConnectorException("Error reflecting Job Properties", iae);
        } catch (InvocationTargetException ite) {
            throw new SplunkConnectorException("Error reflecting Job Properties", ite);
        } catch (NoSuchMethodException nsme) {
            throw new SplunkConnectorException("No matching method, check your properties are correct", nsme);
        } catch (ClassNotFoundException e) {
            throw new SplunkConnectorException("Class not found for com.splunk.JobArgs - check your build path", e);
        }

    }

    /**
     * Convert to Java naming standards for use in a reflection method call
     *
     * @param key - the String key to convert to the java convention (camelCase) with an upper case first letter
     * @return A string converted from javascript (naming_conventions) to Java namingConventions
     */
    private static String convertToIncompleteMethodString(String key) {
        String removedDelimiters = WordUtils.capitalizeFully(key, delimiters).replace("_", "").replace(".", "");
        // cater for special cases
        for (Map.Entry<String, String> entry : SPECIAL_CASES.entrySet()) {
            if (removedDelimiters.indexOf(entry.getKey()) != -1) {
                removedDelimiters = removedDelimiters.replaceAll(entry.getKey(), entry.getValue());
            }
        }
        return Inflector.getInstance().upperCamelCase(removedDelimiters);
    }


}
