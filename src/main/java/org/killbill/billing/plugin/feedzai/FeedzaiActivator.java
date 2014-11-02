/*
 * Copyright 2014 The Billing Project, LLC
 *
 * The Billing Project licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package org.killbill.billing.plugin.feedzai;

import org.killbill.billing.osgi.api.OSGIPluginProperties;
import org.killbill.billing.plugin.feedzai.mock.MockFeedzaiClient;
import org.killbill.billing.routing.plugin.api.PaymentRoutingPluginApi;
import org.killbill.clients.feedzai.DefaultFeedzaiClient;
import org.killbill.clients.feedzai.FeedzaiClient;
import org.killbill.killbill.osgi.libs.killbill.KillbillActivatorBase;
import org.killbill.killbill.osgi.libs.killbill.OSGIKillbillEventDispatcher.OSGIKillbillEventHandler;
import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;

import java.util.Hashtable;

public class FeedzaiActivator extends KillbillActivatorBase {

    // System property if/when needed
    private final boolean USE_SANDBOX = true;

    public static final String PLUGIN_NAME = "killbill-feedzai";

    private FeedzaiPaymentRoutingPluginApi paymentControlPluginApi;

    @Override
    public void start(final BundleContext context) throws Exception {
        super.start(context);

        final Boolean isPrintRequestsDetails = configProperties.getString("org.killbill.billing.plugin.feedzai.requests.info") != null ?
                Boolean.valueOf(configProperties.getString("org.killbill.billing.plugin.feedzai.requests.info")) : true;

        final Integer belowFraudScoreGood = Integer.valueOf(configProperties.getString("org.killbill.billing.plugin.feedzai.fraud.rate.good"));
        final Integer aboveFraudScoreBad = Integer.valueOf(configProperties.getString("org.killbill.billing.plugin.feedzai.fraud.rate.bad"));
        final Boolean useFeedzaiMock = Boolean.valueOf(configProperties.getString("org.killbill.billing.plugin.feedzai.feedzai.mock"));
        final String apiKey = configProperties.getString("org.killbill.billing.plugin.feedzai.api.key");

        final FeedzaiClient feedzaiClient = useFeedzaiMock ? new MockFeedzaiClient() : new DefaultFeedzaiClient(USE_SANDBOX, apiKey, isPrintRequestsDetails);

        logService.log(LogService.LOG_INFO, String.format("Starting Money2020Activator plugin with belowFraudScoreGood=[%d], aboveFraudScoreBad=[%d]", belowFraudScoreGood, aboveFraudScoreBad));

        // Register the PaymentControlPluginApi
        paymentControlPluginApi = new FeedzaiPaymentRoutingPluginApi(feedzaiClient, logService, aboveFraudScoreBad);
        registerPaymentControlPluginApi(context, paymentControlPluginApi);
    }

    @Override
    public OSGIKillbillEventHandler getOSGIKillbillEventHandler() {
        return null;
    }

    private void registerPaymentControlPluginApi(final BundleContext context, final PaymentRoutingPluginApi api) {
        final Hashtable<String, String> props = new Hashtable<String, String>();
        props.put(OSGIPluginProperties.PLUGIN_NAME_PROP, PLUGIN_NAME);
        registrar.registerService(context, PaymentRoutingPluginApi.class, api, props);
    }
}
