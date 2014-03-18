/*
 * $Revision$
 * $Date$
 *
 * Copyright (C) 1999-$year$ Jive Software. All rights reserved.
 *
 * This software is the proprietary information of Jive Software. Use is subject to license terms.
 */
package com.jivesoftware.os.tasmo.lib.process;

import com.jivesoftware.os.jive.utils.base.interfaces.CallbackStream;
import com.jivesoftware.os.tasmo.configuration.EventModel;
import com.jivesoftware.os.tasmo.configuration.EventsModel;
import com.jivesoftware.os.tasmo.configuration.ValueType;
import com.jivesoftware.os.tasmo.id.Id;
import com.jivesoftware.os.tasmo.id.ObjectId;
import com.jivesoftware.os.tasmo.id.TenantId;
import com.jivesoftware.os.tasmo.id.TenantIdAndCentricId;
import com.jivesoftware.os.tasmo.model.process.WrittenEvent;
import com.jivesoftware.os.tasmo.model.process.WrittenInstance;
import com.jivesoftware.os.tasmo.reference.lib.Reference;
import com.jivesoftware.os.tasmo.reference.lib.ReferenceStore;
import java.util.Collection;
import java.util.Map;

/**
 * @author jonathan.colt
 */
public class RefProcessor implements EventProcessor {

    private final WrittenInstanceHelper writtenInstanceHelper;
    private final ReferenceStore referenceStore;
    private final EventsModel eventsModel;

    public RefProcessor(WrittenInstanceHelper writtenInstanceHelper, ReferenceStore referenceStore, EventsModel eventsModel) {
        this.writtenInstanceHelper = writtenInstanceHelper;
        this.referenceStore = referenceStore;
        this.eventsModel = eventsModel;
    }

    @Override
    public boolean process(WrittenEvent writtenEvent) throws Exception {

        boolean wasProcessed = false;

        TenantId tenantId = writtenEvent.getTenantId();
        long writtenOrderId = writtenEvent.getEventId();
        ObjectId objectId = writtenEvent.getWrittenInstance().getInstanceId();
        WrittenInstance writtenInstance = writtenEvent.getWrittenInstance();
        Reference objectInstanceId = new Reference(objectId, writtenOrderId);

        Id userId = writtenEvent.getCentricId();
        TenantIdAndCentricId tenantIdAndCentricId = new TenantIdAndCentricId(tenantId, userId);
        TenantIdAndCentricId globalTenantIdAndCentricId = new TenantIdAndCentricId(tenantId, Id.NULL);
        EventModel event = eventsModel.getEvent(writtenEvent.getWrittenInstance().getInstanceId().getClassName());



        if (writtenEvent.getWrittenInstance().isDeletion()) {
            for (Map.Entry<String, ValueType> entry : event.getEventFields().entrySet()) {
                //this handles things the deleted object references
                ValueType fieldType = entry.getValue();
                if (ValueType.ref.equals(fieldType) || ValueType.refs.equals(fieldType)) {
                    referenceStore.remove_aId_aField(tenantIdAndCentricId, writtenOrderId - 1, objectInstanceId, entry.getKey(),
                        new CallbackStream<Reference>() {
                        @Override
                        public Reference callback(Reference bId) throws Exception {
                            return null;
                        }
                    });

                    referenceStore.remove_aId_aField(globalTenantIdAndCentricId, writtenOrderId - 1, objectInstanceId, entry.getKey(),
                        new CallbackStream<Reference>() {
                        @Override
                        public Reference callback(Reference bId) throws Exception {
                            return null;
                        }
                    });
                }

                //to handle things which reference the deleted object, we'd need to have the model tell us all possible Aclass.Afield pairs could possibly
                //reference this object via an Aclass.Afield.deletedObjectId key. Then, scan each one to find aIds, and for whatever we found, do targeted
                //removals from the A->B mappinds, then remove
                //all the Aclass.Afield.aId -> bId cells. Then we need to delete all the Aclass.Afield.deletedObjectId keys where there were any values.

                //this is why we still want existence store to assert stuff doesn't exist.
            }
        } else {
            for (String fieldName : writtenEvent.getWrittenInstance().getFieldNames()) {
                ValueType fieldType = event.getEventFields().get(fieldName);

                if (ValueType.ref.equals(fieldType) || ValueType.refs.equals(fieldType)) {
//                if (writtenEvent.getWrittenInstance().isDeletion()) {
//                    referenceStore.remove_aId_aField(tenantIdAndCentricId, writtenOrderId, objectInstanceId, fieldName,
//                        new CallbackStream<Reference>() {
//                        @Override
//                        public Reference callback(Reference bId) throws Exception {
//                            return null;
//                        }
//                    });
//                    referenceStore.remove_aId_aField(globalTenantIdAndCentricId, writtenOrderId, objectInstanceId, fieldName,
//                        new CallbackStream<Reference>() {
//                        @Override
//                        public Reference callback(Reference bId) throws Exception {
//                            return null;
//                        }
//                    });
//                } else {
//                    Collection<Reference> bIds = writtenInstanceHelper.getReferencesFromInstanceField(writtenInstance, fieldName, writtenOrderId);
//                    if (bIds != null && !bIds.isEmpty()) {
//                        referenceStore.link_aId_aField_to_bIds(tenantIdAndCentricId, writtenOrderId, objectInstanceId, fieldName, bIds);
//                        referenceStore.link_aId_aField_to_bIds(globalTenantIdAndCentricId, writtenOrderId, objectInstanceId, fieldName, bIds);
//                        wasProcessed |= true;
//                    }
//                }
                    Collection<Reference> bIds = writtenInstanceHelper.getReferencesFromInstanceField(writtenInstance, fieldName, writtenOrderId);
                    if (bIds != null && !bIds.isEmpty()) {
                        referenceStore.link_aId_aField_to_bIds(tenantIdAndCentricId, writtenOrderId, objectInstanceId, fieldName, bIds);
                        referenceStore.link_aId_aField_to_bIds(globalTenantIdAndCentricId, writtenOrderId, objectInstanceId, fieldName, bIds);
                        wasProcessed |= true;
                    }

                    //always speculative remove - need to rework ref store to avoid this
                    referenceStore.remove_aId_aField(tenantIdAndCentricId, writtenOrderId - 1, objectInstanceId, fieldName,
                        new CallbackStream<Reference>() {
                        @Override
                        public Reference callback(Reference bId) throws Exception {
                            return null;
                        }
                    });

                    referenceStore.remove_aId_aField(globalTenantIdAndCentricId, writtenOrderId - 1, objectInstanceId, fieldName,
                        new CallbackStream<Reference>() {
                        @Override
                        public Reference callback(Reference bId) throws Exception {
                            return null;
                        }
                    });

                }
            }
        }

        return wasProcessed;
    }
}
