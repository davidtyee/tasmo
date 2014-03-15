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

import com.jivesoftware.os.tasmo.id.ObjectId;
import com.jivesoftware.os.tasmo.model.path.ModelPathStep;
import java.util.Set;

/**
 *
 */
public class ReferenceRequest {
    private final ModelPathStep step;
    private final ObjectId objectId;

    public ReferenceRequest(ModelPathStep step, ObjectId objectId) {
        this.step = step;
        this.objectId = objectId;
    }
    
    public ObjectId getObjectId() {
        return objectId;
    }
    
    public boolean isBackReference() {
        return step.getStepType().isBackReferenceType();
    }
    
    public Set<String> getOriginClassNames() {
        return step.getOriginClassNames();
    }
    
    public String getRefFieldName() {
        return step.getRefFieldName();
    }
}
