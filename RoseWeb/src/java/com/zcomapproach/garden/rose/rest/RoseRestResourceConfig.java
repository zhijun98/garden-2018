/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.zcomapproach.garden.rose.rest;

import java.util.Set;
import javax.ws.rs.core.Application;

/**
 *
 * @author gail
 */
@javax.ws.rs.ApplicationPath("rest")
public class RoseRestResourceConfig extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet<>();
        addRestResourceClasses(resources);
        return resources;
    }

    /**
     * Do not modify addRestResourceClasses() method.
     * It is automatically populated with
     * all resources defined in the project.
     * If required, comment out calling this method in getClasses().
     */
    private void addRestResourceClasses(Set<Class<?>> resources) {
        resources.add(com.zcomapproach.garden.rose.filter.RoseCorsRestFilter.class);
        resources.add(com.zcomapproach.garden.rose.rest.service.RoseBusinessService.class);
        resources.add(com.zcomapproach.garden.rose.rest.service.RoseCustomerService.class);
        resources.add(com.zcomapproach.garden.rose.rest.service.RoseManagementService.class);
        resources.add(com.zcomapproach.garden.rose.rest.service.RoseTaxcorpService.class);
        resources.add(com.zcomapproach.garden.rose.rest.service.RoseTaxpayerService.class);
        resources.add(com.zcomapproach.garden.rose.rest.service.RoseWelcomeService.class);
        resources.add(com.zcomapproach.garden.rose.rest.tulip.TulipRestService.class);
    }
    
}
