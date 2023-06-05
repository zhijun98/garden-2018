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

package com.zcomapproach.garden.peony.settings;

import com.zcomapproach.commons.ZcaValidator;
import com.zcomapproach.garden.email.GardenEmailMessage;
import com.zcomapproach.garden.email.GardenEmailSerializer;
import com.zcomapproach.garden.email.GardenEmailUtils;
import com.zcomapproach.garden.email.ISpamRuleManager;
import com.zcomapproach.garden.email.data.GardenEmailFeature;
import com.zcomapproach.garden.email.data.PeonySpamRules;
import com.zcomapproach.garden.util.GardenData;
import com.zcomapproach.commons.nio.ZcaNio;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author zhijun98
 */
public class PeonySpamRuleManager implements ISpamRuleManager{

    private static volatile PeonySpamRuleManager self = null;

    public static PeonySpamRuleManager getSingleton(String loginEmployeeUuid) {
        PeonySpamRuleManager selfLocal = PeonySpamRuleManager.self;
        if (selfLocal == null){
            synchronized (PeonySpamRuleManager.class) {
                selfLocal = PeonySpamRuleManager.self;
                if (selfLocal == null){
                    PeonySpamRuleManager.self = selfLocal = new PeonySpamRuleManager(loginEmployeeUuid);
                }
            }
        }
        return selfLocal;
    }
    
    /**
     * key: GardenEmailFeature.value(); value: spam-text
     */
    private final HashMap<String, TreeSet<String>> spamTextMap = new HashMap<>();
    private final String loginEmployeeUuid;
    private final Path spamRuleFilePath;

    public PeonySpamRuleManager(String loginEmployeeUuid) {
        this.loginEmployeeUuid = loginEmployeeUuid;
        spamRuleFilePath = Paths.get(PeonyProperties.getSingleton().getEmailSerializationFolder() 
                + ZcaNio.fileSeparator() + loginEmployeeUuid+"."+PeonySpamRule.class.getSimpleName());
        deserializeSpameTextSet();
    }
    
    private synchronized void serializeSpameTextSet(){
        try {
            if (Files.isRegularFile(spamRuleFilePath)){
                Files.delete(spamRuleFilePath); //this is for update-case: some serialieed file demands update itself 
            }
            PeonySpamRules peonySpamRules = new PeonySpamRules(loginEmployeeUuid);
            peonySpamRules.loadPeonySpamRules(spamTextMap);
            try(ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(spamRuleFilePath.toFile()))){
                out.writeObject(peonySpamRules);
            }
        }catch(IOException ex){
            Logger.getLogger(GardenEmailSerializer.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
    }
    
    private synchronized void deserializeSpameTextSet(){
        PeonySpamRules peonySpamRules = new PeonySpamRules();
        try{
            if (Files.isRegularFile(spamRuleFilePath)){
                try(ObjectInputStream in = new ObjectInputStream(new FileInputStream(spamRuleFilePath.toFile()))){
                    peonySpamRules = (PeonySpamRules)in.readObject();
                }
            }
            peonySpamRules.reloadSpamTextMap(spamTextMap);
        }catch(IOException | ClassNotFoundException ex){
            //Logger.getLogger(GardenEmailSerializer.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            //StreamCorruptedException ex might be rasied from here
        }
    }

    public synchronized void addPeonySpamRule(PeonySpamRule aPeonySpamRule) {
        if (aPeonySpamRule == null){
            return;
        }
        TreeSet<String> values = spamTextMap.get(aPeonySpamRule.getEmailFeature());
        if (values == null){
            values = new TreeSet<>();
            spamTextMap.put(aPeonySpamRule.getEmailFeature(), values);
        }
        values.add(aPeonySpamRule.getSpamText());
        //serialize
        serializeSpameTextSet();
    }

    public synchronized void removePeonySpamRule(PeonySpamRule aPeonySpamRule) {
        if (aPeonySpamRule == null){
            return;
        }
        TreeSet<String> values = spamTextMap.get(aPeonySpamRule.getEmailFeature());
        if (values != null){
            values.remove(aPeonySpamRule.getSpamText());
            if (values.isEmpty()){
                spamTextMap.remove(aPeonySpamRule.getEmailFeature());
            }
        }
        //serialize
        serializeSpameTextSet();
    }
    
    public synchronized List<PeonySpamRule> getPeonySpamRuleList(){
        List<PeonySpamRule> result = new ArrayList<>();
        Set<String> features = spamTextMap.keySet();
        TreeSet<String> values;
        for (String feature : features){
            if (ZcaValidator.isNotNullEmpty(feature)){
                values = spamTextMap.get(feature);
                if (values != null){
                    for (String value : values){
                        if (ZcaValidator.isNotNullEmpty(value)){
                            result.add(new PeonySpamRule(feature, value));
                        }
                    }//for
                }
            }
        }//for
        return result;
    }
    
    @Override
    public synchronized boolean detectSpam(GardenEmailMessage aGardenEmailMessage) {
        boolean result = false;
        GardenEmailFeature feature;
        TreeSet<String> spamTexts;
        for (String key : spamTextMap.keySet()){
            spamTexts = spamTextMap.get(key);
            if (spamTexts != null){
                feature = GardenEmailFeature.convertEnumValueToType(key);
                for (String spamText : spamTexts){
                    result = isSpamHelper(aGardenEmailMessage, feature, spamText);
                    if (result){
                        break;
                    }
                }//for-loop
            }
            if (result){
                break;
            }
        }//for-loop
        return result;
    }
    
    private static boolean isSpamHelper(GardenEmailMessage aGardenEmailMessage, GardenEmailFeature feature, String spamText){
        String target = "";
        switch (feature){
            case TO:
                target = GardenEmailUtils.convertAddressListToText(aGardenEmailMessage.getToList());
                break;
            case CC:
                target = GardenEmailUtils.convertAddressListToText(aGardenEmailMessage.getCcList());
                break;
            case BCC:
                target = GardenEmailUtils.convertAddressListToText(aGardenEmailMessage.getBccList());
                break;
            case SUBJECT:
                target = aGardenEmailMessage.getSubject();
                break;
            case CONTENT:
                target = aGardenEmailMessage.getPlainContent() + ZcaNio.lineSeparator() 
                        + aGardenEmailMessage.getHtmlContent();
                break;
        }
        if (ZcaValidator.isNullEmpty(target)){
            return false;
        }
        if (target.contains(spamText)){
            return true;
        }
        return GardenData.calculateSimilarityByJaroWinklerStrategy(target, spamText, false, 0.9) > 0.9;
    
    }

    public static class PeonySpamRule {
        private String emailFeature; //GardenEmailFeature.value()
        private String spamText;

        public PeonySpamRule() {
        }

        public PeonySpamRule(String emailFeature, String spamText) {
            this.emailFeature = emailFeature;
            this.spamText = spamText;
        }

        public String getEmailFeature() {
            return emailFeature;
        }

        public void setEmailFeature(String emailFeature) {
            this.emailFeature = emailFeature;
        }

        public String getSpamText() {
            return spamText;
        }

        public void setSpamText(String spamText) {
            this.spamText = spamText;
        }

        public boolean isSpam(GardenEmailMessage aGardenEmailMessage) {
            if (aGardenEmailMessage == null){
                return false;
            }
            return isSpamHelper(aGardenEmailMessage, 
                    GardenEmailFeature.convertEnumValueToType(emailFeature), 
                    spamText);
        }

    }
}
