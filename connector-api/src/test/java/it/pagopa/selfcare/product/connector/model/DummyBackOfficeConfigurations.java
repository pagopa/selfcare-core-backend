package it.pagopa.selfcare.product.connector.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DummyBackOfficeConfigurations implements BackOfficeConfigurations {

    private String url;
    private String identityTokenAudience;


    public DummyBackOfficeConfigurations(BackOfficeConfigurations backOfficeConfigurations) {
        url = backOfficeConfigurations.getUrl();
        identityTokenAudience = backOfficeConfigurations.getIdentityTokenAudience();
    }

}