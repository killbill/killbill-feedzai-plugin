package org.killbill.billing.plugin.feedzai.mock;

import org.killbill.clients.feedzai.PaymentRequest;

public class MockPaymentRequest extends PaymentRequest {

    private final Integer scoreInput;

    public MockPaymentRequest(String userId, Integer amount, String ip, String id, Integer scoreInput) {
        super(userId, amount, ip, id);
        this.scoreInput = scoreInput;
    }

    public Integer getScoreInput() {
        return scoreInput;
    }
}
