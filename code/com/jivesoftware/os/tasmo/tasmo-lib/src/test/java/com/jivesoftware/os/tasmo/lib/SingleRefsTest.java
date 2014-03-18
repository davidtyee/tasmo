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
package com.jivesoftware.os.tasmo.lib;

import com.jivesoftware.os.tasmo.event.api.ReservedFields;
import com.jivesoftware.os.tasmo.event.api.write.EventBuilder;
import com.jivesoftware.os.tasmo.id.ObjectId;
import java.util.Arrays;
import java.util.Collections;
import org.testng.annotations.Test;

/**
 *
 * @author pete
 */
public class SingleRefsTest extends BaseTasmoTest {

    @Test
    public void testSetAndReassignRef() throws Exception {
        Expectations expectations = initEventModel("User:username(value)|Content:author(ref),location(ref)|Container:owner(ref)");
        ObjectId user1 = write(EventBuilder.create(idProvider, "User", tenantId, actorId).set("userName", "jane").build());
        ObjectId user2 = write(EventBuilder.create(idProvider, "User", tenantId, actorId).set("userName", "john").build());

        ObjectId container1 = write(EventBuilder.create(idProvider, "Container", tenantId, actorId).set("owner", user1).build());
        ObjectId container2 = write(EventBuilder.create(idProvider, "Container", tenantId, actorId).set("owner", user2).build());

        ObjectId contentId1 = write(EventBuilder.create(idProvider, "Content", tenantId, actorId).set("location", container1).
            set("author", user1).build());
        ObjectId contentId2 = write(EventBuilder.create(idProvider, "Content", tenantId, actorId).set("location", container2).
            set("author", user2).build());

        expectations.addReferenceExpectation(container1, "owner", Arrays.asList(user1));
        expectations.addReferenceExpectation(container2, "owner", Arrays.asList(user2));
        expectations.addReferenceExpectation(contentId1, "author", Arrays.asList(user1));
        expectations.addReferenceExpectation(contentId2, "author", Arrays.asList(user2));
        expectations.addReferenceExpectation(contentId1, "location", Arrays.asList(container1));
        expectations.addReferenceExpectation(contentId2, "location", Arrays.asList(container2));
        expectations.addValueExpectation(user1, Arrays.asList("userName"), Arrays.<Object>asList("jane"));
        expectations.addValueExpectation(user2, Arrays.asList("userName"), Arrays.<Object>asList("john"));
        expectations.addExistenceExpectation(user1, true);
        expectations.addExistenceExpectation(user2, true);
        expectations.addExistenceExpectation(container1, true);
        expectations.addExistenceExpectation(container2, true);
        expectations.addExistenceExpectation(contentId1, true);
        expectations.addExistenceExpectation(contentId2, true);
        expectations.assertExpectation(tenantIdAndCentricId);
        expectations.clear();

        write(EventBuilder.update(contentId1, tenantId, actorId).set("location", container2).
            set("author", user2).build());
        write(EventBuilder.update(contentId2, tenantId, actorId).set("location", container1).
            set("author", user1).build());

        write(EventBuilder.update(container1, tenantId, actorId).set("owner", user2).build());
        write(EventBuilder.update(container2, tenantId, actorId).set("owner", user1).build());

        expectations.addReferenceExpectation(container1, "owner", Arrays.asList(user2));
        expectations.addReferenceExpectation(container2, "owner", Arrays.asList(user1));
        expectations.addReferenceExpectation(contentId1, "author", Arrays.asList(user2));
        expectations.addReferenceExpectation(contentId2, "author", Arrays.asList(user1));
        expectations.addReferenceExpectation(contentId1, "location", Arrays.asList(container2));
        expectations.addReferenceExpectation(contentId2, "location", Arrays.asList(container1));
        expectations.assertExpectation(tenantIdAndCentricId);
        expectations.clear();

    }

    @Test
    public void testDeleteRef() throws Exception {
        Expectations expectations = initEventModel("User:username(value)|Content:author(ref),location(ref)|Container:owner(ref)");
        ObjectId user1 = write(EventBuilder.create(idProvider, "User", tenantId, actorId).set("userName", "jane").build());
        ObjectId user2 = write(EventBuilder.create(idProvider, "User", tenantId, actorId).set("userName", "john").build());

        ObjectId container1 = write(EventBuilder.create(idProvider, "Container", tenantId, actorId).set("owner", user1).build());
        ObjectId container2 = write(EventBuilder.create(idProvider, "Container", tenantId, actorId).set("owner", user2).build());

        ObjectId contentId1 = write(EventBuilder.create(idProvider, "Content", tenantId, actorId).set("location", container1).
            set("author", user1).build());
        ObjectId contentId2 = write(EventBuilder.create(idProvider, "Content", tenantId, actorId).set("location", container2).
            set("author", user2).build());

        expectations.addReferenceExpectation(container1, "owner", Arrays.asList(user1));
        expectations.addReferenceExpectation(container2, "owner", Arrays.asList(user2));
        expectations.addReferenceExpectation(contentId1, "author", Arrays.asList(user1));
        expectations.addReferenceExpectation(contentId2, "author", Arrays.asList(user2));
        expectations.addReferenceExpectation(contentId1, "location", Arrays.asList(container1));
        expectations.addReferenceExpectation(contentId2, "location", Arrays.asList(container2));
        expectations.addValueExpectation(user1, Arrays.asList("userName"), Arrays.<Object>asList("jane"));
        expectations.addValueExpectation(user2, Arrays.asList("userName"), Arrays.<Object>asList("john"));
        expectations.assertExpectation(tenantIdAndCentricId);
        expectations.clear();

        write(EventBuilder.update(contentId1, tenantId, actorId).set(ReservedFields.DELETED, true).build());
        write(EventBuilder.update(contentId2, tenantId, actorId).set(ReservedFields.DELETED, true).build());

        expectations.addReferenceExpectation(contentId1, "author", Collections.<ObjectId>emptyList());
        expectations.addReferenceExpectation(contentId2, "author", Collections.<ObjectId>emptyList());
        expectations.addReferenceExpectation(contentId1, "location", Collections.<ObjectId>emptyList());
        expectations.addReferenceExpectation(contentId2, "location", Collections.<ObjectId>emptyList());
        expectations.addExistenceExpectation(contentId1, false);
        expectations.addExistenceExpectation(contentId2, false);
        expectations.assertExpectation(tenantIdAndCentricId);
        expectations.clear();

        write(EventBuilder.update(user1, tenantId, actorId).set(ReservedFields.DELETED, true).build());
        write(EventBuilder.update(user2, tenantId, actorId).set(ReservedFields.DELETED, true).build());

        //this type of cleanup is not supported. we leave the cruft, but event value or existence store testing can tell us things are not referenceable.
//        expectations.addReferenceExpectation(container1, "owner", Collections.<ObjectId>emptyList());
//        expectations.addReferenceExpectation(container2, "owner", Collections.<ObjectId>emptyList());

        expectations.addValueExpectation(user1, Arrays.asList("userName", ReservedFields.INSTANCE_ID), Arrays.<Object>asList(null, null));
        expectations.addValueExpectation(user2, Arrays.asList("userName", ReservedFields.INSTANCE_ID), Arrays.<Object>asList(null, null));
        expectations.addExistenceExpectation(user1, false);
        expectations.addExistenceExpectation(user2, false);
        expectations.assertExpectation(tenantIdAndCentricId);
        expectations.clear();

    }

    public void testClearRef() throws Exception {
        Expectations expectations = initEventModel("User:username(value)|Content:author(ref),location(ref)|Container:owner(ref)");
        ObjectId user1 = write(EventBuilder.create(idProvider, "User", tenantId, actorId).set("userName", "jane").build());
        ObjectId user2 = write(EventBuilder.create(idProvider, "User", tenantId, actorId).set("userName", "john").build());

        ObjectId container1 = write(EventBuilder.create(idProvider, "Container", tenantId, actorId).set("owner", user1).build());
        ObjectId container2 = write(EventBuilder.create(idProvider, "Container", tenantId, actorId).set("owner", user2).build());

        ObjectId contentId1 = write(EventBuilder.create(idProvider, "Content", tenantId, actorId).set("location", container1).
            set("author", user1).build());
        ObjectId contentId2 = write(EventBuilder.create(idProvider, "Content", tenantId, actorId).set("location", container2).
            set("author", user2).build());

        expectations.addReferenceExpectation(container1, "owner", Arrays.asList(user1));
        expectations.addReferenceExpectation(container2, "owner", Arrays.asList(user2));
        expectations.addReferenceExpectation(contentId1, "author", Arrays.asList(user1));
        expectations.addReferenceExpectation(contentId2, "author", Arrays.asList(user2));
        expectations.addReferenceExpectation(contentId1, "location", Arrays.asList(container1));
        expectations.addReferenceExpectation(contentId2, "location", Arrays.asList(container2));
        expectations.addValueExpectation(user1, Arrays.asList("userName"), Arrays.<Object>asList("jane"));
        expectations.addValueExpectation(user2, Arrays.asList("userName"), Arrays.<Object>asList("john"));
        expectations.addExistenceExpectation(user1, true);
        expectations.addExistenceExpectation(user2, true);
        expectations.addExistenceExpectation(container1, true);
        expectations.addExistenceExpectation(container2, true);
        expectations.addExistenceExpectation(contentId1, true);
        expectations.addExistenceExpectation(contentId2, true);
        expectations.assertExpectation(tenantIdAndCentricId);
        expectations.clear();

        write(EventBuilder.update(contentId1, tenantId, actorId).clear("author").clear("location").build());
        write(EventBuilder.update(contentId2, tenantId, actorId).clear("author").clear("location").build());

        expectations.addReferenceExpectation(contentId1, "author", Collections.<ObjectId>emptyList());
        expectations.addReferenceExpectation(contentId2, "author", Collections.<ObjectId>emptyList());
        expectations.addReferenceExpectation(contentId1, "location", Collections.<ObjectId>emptyList());
        expectations.addReferenceExpectation(contentId2, "location", Collections.<ObjectId>emptyList());
        expectations.assertExpectation(tenantIdAndCentricId);
        expectations.clear();

        write(EventBuilder.update(container1, tenantId, actorId).clear("owner").build());
        write(EventBuilder.update(container2, tenantId, actorId).clear("owner").build());

        expectations.addReferenceExpectation(container1, "owner", Collections.<ObjectId>emptyList());
        expectations.addReferenceExpectation(container2, "owner", Collections.<ObjectId>emptyList());
        expectations.addExistenceExpectation(user1, true);
        expectations.addExistenceExpectation(user2, true);
        expectations.addExistenceExpectation(container1, true);
        expectations.addExistenceExpectation(container2, true);
        expectations.addExistenceExpectation(contentId1, true);
        expectations.addExistenceExpectation(contentId2, true);
        expectations.assertExpectation(tenantIdAndCentricId);
        expectations.clear();

    }
}
