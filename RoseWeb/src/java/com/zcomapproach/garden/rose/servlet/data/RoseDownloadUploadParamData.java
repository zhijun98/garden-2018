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

package com.zcomapproach.garden.rose.servlet.data;

import com.zcomapproach.garden.data.GardenFlower;
import com.zcomapproach.garden.data.GardenWebParamValue;
import javax.servlet.http.Part;

/**
 *
 * @author zhijun98
 */
public class RoseDownloadUploadParamData {
    private GardenFlower flower;
    private String fileName;
    private String gmailUuid;
    private String gmailAttUuid;
    private String gmailAddress;
    private String roseCode;
    private GardenWebParamValue purpose;
    private Part fileData;

    public GardenFlower getFlower() {
        return flower;
    }

    public void setFlower(GardenFlower flower) {
        this.flower = flower;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getGmailUuid() {
        return gmailUuid;
    }

    public void setGmailUuid(String gmailUuid) {
        this.gmailUuid = gmailUuid;
    }

    public String getGmailAttUuid() {
        return gmailAttUuid;
    }

    public void setGmailAttUuid(String gmailAttUuid) {
        this.gmailAttUuid = gmailAttUuid;
    }

    public String getGmailAddress() {
        return gmailAddress;
    }

    public void setGmailAddress(String gmailAddress) {
        this.gmailAddress = gmailAddress;
    }

    public String getRoseCode() {
        return roseCode;
    }

    public void setRoseCode(String roseCode) {
        this.roseCode = roseCode;
    }

    public GardenWebParamValue getPurpose() {
        return purpose;
    }

    public void setPurpose(GardenWebParamValue purpose) {
        this.purpose = purpose;
    }

    public Part getFileData() {
        return fileData;
    }

    public void setFileData(Part fileData) {
        this.fileData = fileData;
    }

}
