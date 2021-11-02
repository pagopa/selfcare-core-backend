package it.pagopa.selfcare.product.web.security;

import it.pagopa.selfcare.commons.web.security.JwtAuthenticationDetails;
import it.pagopa.selfcare.product.connector.rest.PartyRestClient;
import it.pagopa.selfcare.product.connector.rest.model.RelationshipInfo;
import it.pagopa.selfcare.product.connector.rest.model.RelationshipsResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;


public class PartyAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

    private final PartyRestClient restClient;


    public PartyAuthenticationProvider(PartyRestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
    }


    @Override
    protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        // TODO: implement real logic after deciding how to manage roles (Admin, Reviewer)
        JwtAuthenticationDetails details = (JwtAuthenticationDetails) authentication.getDetails();
//        RelationshipsResponse institutionRelationships = restClient.getInstitutionRelationships(details.getInstitutionId());

        // start mock
        RelationshipInfo info = new RelationshipInfo();
        info.setFrom("from");
        info.setStatus(RelationshipInfo.StatusEnum.ACTIVE);
        info.setRole(RelationshipInfo.RoleEnum.MANAGER);
        info.setPlatformRole("ADMIN");
        RelationshipsResponse institutionRelationships = new RelationshipsResponse();
        institutionRelationships.add(info);
        // end mock

        User user = null;
        if (!institutionRelationships.isEmpty()) {
            RelationshipInfo relationshipInfo = institutionRelationships.get(0);
            SimpleGrantedAuthority grantedAuthority = new SimpleGrantedAuthority(relationshipInfo.getPlatformRole());
            user = new User(username, authentication.getCredentials().toString(), Collections.singletonList(grantedAuthority));
        }
        // TODO: map RelationshipsResponse to UserDetails
//        return new User("admin", "", Collections.singletonList(new SimpleGrantedAuthority(Role.ROLE_ADMIN.name())));
        return user;
    }

}