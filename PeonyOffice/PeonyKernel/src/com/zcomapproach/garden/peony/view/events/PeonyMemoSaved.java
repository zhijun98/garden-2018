/*
 * Copyright 2018 ZComApproach Inc.
 *
 * Licensed under multiple open source licenses involved in the project (the "Licenses");
 * you may not use this file except in compliance with the Licenses.
 * You may obtain copies of the Licenses at
 *
 *      http://www.zcomapproach.com/licenses
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zcomapproach.garden.peony.view.events;

import com.zcomapproach.garden.persistence.peony.PeonyMemo;

/**
 *
 * @author zhijun98
 */
public class PeonyMemoSaved extends PeonyFaceEvent{

    private final Object memoOwner; //who own this memo
    private final PeonyMemo peonyMemo;
    private final boolean newEntity;

    public PeonyMemoSaved(PeonyMemo peonyMemo){
        this.memoOwner = null;
        this.peonyMemo = peonyMemo;
        this.newEntity = false;
    }

    public PeonyMemoSaved(Object memoOwner, PeonyMemo peonyMemo) {
        this.memoOwner = memoOwner;
        this.peonyMemo = peonyMemo;
        this.newEntity = false;
    }
    
    public PeonyMemoSaved(PeonyMemo peonyMemo, boolean newEntity) {
        this.memoOwner = null;
        this.peonyMemo = peonyMemo;
        this.newEntity = newEntity;
    }

    public PeonyMemoSaved(Object memoOwner, PeonyMemo peonyMemo, boolean newEntity) {
        this.memoOwner = memoOwner;
        this.peonyMemo = peonyMemo;
        this.newEntity = newEntity;
    }

    public Object getMemoOwner() {
        return memoOwner;
    }

    public PeonyMemo getPeonyMemo() {
        return peonyMemo;
    }

    public boolean isNewEntity() {
        return newEntity;
    }

}
