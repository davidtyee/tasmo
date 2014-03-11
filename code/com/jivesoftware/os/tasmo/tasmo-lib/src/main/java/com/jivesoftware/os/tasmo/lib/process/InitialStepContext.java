/*
 * $Revision$
 * $Date$
 *
 * Copyright (C) 1999-$year$ Jive Software. All rights reserved.
 *
 * This software is the proprietary information of Jive Software. Use is subject to license terms.
 */
package com.jivesoftware.os.tasmo.lib.process;

import com.google.common.collect.Lists;
import com.jivesoftware.os.tasmo.event.api.ReservedFields;
import com.jivesoftware.os.tasmo.model.path.ModelPathStep;
import com.jivesoftware.os.tasmo.model.path.ModelPathStepType;
import java.util.List;
import java.util.Set;

/**
 *
 */
public class InitialStepContext {

    protected final ModelPathStep initialModelPathMember;
    protected final int membersSize;
    protected final String viewClassName;
    protected final String modelPathId;

    public InitialStepContext(
        ModelPathStep initialModelPathMember,
        int membersSize,
        String viewclassName,
        String modelPathId) {
        this.initialModelPathMember = initialModelPathMember;
        this.membersSize = membersSize;
        this.viewClassName = viewclassName;
        this.modelPathId = modelPathId;
    }

    public Set<String> getInitialClassNames() {
        return initialModelPathMember.getOriginClassNames();
    }

    public ModelPathStep getInitialModelPathStep() {
        return initialModelPathMember;
    }

    public ModelPathStepType getInitialModelPathStepType() {
        return initialModelPathMember.getStepType();
    }

    public String getRefFieldName() {
        return initialModelPathMember.getRefFieldName();
    }

    public List<String> getInitialFieldNames() {
        List<String> allInitialFieldNames = Lists.newArrayList();

        List<String> fieldNames = initialModelPathMember.getFieldNames();
        if (fieldNames != null && !fieldNames.isEmpty()) {
            allInitialFieldNames.addAll(fieldNames);
        }

        allInitialFieldNames.add(ReservedFields.DELETED);
        return allInitialFieldNames;
    }

    public int getMembersSize() {
        return membersSize;
    }

    @Override
    public String toString() {
        return "InitialStep{ viewClassName=" + viewClassName
            + ", initialModelPathMember=" + initialModelPathMember
            + ", membersSize=" + membersSize
            + ", modelPathId=" + modelPathId
            + '}';
    }
}
