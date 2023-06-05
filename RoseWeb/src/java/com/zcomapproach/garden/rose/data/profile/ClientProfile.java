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

package com.zcomapproach.garden.rose.data.profile;

/**
 *
 * @author zhijun98
 */
public class ClientProfile extends AbstractRoseProfile{

    private String accountUuid;
    private String loginName;
    private String accountEmail;
    private String mobilePhone;
    private String firstName;
    private String lastName;

    public ClientProfile() {
    }

    public ClientProfile(String accountUuid, String loginName, String accountEmail, String mobilePhone, String firstName, String lastName) {
        this.accountUuid = accountUuid;
        this.loginName = loginName;
        this.accountEmail = accountEmail;
        this.mobilePhone = mobilePhone;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getAccountUuid() {
        return accountUuid;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getAccountEmail() {
        return accountEmail;
    }

    public void setAccountEmail(String accountEmail) {
        this.accountEmail = accountEmail;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public String getProfileName() {
        return firstName + " " + lastName;
    }

    @Override
    public String getProfileDescriptiveName() {
        return getProfileName() + " <" + accountEmail + ">";
    }

    @Override
    protected String getProfileUuid() {
        return accountUuid;
    }
}
