/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jivesoftware.os.tasmo.view.reader.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jivesoftware.os.tasmo.id.Id;
import com.jivesoftware.os.tasmo.id.ObjectId;
import java.io.IOException;

/**
 *
 */
public class RefCollector implements Collector {

    private final JsonViewMerger merger;
    private final String className;
    private final String fieldName;
    private ObjectNode valueObject;
    private Id activeOrderId = null;

    public RefCollector(JsonViewMerger merger, String className, String fieldName) {
        // Mappers have serializer caches that should be shared between fields, as creating the serializers by far dominates usages of ObjectMapper
        this.merger = merger;
        this.className = className;
        this.fieldName = fieldName;
    }

    @Override
    public void process(Collector[] collectors, int i, Id orderId, String value, long timestamp) throws IOException {
        if (activeOrderId == null || !orderId.equals(activeOrderId)) {
            valueObject = merger.createObjectNode();
            valueObject.put(FieldConstants.VIEW_OBJECT_ID, new ObjectId(className, orderId).toStringForm());
            if (i > 0) {
                collectors[i - 1].link(valueObject, timestamp);
            }
            activeOrderId = orderId;
        }
    }

    @Override
    public void link(JsonNode value, long timestamp) {
        valueObject.put(fieldName, value);
    }

    @Override
    public ObjectNode active() {
        return valueObject;
    }
}
