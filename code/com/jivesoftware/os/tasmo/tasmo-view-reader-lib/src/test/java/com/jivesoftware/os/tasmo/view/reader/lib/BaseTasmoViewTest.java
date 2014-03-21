/*
 * Copyright 2014 pete.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jivesoftware.os.tasmo.view.reader.lib;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.jivesoftware.os.jive.utils.base.interfaces.CallbackStream;
import com.jivesoftware.os.jive.utils.ordered.id.OrderIdProvider;
import com.jivesoftware.os.jive.utils.row.column.value.store.api.RowColumnValueStore;
import com.jivesoftware.os.jive.utils.row.column.value.store.inmemory.RowColumnValueStoreImpl;
import com.jivesoftware.os.tasmo.configuration.EventModel;
import com.jivesoftware.os.tasmo.configuration.EventsModel;
import com.jivesoftware.os.tasmo.configuration.ValueType;
import com.jivesoftware.os.tasmo.configuration.events.TenantEventsProvider;
import com.jivesoftware.os.tasmo.configuration.events.VersionedEventsModel;
import com.jivesoftware.os.tasmo.event.api.JsonEventConventions;
import com.jivesoftware.os.tasmo.event.api.write.Event;
import com.jivesoftware.os.tasmo.event.api.write.EventWriteException;
import com.jivesoftware.os.tasmo.event.api.write.EventWriter;
import com.jivesoftware.os.tasmo.event.api.write.EventWriterOptions;
import com.jivesoftware.os.tasmo.event.api.write.EventWriterResponse;
import com.jivesoftware.os.tasmo.event.api.write.JsonEventWriteException;
import com.jivesoftware.os.tasmo.event.api.write.JsonEventWriter;
import com.jivesoftware.os.tasmo.id.ChainedVersion;
import com.jivesoftware.os.tasmo.id.Id;
import com.jivesoftware.os.tasmo.id.IdProvider;
import com.jivesoftware.os.tasmo.id.IdProviderImpl;
import com.jivesoftware.os.tasmo.id.ImmutableByteArray;
import com.jivesoftware.os.tasmo.id.ObjectId;
import com.jivesoftware.os.tasmo.id.TenantId;
import com.jivesoftware.os.tasmo.id.TenantIdAndCentricId;
import com.jivesoftware.os.tasmo.lib.DispatcherProvider;
import com.jivesoftware.os.tasmo.lib.TasmoViewMaterializer;
import com.jivesoftware.os.tasmo.lib.events.EventValueStore;
import com.jivesoftware.os.tasmo.lib.exists.ExistenceStore;
import com.jivesoftware.os.tasmo.lib.process.bookkeeping.BookkeepingEvent;
import com.jivesoftware.os.tasmo.lib.process.bookkeeping.TasmoEventBookkeeper;
import com.jivesoftware.os.tasmo.lib.process.notification.ViewChangeNotificationProcessor;
import com.jivesoftware.os.tasmo.model.Views;
import com.jivesoftware.os.tasmo.model.ViewsProcessorId;
import com.jivesoftware.os.tasmo.model.ViewsProvider;
import com.jivesoftware.os.tasmo.model.process.JsonWrittenEventProvider;
import com.jivesoftware.os.tasmo.model.process.ModifiedViewProvider;
import com.jivesoftware.os.tasmo.model.process.OpaqueFieldValue;
import com.jivesoftware.os.tasmo.model.process.WrittenEvent;
import com.jivesoftware.os.tasmo.model.process.WrittenEventProvider;
import com.jivesoftware.os.tasmo.reference.lib.ClassAndField_IdKey;
import com.jivesoftware.os.tasmo.reference.lib.ReferenceStore;
import com.jivesoftware.os.tasmo.view.reader.api.ViewReader;
import com.jivesoftware.os.tasmo.view.reader.api.ViewResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

/**
 *
 * @author pete
 */
public class BaseTasmoViewTest {
     public static final TenantId MASTER_TENANT_ID = new TenantId("master");
    IdProvider idProvider;
    OrderIdProvider orderIdProvider;
    TenantId tenantId;
    TenantIdAndCentricId tenantIdAndCentricId;
    Id actorId;
    //UserIdentity userIdentity;
    EventWriter writer;
    ExistenceStore existenceStore;
    EventValueStore eventValueStore;
    ReferenceStore referenceStore;
    DispatcherProvider dispatcherProvider;
    TasmoViewMaterializer materializer;
    ChainedVersion currentVersion = new ChainedVersion("0", "1");
    AtomicReference<VersionedEventsModel> events = new AtomicReference<>();
    TenantEventsProvider eventsProvider;
    AtomicReference<Views> views = new AtomicReference<>();
    ObjectMapper mapper = new ObjectMapper();

    {
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);

    }
    WrittenEventProvider<ObjectNode, JsonNode> eventProvider = new JsonWrittenEventProvider();
    ViewReader<ViewResponse> viewReader;
    final Set<Id> permittedIds = new HashSet<>();
    final Set<Id> existingIds = new HashSet<>();
    
    

    public ViewChangeNotificationProcessor getViewChangeNotificationProcessor() {
        // default is a no op processor
        return new ViewChangeNotificationProcessor() {
            @Override
            public void process(ModifiedViewProvider modifiedViewProvider, WrittenEvent writtenEvent) throws Exception {
            }
        };
    }

    public RowColumnValueStoreProvider getRowColumnValueStoreProvider(final String env) {
        return new RowColumnValueStoreProvider() {
            @Override
            public RowColumnValueStore<TenantId, ObjectId, String, String, RuntimeException> existenceStore() {
                return new RowColumnValueStoreImpl<>();
            }

            @Override
            public RowColumnValueStore<TenantIdAndCentricId, ObjectId, String, OpaqueFieldValue, RuntimeException> eventStore() {
                return new RowColumnValueStoreImpl<>();
            }

            @Override
            public RowColumnValueStore<TenantIdAndCentricId, ImmutableByteArray, ImmutableByteArray, String, RuntimeException> viewValueStore() {
                return new RowColumnValueStoreImpl<>();
            }

            @Override
            public RowColumnValueStore<TenantIdAndCentricId, ClassAndField_IdKey, ObjectId, byte[], RuntimeException> multiLinks() {
                return new RowColumnValueStoreImpl<>();
            }

            @Override
            public RowColumnValueStore<TenantIdAndCentricId, ClassAndField_IdKey, ObjectId, byte[], RuntimeException> multiBackLinks() {
                return new RowColumnValueStoreImpl<>();
            }
        };
    }

    public static interface RowColumnValueStoreProvider {

        RowColumnValueStore<TenantId, ObjectId, String, String, RuntimeException> existenceStore() throws Exception;

        RowColumnValueStore<TenantIdAndCentricId, ObjectId, String, OpaqueFieldValue, RuntimeException> eventStore() throws Exception;

        RowColumnValueStore<TenantIdAndCentricId, ImmutableByteArray, ImmutableByteArray, String, RuntimeException> viewValueStore() throws Exception;

        RowColumnValueStore<TenantIdAndCentricId, ClassAndField_IdKey, ObjectId, byte[], RuntimeException> multiLinks() throws Exception;

        RowColumnValueStore<TenantIdAndCentricId, ClassAndField_IdKey, ObjectId, byte[], RuntimeException> multiBackLinks() throws Exception;
    }


    @BeforeClass
    public void setupPrimordialStuff() {
        orderIdProvider = idProvider();
        idProvider = new IdProviderImpl(orderIdProvider);
        tenantId = new TenantId("test");
        tenantIdAndCentricId = new TenantIdAndCentricId(tenantId, Id.NULL);
        actorId = new Id(1L);
    }

    @BeforeMethod
    public void setupModelAndMaterializer() throws Exception {

        String uuid = UUID.randomUUID().toString();

        RowColumnValueStoreProvider rowColumnValueStoreProvider = getRowColumnValueStoreProvider(uuid);
        RowColumnValueStore<TenantIdAndCentricId, ObjectId, String, OpaqueFieldValue, RuntimeException> eventStore = rowColumnValueStoreProvider.eventStore();
        RowColumnValueStore<TenantId, ObjectId, String, String, RuntimeException> existenceStorage = rowColumnValueStoreProvider.existenceStore();
         RowColumnValueStore<TenantIdAndCentricId, ClassAndField_IdKey, ObjectId, byte[], RuntimeException> multiLinks =
             rowColumnValueStoreProvider.multiLinks();
         RowColumnValueStore<TenantIdAndCentricId, ClassAndField_IdKey, ObjectId, byte[], RuntimeException> multiBackLinks =
             rowColumnValueStoreProvider.multiBackLinks();

        existenceStore = new ExistenceStore(existenceStorage);
        eventValueStore = new EventValueStore(eventStore);

        referenceStore = new ReferenceStore(multiLinks, multiBackLinks);

        TasmoEventBookkeeper tasmoEventBookkeeper = new TasmoEventBookkeeper(
            new CallbackStream<List<BookkeepingEvent>>() {
            @Override
            public List<BookkeepingEvent> callback(List<BookkeepingEvent> value) throws Exception {
                return value;
            }
        });

        eventsProvider = new TenantEventsProvider(MASTER_TENANT_ID, null) {
            @Override
            public VersionedEventsModel getVersionedEventsModel(TenantId tenantId) {
                return events.get();
            }
        };


        dispatcherProvider = new DispatcherProvider(
            eventsProvider,
            referenceStore,
            eventValueStore);

        materializer = new TasmoViewMaterializer(tasmoEventBookkeeper,
            dispatcherProvider, existenceStore, getViewChangeNotificationProcessor());

        writer = new EventWriter(jsonEventWriter(materializer, orderIdProvider));
        
        ViewsProvider viewsProvider = new ViewsProvider() {

            @Override
            public ChainedVersion getCurrentViewsVersion(TenantId tenantId) {
                return currentVersion;
            }

            @Override
            public Views getViews(ViewsProcessorId viewsProcessorId) {
                return views.get();
            }
        };
        
        ViewModelProvider viewModelProvider = new ViewModelProvider(tenantId, viewsProvider);
        BatchingReferenceStore batchingReferenceStore = new BatchingReferenceStore(
            multiLinks, multiBackLinks);
        BatchingEventValueStore batchingEventValueStore = new BatchingEventValueStore(eventStore);
        
        
        viewReader = new ReadTimeViewMaterializer(viewModelProvider, new ReferenceGatherer(batchingReferenceStore),
            new ValueGatherer(batchingEventValueStore), new JsonViewFormatter(mapper, eventProvider), viewPermissionChecker(), existenceChecker());
        
        permittedIds.clear();
        existingIds.clear();
            
    }
    
    private ViewPermissionChecker viewPermissionChecker() {
        return new ViewPermissionChecker() {

            @Override
            public ViewPermissionCheckResult check(TenantId tenantId, Id actorId, final Set<Id> permissionCheckTheseIds) {
                if (permittedIds.isEmpty()) {
                    return new ViewPermissionCheckResult() {

                        @Override
                        public Set<Id> allowed() {
                            return permissionCheckTheseIds;
                        }

                        @Override
                        public Set<Id> denied() {
                            return Collections.emptySet();
                        }

                        @Override
                        public Set<Id> unknown() {
                            return Collections.emptySet();
                        }
                    };
                } else {
                    return new ViewPermissionCheckResult() {

                        @Override
                        public Set<Id> allowed() {
                            return permittedIds;
                        }

                        @Override
                        public Set<Id> denied() {
                            return Sets.difference(permissionCheckTheseIds, permittedIds);
                        }

                        @Override
                        public Set<Id> unknown() {
                            return Collections.emptySet();
                        }
                    };
                }
                
            }
        };
        
    }
    
    private ExistenceChecker existenceChecker() {
        return new ExistenceChecker() {

            @Override
            public Set<Id> check(TenantId tenantId, Set<Id> existenceCheckTheseIds) {
                return existingIds.isEmpty() ? existenceCheckTheseIds : existingIds;
            }
        };
    }

    protected void initModel(String eventsModel, String viewModel) throws Exception {
        StringTokenizer tokenizer = new StringTokenizer(eventsModel, "|");
        EventsModel model = new EventsModel();

        while (tokenizer.hasMoreTokens()) {
            String eventDef = tokenizer.nextToken();
            String[] nameAndFields = eventDef.split(":");
            if (nameAndFields.length != 2) {
                throw new IllegalArgumentException();
            }

            Map<String, ValueType> fields = new HashMap<>();

            for (String fieldDef : nameAndFields[1].split(",")) {
                int idx = fieldDef.indexOf("(");
                if (idx < 0 || !fieldDef.endsWith(")")) {
                    throw new IllegalArgumentException("Field definitions require the form name(type)");
                }
                String fieldName = fieldDef.substring(0, idx);
                String fieldType = fieldDef.substring(idx + 1, fieldDef.indexOf(")"));

                fields.put(fieldName, ValueType.valueOf(fieldType));
            }

            model.addEvent(new EventModel(nameAndFields[0], fields));
        }
        
        Views views = new ViewModelParser(MASTER_TENANT_ID, currentVersion).parse(viewModel);

        initModel(model, views);
    }

    protected void initModel(EventsModel eventsModel, Views viewModel) throws Exception {
        VersionedEventsModel newEvents = new VersionedEventsModel(currentVersion, eventsModel);
        events.set(newEvents);
        
        views.set(viewModel);

    }

    OrderIdProvider idProvider() {
        return new OrderIdProvider() {
            private final AtomicLong id = new AtomicLong();

            @Override
            public long nextId() {
                return id.addAndGet(2); // Have to move by twos so there is room for add vs remove differentiation.
            }
        };
    }

    JsonEventWriter jsonEventWriter(final TasmoViewMaterializer tasmoViewMaterializer, final OrderIdProvider idProvider) {
        return new JsonEventWriter() {
            JsonEventConventions jsonEventConventions = new JsonEventConventions();

            @Override
            public EventWriterResponse write(List<ObjectNode> events, EventWriterOptions options) throws JsonEventWriteException {
                try {
                    List<ObjectId> objectIds = Lists.newArrayList();
                    List<Long> eventIds = Lists.newArrayList();
                    for (ObjectNode w : events) {
                        long eventId = idProvider.nextId();
                        eventIds.add(eventId);
                        jsonEventConventions.setEventId(w, eventId);

                        String instanceClassname = jsonEventConventions.getInstanceClassName(w);
                        ObjectId objectId = new ObjectId(instanceClassname, jsonEventConventions.getInstanceId(w, instanceClassname));
                        objectIds.add(objectId);

                    }

                    List<WrittenEvent> writtenEvents = new ArrayList<>();
                    for (ObjectNode eventNode : events) {
                        writtenEvents.add(eventProvider.convertEvent(eventNode));
                    }

                    tasmoViewMaterializer.process(writtenEvents);

                    return new EventWriterResponse(eventIds, objectIds);

                } catch (Exception ex) {
                    throw new JsonEventWriteException("sad trombone", ex);
                }
            }
        };
    }

    ObjectId write(Event event) throws EventWriteException {
        EventWriterResponse eventWriterResponse = writer.write(event);
        return eventWriterResponse.getObjectIds().get(0);
    }
    
}
