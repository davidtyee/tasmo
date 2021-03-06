/*
 * $Revision$
 * $Date$
 *
 * Copyright (C) 1999-$year$ Jive Software. All rights reserved.
 *
 * This software is the proprietary information of Jive Software. Use is subject to license terms.
 */
package com.jivesoftware.os.tasmo.view.reader.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;

/**
 *
 */
public class JsonViewMerger {

    private final ObjectMapper mapper;

    public JsonViewMerger(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public ObjectNode toObjectNode(byte[] jsonBytes) throws IOException {
        if (jsonBytes == null) {
            return mapper.createObjectNode();
        }
//        if (!jsonBytes.startsWith(JsonToken.START_OBJECT.asString())) {
//            jsonBytes = mapper.readValue(jsonBytes, String.class);
//        }

        return mapper.readValue(jsonBytes, ObjectNode.class);
    }

    public ArrayNode createArrayNode() {
        return mapper.createArrayNode();
    }

    public ObjectNode createObjectNode() {
        return mapper.createObjectNode();
    }
}
