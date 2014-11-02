package org.killbill.billing.plugin.feedzai;

import org.killbill.billing.util.callcontext.TenantContext;

import java.util.UUID;

final class FeedzaiTenantContext implements TenantContext {

    private final UUID tenantId;

    FeedzaiTenantContext(final UUID tenantId) {
        this.tenantId = tenantId;
    }

    @Override
    public UUID getTenantId() {
        return tenantId;
    }
}
