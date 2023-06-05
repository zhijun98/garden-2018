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

package com.zcomapproach.garden.peony.view.data;

import com.zcomapproach.garden.peony.utils.PeonyDataUtils;
import com.zcomapproach.garden.persistence.peony.TaxcorpCaseBrief;
import com.zcomapproach.garden.persistence.peony.TaxpayerCaseBrief;
import java.util.Objects;

/**
 *
 * @author zhijun98
 */
public class PeonyTaxCaseTreeItemData extends PeonyTreeItemData {

    private String treeItemTitle;
    private TaxpayerCaseBrief taxpayer;
    private TaxcorpCaseBrief taxcorp;

    public PeonyTaxCaseTreeItemData() {
        super("Business Tax Cases", Status.INITIAL);
        this.treeItemTitle = "Business Tax Cases";
    }

    public PeonyTaxCaseTreeItemData(String treeItemTitle) {
        super(treeItemTitle, Status.INITIAL);
        this.treeItemTitle = treeItemTitle;
    }

    public PeonyTaxCaseTreeItemData(TaxpayerCaseBrief taxpayer) {
        super(taxpayer, Status.INITIAL);
        this.taxpayer = taxpayer;
        this.taxcorp = null;
        this.treeItemTitle = null;
    }

    public PeonyTaxCaseTreeItemData(TaxcorpCaseBrief taxcorp) {
        super(taxcorp, Status.INITIAL);
        this.taxpayer = null;
        this.taxcorp = taxcorp;
        this.treeItemTitle = null;
    }

    public PeonyTaxCaseTreeItemData(Object treeItemData, Status treeItemDataMode) {
        super(treeItemData, treeItemDataMode);
        if (treeItemData instanceof TaxpayerCaseBrief){
            taxpayer = (TaxpayerCaseBrief)treeItemData;
        }else if (treeItemData instanceof TaxcorpCaseBrief){
            taxcorp = (TaxcorpCaseBrief)treeItemData;
        }else if (treeItemData instanceof String){
            treeItemTitle = treeItemData.toString();
        }
    }

    public String getTreeItemTitle() {
        return treeItemTitle;
    }

    public void setTreeItemTitle(String treeItemTitle) {
        this.treeItemTitle = treeItemTitle;
    }

    public TaxpayerCaseBrief getTaxpayer() {
        return taxpayer;
    }

    public void setTaxpayer(TaxpayerCaseBrief taxpayer) {
        this.taxpayer = taxpayer;
    }

    public TaxcorpCaseBrief getTaxcorp() {
        return taxcorp;
    }

    public void setTaxcorp(TaxcorpCaseBrief taxcorp) {
        this.taxcorp = taxcorp;
    }
    @Override
    public String toString() {
        Object data = getTreeItemData();
        String result = "Tax Cases";
        if (data instanceof TaxpayerCaseBrief){
            return PeonyDataUtils.generateDataTitle(taxpayer);
        }else if (data instanceof TaxcorpCaseBrief){
            return PeonyDataUtils.generateDataTitle(taxcorp);
        }else if (data instanceof String){
            return treeItemTitle;
        }
        return result;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 71 * hash + Objects.hashCode(getTreeItemData());
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PeonyLocationContactTreeItemData other = (PeonyLocationContactTreeItemData) obj;
        if (!Objects.equals(this.getTreeItemData(), other.getTreeItemData())) {
            return false;
        }
        return true;
    }

    /**
     * if the tree-item-data is STRING, the treeItemTitle returns. Otherwise, the entity UUID returns
     * @return 
     */
    public String getTreeItemUuid() {
        Object data = getTreeItemData();
        if (data instanceof TaxpayerCaseBrief){
            return taxpayer.getTaxpayerCaseUuid();
        }else if (data instanceof TaxcorpCaseBrief){
            return taxcorp.getTaxcorpCaseUuid();
        }else {
            return treeItemTitle;
        }
    }

}
