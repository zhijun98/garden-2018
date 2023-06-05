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

package com.zcomapproach.garden.rose.i18n;

import com.zcomapproach.garden.rose.util.RoseJsfUtils;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 *
 * @author zhijun98
 */
public class RoseText {
    
    private static final ResourceBundle bundle = ResourceBundle.getBundle("com.zcomapproach.garden.rose.i18n.RoseText", Locale.getDefault());
    private static final ResourceBundle bundle_zh = ResourceBundle.getBundle("com.zcomapproach.garden.rose.i18n.RoseText", Locale.CHINESE);
    
    public static String getText(String key){
        try{
            Locale locale = RoseJsfUtils.getCurrentLocale();
            if (Locale.CHINESE.equals(locale) || Locale.CHINESE.equals(locale)){
                return bundle_zh.getString(key);
            }else{
                return bundle.getString(key);
            }
        }catch (Exception ex){
            return "[Broken-Text] " + key;
        }
    }

}
