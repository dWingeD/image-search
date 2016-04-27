package com.dwinged.boof;


import boofcv.abst.feature.associate.AssociateDescription;
import boofcv.struct.feature.TupleDesc;
import org.ddogleg.struct.FastQueue;


public class AssociateMatcher<TD extends TupleDesc> {

    // Associated descriptions together by minimizing an error metric
    AssociateDescription<TD> associate;

    public AssociateMatcher(AssociateDescription<TD> associate) {
        this.associate = associate;
    }

    public float matchDescriptors(FastQueue<TD> descTemplate, FastQueue<TD> descScene) {
        associate.setSource(descTemplate);
        associate.setDestination(descScene);
        associate.associate();

        int matchCount = associate.getMatches().getSize();
        return ((float) matchCount / (float) descTemplate.getSize()) * 100f;
    }
}
