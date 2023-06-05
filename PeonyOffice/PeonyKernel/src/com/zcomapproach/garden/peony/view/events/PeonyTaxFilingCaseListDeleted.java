/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zcomapproach.garden.peony.view.events;

import com.zcomapproach.garden.persistence.peony.PeonyTaxFilingCaseList;

/**
 *
 * @author yinlu
 */
public class PeonyTaxFilingCaseListDeleted extends PeonyFaceEvent {
    private final PeonyTaxFilingCaseList peonyTaxFilingCaseList;

    public PeonyTaxFilingCaseListDeleted(PeonyTaxFilingCaseList peonyTaxFilingCaseList) {
        this.peonyTaxFilingCaseList = peonyTaxFilingCaseList;
    }

    public PeonyTaxFilingCaseList getPeonyTaxFilingCaseList() {
        return peonyTaxFilingCaseList;
    }
}
