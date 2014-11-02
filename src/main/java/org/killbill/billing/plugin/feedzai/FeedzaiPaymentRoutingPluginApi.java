package org.killbill.billing.plugin.feedzai;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

import org.joda.time.DateTime;
import org.killbill.billing.catalog.api.Currency;
import org.killbill.billing.payment.api.PluginProperty;
import org.killbill.billing.plugin.feedzai.mock.MockFeedzaiClient;
import org.killbill.billing.plugin.feedzai.mock.MockPaymentRequest;
import org.killbill.billing.routing.plugin.api.OnFailurePaymentRoutingResult;
import org.killbill.billing.routing.plugin.api.OnSuccessPaymentRoutingResult;
import org.killbill.billing.routing.plugin.api.PaymentRoutingApiException;
import org.killbill.billing.routing.plugin.api.PaymentRoutingContext;
import org.killbill.billing.routing.plugin.api.PaymentRoutingPluginApi;
import org.killbill.billing.routing.plugin.api.PriorPaymentRoutingResult;
import org.killbill.clients.feedzai.FeedzaiClient;
import org.killbill.clients.feedzai.FeedzaiClientException;
import org.killbill.clients.feedzai.PaymentRequest;
import org.killbill.clients.feedzai.PaymentResponse;
import org.osgi.service.log.LogService;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

public class FeedzaiPaymentRoutingPluginApi implements PaymentRoutingPluginApi {

    private final FeedzaiClient client;

    private final LogService logger;
    private final Integer aboveScoreBad;

    public FeedzaiPaymentRoutingPluginApi(final FeedzaiClient client, final LogService logger, final Integer aboveScoreBad) {
        this.client = client;
        this.logger = logger;
        this.aboveScoreBad = aboveScoreBad;
    }

    @Override
    public PriorPaymentRoutingResult priorCall(final PaymentRoutingContext context, final Iterable<PluginProperty> properties) throws PaymentRoutingApiException {
        logger.log(LogService.LOG_INFO, String.format("%s: priorCall %s", FeedzaiActivator.PLUGIN_NAME, propertiesFromContext(context)));

        final boolean shouldAbortPayment;
        final PaymentRequest request = createPaymentRequest(context, properties);
        try {
            final PaymentResponse response = client.scorePayment(request);
            shouldAbortPayment = shouldAbortPayment(response);

            logger.log(LogService.LOG_INFO, String.format("%s: onSuccessCall shouldAbortPayment = %s", FeedzaiActivator.PLUGIN_NAME, shouldAbortPayment));
        } catch (FeedzaiClientException e) {
            throw new PaymentRoutingApiException(e);
        }

        return new PriorPaymentRoutingResult() {
            @Override
            public boolean isAborted() {
                return shouldAbortPayment;
            }

            @Override
            public BigDecimal getAdjustedAmount() {
                return null;
            }

            @Override
            public Currency getAdjustedCurrency() {
                return null;
            }

            @Override
            public UUID getAdjustedPaymentMethodId() {
                return null;
            }
        };
    }

    @Override
    public OnSuccessPaymentRoutingResult onSuccessCall(PaymentRoutingContext context, final Iterable<PluginProperty> properties) throws PaymentRoutingApiException {
        logger.log(LogService.LOG_INFO, String.format("%s: onSuccessCall %s", FeedzaiActivator.PLUGIN_NAME, propertiesFromContext(context)));

        return new OnSuccessPaymentRoutingResult() {
        };
    }

    @Override
    public OnFailurePaymentRoutingResult onFailureCall(PaymentRoutingContext context, final Iterable<PluginProperty> properties) throws PaymentRoutingApiException {
        logger.log(LogService.LOG_INFO, String.format("%s: onFailureCall %s", FeedzaiActivator.PLUGIN_NAME, propertiesFromContext(context)));

        return new OnFailurePaymentRoutingResult() {
            @Override
            public DateTime getNextRetryDate() {
                return null;
            }
        };
    }

    private String propertiesFromContext(final PaymentRoutingContext context) {
        final StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (final PluginProperty pluginProperty : context.getPluginProperties()) {
            if (!first) {
                sb.append(", ");
            } else {
                first = false;
            }
            sb.append(pluginProperty.getKey()).append("=").append(pluginProperty.getValue());
        }
        return sb.toString();
    }

    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");

    private Integer toAmountCents(final BigDecimal input) {
        // TODO maybe wrong for other than US dollars
        return input.multiply(ONE_HUNDRED).intValue();
    }

    private boolean shouldAbortPayment(final PaymentResponse response) {
        return response.getScore() >= aboveScoreBad;
    }

    private PaymentRequest createPaymentRequest(final PaymentRoutingContext context, final Iterable<PluginProperty> properties) {
        final boolean isMockFeedzaiClient = (client instanceof MockFeedzaiClient);
        final PaymentRequest result;
        if (isMockFeedzaiClient) {
            final String feedzaiMockScoreProperty = findProperty("FEEDZAI_SCORE", properties);
            final Integer score = feedzaiMockScoreProperty == null ? null : Integer.valueOf(feedzaiMockScoreProperty);
            result = new MockPaymentRequest(context.getAccountId().toString(), toAmountCents(context.getAmount()), null /* How to get IP */, context.getAttemptPaymentId().toString(), score);
        } else {
            final String ip = findProperty("FEEDZAI_IP", properties);
            final String transactionType = findProperty("FEEDZAI_TRANSACTION_TYPE", properties);
            final String userEmail = findProperty("FEEDZAI_EMAIL", properties);
            final String userFullName = findProperty("FEEDZAI_NAME", properties);
            final String userCreatedAt = findProperty("FEEDZAI_USER_CREATED_AT", properties);
            final String userGender = findProperty("FEEDZAI_USER_GENDER", properties);

            final String userDateofbirth = findProperty("FEEDZAI_DATE_OF_BIRTH", properties);
            final String userPhone = findProperty("FEEDZAI_USER_PHONE", properties);
            final String userAddressLine1 = findProperty("FEEDZAI_USER_ADDRESS_LINE_1", properties);
            final String userAddressLine2 = findProperty("FEEDZAI_USER_ADDRESS_LINE_2", properties);
            final String userZip = findProperty("FEEDZAI_USER_ZIP", properties);
            final String userCity = findProperty("FEEDZAI_USER_CITY", properties);
            final String userRegion = findProperty("FEEDZAI_USER_REGION", properties);
            final String userCountry = findProperty("FEEDZAI_USER_COUNTRY", properties);
            final String cardHash = findProperty("FEEDZAI_CARD_HASH", properties);
            final String cardFullname = findProperty("FEEDZAI_CARD_FULL_NAME", properties);
            final String cardExp = findProperty("FEEDZAI_CARD_EXP", properties);
            final String cardCountry = findProperty("FEEDZAI_CARD_COUNTRY", properties);
            final String cardBin = findProperty("FEEDZAI_CARD_BIN", properties);
            final String cardLast4 = findProperty("FEEDZAI_CARD_LAST4", properties);

            final String billingPhone = findProperty("FEEDZAI_BILLING_PHONE", properties);
            final String billingAddressLine1 = findProperty("FEEDZAI_BILLING_ADDRESS_LINE_1", properties);
            final String billingAddressLine2 = findProperty("FEEDZAI_BILLING_ADDRESS_LINE_2", properties);
            final String billingZip = findProperty("FEEDZAI_BILLING_ZIP", properties);
            final String billingCity = findProperty("FEEDZAI_BILLING_CITY", properties);
            final String billingRegion = findProperty("FEEDZAI_BILLING_REGION", properties);
            final String billingCountry = findProperty("FEEDZAI_BILLING_COUNTRY", properties);

            final String shippingCountry = findProperty("FEEDZAI_SHIPPING_COUNTRY", properties);

            final String userDefinedUserAgent = findProperty("FEEDZAI_BILLING_USER_DEFINED_USER_AGENT", properties);
            final String userDefinedAccept = findProperty("FEEDZAI_BILLING_USER_DEFINED_ACCEPT", properties);

            final Map<String, String> userDefined = new HashMap<String, String>();
            userDefined.put("HTTP_USER_AGENT", userDefinedUserAgent);
            userDefined.put("HTTP_ACCEPT", userDefinedAccept);

            result = new PaymentRequest(context.getAccountId().toString(),
                                        toAmountCents(context.getAmount()),
                                        ip,
                                        context.getAttemptPaymentId().toString(),
                                        transactionType,
                                        userEmail,
                                        userFullName,
                                        userCreatedAt != null ? Long.valueOf(userCreatedAt) : null,
                                        userGender,
                                        userDateofbirth,
                                        userPhone,
                                        userAddressLine1,
                                        userAddressLine2,
                                        userZip,
                                        userCity,
                                        userRegion,
                                        userCountry,
                                        cardHash,
                                        cardFullname,
                                        cardExp,
                                        cardCountry,
                                        cardBin != null ? Integer.valueOf(cardBin) : null,
                                        cardLast4 != null ? Integer.valueOf(cardLast4) : null,
                                        billingPhone,
                                        billingAddressLine1,
                                        billingAddressLine2,
                                        billingZip,
                                        billingCity,
                                        billingRegion,
                                        billingCountry,
                                        shippingCountry,
                                        userDefined);
        }

        return result;
    }

    private String findProperty(final String property, final Iterable<PluginProperty> properties) {
        final PluginProperty pluginProperty = Iterables.tryFind(properties, new Predicate<PluginProperty>() {
            @Override
            public boolean apply(@Nullable PluginProperty input) {
                return property.equals(input.getKey());
            }
        }).orNull();
        return pluginProperty == null ? null : pluginProperty.getValue().toString();
    }
}
