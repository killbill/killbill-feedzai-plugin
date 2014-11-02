package org.killbill.billing.plugin.feedzai.mock;

import org.killbill.clients.feedzai.FeedzaiClient;
import org.killbill.clients.feedzai.FeedzaiClientException;
import org.killbill.clients.feedzai.HistoricalPayments;
import org.killbill.clients.feedzai.LabelRequest;
import org.killbill.clients.feedzai.PaymentRequest;
import org.killbill.clients.feedzai.PaymentResponse;
import org.killbill.clients.feedzai.PreviousPaymentResponse;
import org.killbill.clients.feedzai.StatusRequest;
import org.killbill.clients.feedzai.UserActionRequest;
import org.killbill.clients.feedzai.UserStatusResponse;

public class MockFeedzaiClient implements FeedzaiClient {

    @Override
    public PaymentResponse scorePayment(PaymentRequest paymentRequest) throws FeedzaiClientException {
        MockPaymentRequest mockRequest = (MockPaymentRequest) paymentRequest;
        return new PaymentResponse(paymentRequest.getId(), mockRequest.getScoreInput(), "scored", false, 0.1, null);
    }

    @Override
    public PreviousPaymentResponse getPreviousPayment(String s) throws FeedzaiClientException {
        return null;
    }

    @Override
    public void labelPreviousPayment(String s, LabelRequest labelRequest) throws FeedzaiClientException {

    }

    @Override
    public void sendHistoricalPayments(HistoricalPayments historicalPayments) throws FeedzaiClientException {

    }

    @Override
    public UserStatusResponse getUserStatus(String s) throws FeedzaiClientException {
        return null;
    }

    @Override
    public void blockUser(String s, StatusRequest statusRequest) throws FeedzaiClientException {

    }

    @Override
    public void sendUserAction(UserActionRequest userActionRequest) throws FeedzaiClientException {

    }
}
