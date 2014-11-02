package org.killbill.billing.plugin.feedzai;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.killbill.billing.util.callcontext.CallContext;
import org.killbill.billing.util.callcontext.CallOrigin;
import org.killbill.billing.util.callcontext.UserType;

import java.util.UUID;

public class FeedzaiCallContext implements CallContext {

    private final UUID tenantId;

    private final DateTime now;

    public FeedzaiCallContext(final UUID tenantId) {
        this.tenantId = tenantId;
        this.now = new DateTime(DateTimeZone.UTC);
    }

    @Override
    public UUID getUserToken() {
        // This is missing should come from context
        return UUID.randomUUID();
    }

    @Override
    public String getUserName() {
        return FeedzaiActivator.PLUGIN_NAME;
    }

    @Override
    public CallOrigin getCallOrigin() {
        return CallOrigin.EXTERNAL;
    }

    @Override
    public UserType getUserType() {
        return UserType.SYSTEM;
    }

    @Override
    public String getReasonCode() {
        return null;
    }

    @Override
    public String getComments() {
        return null;
    }

    @Override
    public DateTime getCreatedDate() {
        return now;
    }

    @Override
    public DateTime getUpdatedDate() {
        return now;
    }

    @Override
    public UUID getTenantId() {
        return tenantId;
    }
}
