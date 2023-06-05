/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zcomapproach.garden.peony.view.events;

import com.zcomapproach.garden.persistence.peony.PeonyMemo;

/**
 *
 * @author yinlu
 */
public class PeonyArchivedFileAttentionRequest extends PeonyFaceEvent{
    private final PeonyMemo peonyMemo;

    public PeonyArchivedFileAttentionRequest(PeonyMemo peonyMemo) {
        this.peonyMemo = peonyMemo;
    }

    public PeonyMemo getPeonyMemo() {
        return peonyMemo;
    }
}
