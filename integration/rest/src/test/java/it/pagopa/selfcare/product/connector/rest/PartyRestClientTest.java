package it.pagopa.selfcare.product.connector.rest;

import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;
import com.github.tomakehurst.wiremock.junit.WireMockClassRule;
import com.github.tomakehurst.wiremock.standalone.JsonFileMappingsSource;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import it.pagopa.selfcare.commons.connector.rest.BaseFeignRestClientTest;
import it.pagopa.selfcare.product.connector.rest.config.PartyRestClientTestConfig;
import it.pagopa.selfcare.product.connector.rest.model.RelationshipInfo;
import it.pagopa.selfcare.product.connector.rest.model.RelationshipsResponse;
import lombok.SneakyThrows;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.support.TestPropertySourceUtils;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

@TestPropertySource(
        locations = "classpath:config/party-rest-client.properties",
        properties = {
                "logging.level.it.pagopa.selfcare.product.connector.rest=DEBUG",
                "spring.application.name=selc-product-integration-rest"
        })
@ContextConfiguration(
        initializers = PartyRestClientTest.RandomPortInitializer.class,
        classes = {PartyRestClientTestConfig.class})
public class PartyRestClientTest extends BaseFeignRestClientTest {

    @ClassRule
    public static WireMockClassRule wireMockRule;

    static {
        String port = System.getenv("WIREMOCKPORT");
        WireMockConfiguration config = wireMockConfig()
                .port(port != null ? Integer.parseInt(port) : 0)
                .bindAddress("localhost")
//                .usingFilesUnderClasspath("stubs")
                .withRootDirectory("src/test/resources")
                .extensions(new ResponseTemplateTransformer(false));
        config.mappingSource(new JsonFileMappingsSource(config.filesRoot().child("stubs")));
        wireMockRule = new WireMockClassRule(config);
    }


    public static class RandomPortInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @SneakyThrows
        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            TestPropertySourceUtils.addInlinedPropertiesToEnvironment(applicationContext,
                    String.format("rest-client.party.base-url=http://%s:%d/pdnd-interop-uservice-party-process/0.0.1",
                            wireMockRule.getOptions().bindAddress(),
                            wireMockRule.port())
            );
        }
    }


    private enum TestCase {
        FULLY_VALUED,
        FULLY_NULL,
        EMPTY_RESULT
    }

    private static final Map<TestCase, String> testCase2instIdMap = new EnumMap<>(TestCase.class) {{
        put(TestCase.FULLY_VALUED, "institutionId1");
        put(TestCase.FULLY_NULL, "institutionId2");
        put(TestCase.EMPTY_RESULT, "institutionId3");
    }};

    @Autowired
    private PartyRestClient restClient;


    @Test
    public void getInstitutionRelationships_fullyValued() {
        List<StubMapping> stubMappings = wireMockRule.getStubMappings();
        // given
        String institutionId = UUID.randomUUID().toString();
        // when
        RelationshipsResponse response = restClient.getInstitutionRelationships(testCase2instIdMap.get(TestCase.FULLY_VALUED));
        // then
        Assert.assertNotNull(response);
        Assert.assertFalse(response.isEmpty());
        Assert.assertNotNull(response.get(0).getFrom());
        Assert.assertEquals(RelationshipInfo.RoleEnum.MANAGER, response.get(0).getRole());
        Assert.assertEquals(RelationshipInfo.StatusEnum.PENDING, response.get(0).getStatus());
        Assert.assertNotNull(response.get(0).getPlatformRole());
    }


    @Test
    public void getInstitutionRelationships_fullyNull() {
        // given
        String institutionId = UUID.randomUUID().toString();
        // when
        RelationshipsResponse response = restClient.getInstitutionRelationships(testCase2instIdMap.get(TestCase.FULLY_NULL));
        // then
        Assert.assertNotNull(response);
        Assert.assertFalse(response.isEmpty());
        Assert.assertNull(response.get(0).getFrom());
        Assert.assertNull(response.get(0).getRole());
        Assert.assertNull(response.get(0).getStatus());
        Assert.assertNull(response.get(0).getPlatformRole());
    }


    @Test
    public void getInstitutionRelationships_emptyResult() {
        // given
        String institutionId = UUID.randomUUID().toString();
        // when
        RelationshipsResponse response = restClient.getInstitutionRelationships(testCase2instIdMap.get(TestCase.EMPTY_RESULT));
        // then
        Assert.assertNotNull(response);
        Assert.assertTrue(response.isEmpty());
    }

}