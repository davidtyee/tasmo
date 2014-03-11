package com.jivesoftware.os.tasmo.local;


import com.jivesoftware.os.jive.utils.ordered.id.OrderIdProviderImpl;
import com.jivesoftware.os.tasmo.event.api.write.EventWriter;
import com.jivesoftware.os.tasmo.event.api.write.EventWriterResponse;
import com.jivesoftware.os.tasmo.id.Id;
import com.jivesoftware.os.tasmo.id.IdProvider;
import com.jivesoftware.os.tasmo.id.IdProviderImpl;
import com.jivesoftware.os.tasmo.id.ObjectId;
import com.jivesoftware.os.tasmo.id.TenantId;
import com.jivesoftware.os.tasmo.local.LocalMaterializationSystem;
import com.jivesoftware.os.tasmo.local.LocalMaterializationSystemBuilder;
import com.jivesoftware.os.tasmo.view.reader.api.ModelAdapterFactory;
import com.jivesoftware.os.tasmo.view.reader.api.ViewDescriptor;
import com.jivesoftware.os.tasmo.view.reader.api.ViewReader;
import com.jivesoftware.os.tasmo.view.reader.api.ViewResponse;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 *
 */
public class LocalMaterializationSystemBuilderTest {

    

    /*
     * @Test
    private void createAndUseMaterializer(boolean useHBase) throws Exception {
        LocalMaterializationSystem localMaterializationSystem = null;
        try {

            localMaterializationSystem =
                new LocalMaterializationSystemBuilder().build();

            IdProvider idProvider = new IdProviderImpl(new OrderIdProviderImpl(345));

            TenantId tenantId = new TenantId("booya");
            Id actor = new Id(6457);

            //PlatformMonitorEventBuilder platformMonitorEventBuilder = new PlatformMonitorEventBuilder(idProvider,
            //    tenantId, actor);

            long lastTraceValue = 12345;

            //platformMonitorEventBuilder.setLastTraceId(lastTraceValue);

            EventWriter writer = localMaterializationSystem.getWriter();
            ViewReader<ViewResponse> reader = localMaterializationSystem.getReader();

            Assert.assertNotNull(writer);
            Assert.assertNotNull(reader);

            EventWriterResponse response = writer.write(platformMonitorEventBuilder.build());
            List<ObjectId> objectIds = response.getObjectIds();

            Assert.assertNotNull(objectIds);
            Assert.assertEquals(objectIds.size(), 1);

            Class<PlatformMonitorView> viewClass = PlatformMonitorView.class;

            ObjectId viewId = new ObjectId(viewClass.getSimpleName(), objectIds.get(0).getId());

            ViewResponse viewResponse = reader.readView(new ViewDescriptor(tenantId, actor, viewId));

            Assert.assertEquals(viewResponse.getStatusCode(), ViewResponse.StatusCode.OK);

            PlatformMonitorView platformMonitorView = ModelAdapterFactory.createModelAdapter(viewResponse.getViewBody(), viewClass);

            Assert.assertNotNull(platformMonitorView);
            Assert.assertEquals(platformMonitorView.lastTraceId(), lastTraceValue);
            Assert.assertEquals(platformMonitorView.viewBase().getObjectId(), objectIds.get(0));
        } finally {
            if (localMaterializationSystem != null) {
                localMaterializationSystem.shutDown();
            }

        }

    }
    */
}