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

package com.zcomapproach.garden.rose.util;

import com.zcomapproach.garden.guard.RoseWebCipher;
import com.zcomapproach.garden.persistence.GardenJpaUtils;
import com.zcomapproach.garden.persistence.entity.G01Account;
import java.util.HashMap;
import javax.persistence.EntityManager;

/**
 *
 * @author zhijun98
 */
public class RoseDebugger {
    public static String retrieveAccountPassword(EntityManager em, String loginName) throws Exception{
        String sqlQuery = "SELECT g FROM G01Account g WHERE g.loginName = :loginName";
        HashMap<String, Object> params = new HashMap<>();
        params.put("loginName", loginName);
        
        G01Account aG01Account = GardenJpaUtils.findEntityByQuery(em, G01Account.class, sqlQuery, params);
        return RoseWebCipher.getSingleton().decrypt(aG01Account.getEncryptedPassword());
    }
}
