/*
 * $Revision$
 * $Date$
 *
 * Copyright (C) 1999-$year$ Jive Software. All rights reserved.
 *
 * This software is the proprietary information of Jive Software. Use is subject to license terms.
 */
package com.jivesoftware.os.tasmo.view.reader.service.writer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.jivesoftware.os.tasmo.id.Id;
import com.jivesoftware.os.tasmo.id.ObjectId;
import com.jivesoftware.os.tasmo.id.TenantIdAndCentricId;
import java.util.Arrays;
import java.util.Objects;

/**
 * This is the read side of com.jivesoftware.os.tasmo.service.write.ViewFieldChange. If you changes this you will likely need to change
 * ViewFieldChange.
 */
public class ViewWriteFieldChange {

    static public enum Type {

        add, remove
    }
    private final long eventId;
    private final TenantIdAndCentricId tenantIdAndCentricId;
    private final Id actorId;
    private final Type type;
    private final ObjectId viewObjectId;
    private final String modelPathId;
    private final ObjectId[] modelPathInstanceIds;
    private final String value;
    private final long timestamp;

    @JsonCreator
    public ViewWriteFieldChange(
        @JsonProperty("eventId") long eventId,
        @JsonProperty("tenantIdAndCentricId") TenantIdAndCentricId tenantIdAndCentricId,
        @JsonProperty("actorId") Id actorId,
        @JsonProperty("type") Type type,
        @JsonProperty("viewObjectId") ObjectId viewObjectId,
        @JsonProperty("modelPathId") String modelPathId,
        @JsonProperty("modelPathInstanceIds") ObjectId[] modelPathInstanceIds,
        @JsonProperty("value") String value,
        @JsonProperty("timestamp") long timestamp) {
        this.eventId = eventId;
        this.tenantIdAndCentricId = tenantIdAndCentricId;
        this.actorId = actorId;
        this.type = type;
        this.viewObjectId = viewObjectId;
        this.modelPathId = modelPathId;
        this.modelPathInstanceIds = modelPathInstanceIds;
        this.value = value;
        this.timestamp = timestamp;
    }

    public long getEventId() {
        return eventId;
    }

    public TenantIdAndCentricId getTenantIdAndCentricId() {
        return tenantIdAndCentricId;
    }

    public Id getActorId() {
        return actorId;
    }

    public Type getType() {
        return type;
    }

    public ObjectId getViewObjectId() {
        return viewObjectId;
    }

    public String getModelPathId() {
        return modelPathId;
    }

    public ObjectId[] getModelPathInstanceIds() {
        return modelPathInstanceIds;
    }

    public String getValue() {
        return value;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "ViewWriteFieldChange{"
            + "eventId=" + eventId
            + ", tenantIdAndCentricId=" + tenantIdAndCentricId
            + ", actorId=" + actorId
            + ", type=" + type
            + ", viewObjectId=" + viewObjectId
            + ", modelPathId=" + modelPathId
            + ", modelPathInstanceIds=" + Arrays.deepToString(modelPathInstanceIds)
            + ", value=" + value
            + ", timestamp=" + timestamp + '}';
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + (int) (this.eventId ^ (this.eventId >>> 32));
        hash = 67 * hash + Objects.hashCode(this.tenantIdAndCentricId);
        hash = 67 * hash + Objects.hashCode(this.actorId);
        hash = 67 * hash + (this.type != null ? this.type.hashCode() : 0);
        hash = 67 * hash + Objects.hashCode(this.viewObjectId);
        hash = 67 * hash + Objects.hashCode(this.modelPathId);
        hash = 67 * hash + Arrays.deepHashCode(this.modelPathInstanceIds);
        hash = 67 * hash + Objects.hashCode(this.value);
        hash = 67 * hash + (int) (this.timestamp ^ (this.timestamp >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ViewWriteFieldChange other = (ViewWriteFieldChange) obj;
        if (this.eventId != other.eventId) {
            return false;
        }
        if (!Objects.equals(this.tenantIdAndCentricId, other.tenantIdAndCentricId)) {
            return false;
        }
        if (!Objects.equals(this.actorId, other.actorId)) {
            return false;
        }
        if (this.type != other.type) {
            return false;
        }
        if (!Objects.equals(this.viewObjectId, other.viewObjectId)) {
            return false;
        }
        if (!Objects.equals(this.modelPathId, other.modelPathId)) {
            return false;
        }
        if (!Arrays.deepEquals(this.modelPathInstanceIds, other.modelPathInstanceIds)) {
            return false;
        }
        if (!Objects.equals(this.value, other.value)) {
            return false;
        }
        if (this.timestamp != other.timestamp) {
            return false;
        }
        return true;
    }
}
