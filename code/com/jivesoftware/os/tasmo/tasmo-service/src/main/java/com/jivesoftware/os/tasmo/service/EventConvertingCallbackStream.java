/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jivesoftware.os.tasmo.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jivesoftware.os.jive.utils.base.interfaces.CallbackStream;
import com.jivesoftware.os.tasmo.model.process.WrittenEvent;
import com.jivesoftware.os.tasmo.model.process.WrittenEventProvider;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author pete
 */
public class EventConvertingCallbackStream implements CallbackStream<List<ObjectNode>> {

    private final EventIngressCallbackStream eventIngressCallbackStream;
    private final WrittenEventProvider<ObjectNode, JsonNode> writtenEventProvider;

    public EventConvertingCallbackStream(EventIngressCallbackStream eventIngressCallbackStream,
        WrittenEventProvider<ObjectNode, JsonNode> writtenEventProvider) {
        this.eventIngressCallbackStream = eventIngressCallbackStream;
        this.writtenEventProvider = writtenEventProvider;
    }

    @Override
    public List<ObjectNode> callback(List<ObjectNode> objectNodes) throws Exception {
        if (objectNodes != null) {
            List<WrittenEvent> converted = new ArrayList<>(objectNodes.size());
            for (ObjectNode objectNode : objectNodes) {
                converted.add(writtenEventProvider.convertEvent(objectNode));
            }

            eventIngressCallbackStream.callback(converted);
        } else {
            eventIngressCallbackStream.callback(null);
        }

        return objectNodes;
    }
}
