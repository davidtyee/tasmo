package com.jivesoftware.os.tasmo.lib;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.MoreExecutors;
import com.jivesoftware.os.tasmo.event.api.JsonEventConventions;
import com.jivesoftware.os.tasmo.id.ChainedVersion;
import com.jivesoftware.os.tasmo.id.Id;
import com.jivesoftware.os.tasmo.id.TenantId;
import com.jivesoftware.os.tasmo.model.ViewBinding;
import com.jivesoftware.os.tasmo.model.Views;
import com.jivesoftware.os.tasmo.model.ViewsProcessorId;
import com.jivesoftware.os.tasmo.model.ViewsProvider;
import com.jivesoftware.os.tasmo.model.path.ModelPath;
import com.jivesoftware.os.tasmo.model.path.ModelPathStep;
import com.jivesoftware.os.tasmo.model.path.ModelPathStepType;
import com.jivesoftware.os.tasmo.model.path.StringHashcodeViewPathKeyProvider;
import com.jivesoftware.os.tasmo.reference.lib.ReferenceStore;
import com.jivesoftware.os.tasmo.reference.lib.concur.ConcurrencyStore;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 *
 * @author jonathan.colt
 */
public class TasmoViewModelTest {

    private final ObjectMapper mapper = new ObjectMapper();
    private final TenantId tenantId = new TenantId("master");
    private ViewsProvider viewsProvider;
    private TasmoViewModel tasmoViewModel;
    private ConcurrencyStore concurrencyStore;
    private ReferenceStore referenceStore;

    @BeforeMethod
    public void setUpMethod() throws Exception {

        viewsProvider = Mockito.mock(ViewsProvider.class);
        concurrencyStore = Mockito.mock(ConcurrencyStore.class);
        referenceStore = Mockito.mock(ReferenceStore.class);
        tasmoViewModel = new TasmoViewModel(MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(8)),
                tenantId,
                viewsProvider,
                new StringHashcodeViewPathKeyProvider(),
                concurrencyStore,
                referenceStore);
    }

    @AfterMethod
    public void tearDownMethod() throws Exception {
    }

    /**
     * Test of loadModel method, of class TasmoViewModel.
     */
    @Test
    public void testLoadModel() throws Exception {
        System.out.println("loadModel");

        ChainedVersion version1 = new ChainedVersion("0", "1");
        ChainedVersion version2 = new ChainedVersion("1", "2");
        Views views1 = makeViews("Foo", version1, "A", "x", "y", "z");
        Views views2 = makeViews("Bar", version2, "B", "j", "k", "l");

        ObjectNode event = mapper.createObjectNode();
        ObjectNode instance = mapper.createObjectNode();
        instance.put("j", "j");
        instance.put("k", "k");
        instance.put("l", "l");

        JsonEventConventions jec = new JsonEventConventions();
        jec.setActorId(event, new Id(1));
        jec.setUserId(event, new Id(1));
        jec.setEventId(event, 2);
        jec.setInstanceClassName(event, "Bar");
        jec.setTenantId(event, tenantId);
        jec.setInstanceId(event, new Id(3), "Bar");
        jec.setInstanceNode(event, "Bar", instance);

        Mockito.when(viewsProvider.getCurrentViewsVersion(tenantId)).thenReturn(version1, version2);
        Mockito.when(viewsProvider.getViews(Mockito.any(ViewsProcessorId.class))).thenReturn(views1, views2);

        tasmoViewModel.loadModel(tenantId);
        VersionedTasmoViewModel first = tasmoViewModel.getVersionedTasmoViewModel(tenantId);
        Assert.assertEquals(version1, first.getVersion());
        Assert.assertTrue(first.getDispatchers().containsKey("A"));
        Assert.assertFalse(first.getDispatchers().containsKey("B"));

        tasmoViewModel.loadModel(tenantId);
        VersionedTasmoViewModel second = tasmoViewModel.getVersionedTasmoViewModel(tenantId);
        Assert.assertNotEquals(first, second);
        Assert.assertEquals(version2, second.getVersion());
        Assert.assertTrue(second.getDispatchers().containsKey("B"));
        Assert.assertFalse(second.getDispatchers().containsKey("A"));

    }

    private Views makeViews(String className, ChainedVersion version, String pathName, String... fieldNames) {
        List<ModelPath> modelPaths = new ArrayList<>();
        modelPaths.add(ModelPath.builder(pathName)
                .addPathMember(new ModelPathStep(true, Sets.newHashSet(pathName), null, ModelPathStepType.value, null, Arrays.asList(fieldNames)))
                .build());
        ViewBinding viewBinding = new ViewBinding(className, modelPaths, false, false, false, null);
        List<ViewBinding> viewBindings = new ArrayList<>();
        viewBindings.add(viewBinding);
        return new Views(tenantId, version, viewBindings);
    }
}
