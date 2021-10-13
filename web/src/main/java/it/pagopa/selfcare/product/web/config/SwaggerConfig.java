package it.pagopa.selfcare.product.web.config;

import com.fasterxml.classmate.TypeResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.Ordered;
import org.springframework.data.domain.Pageable;
import springfox.documentation.builders.AlternateTypeBuilder;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger.web.SecurityConfigurationBuilder;

import java.lang.reflect.Type;
import java.time.LocalTime;

import static springfox.documentation.schema.AlternateTypeRules.newRule;

/**
 * The Class SwaggerConfig.
 */
@Configuration
public class SwaggerConfig {

    @Configuration
    @Profile("swaggerIT")
    @PropertySource("classpath:/swagger/swagger_it_IT.properties")
    public static class itConfig {
    }

    /**
     * The title.
     */
    @Value("${swagger.title:${spring.application.name}}")
    private String title;

    /**
     * The description.
     */
    @Value("${swagger.description:Api and Models}")
    private String description;

    /**
     * The version.
     */
    @Value("${swagger.version:${spring.application.version}}")
    private String version;

    /**
     * Swagger spring.
     *
     * @param typeResolver the type resolver
     * @return the docket
     */
    @Bean
    public Docket swaggerSpringPlugin(@Autowired TypeResolver typeResolver) {
        return (new Docket(DocumentationType.OAS_30)).select().apis(RequestHandlerSelectors.any())
                .apis(RequestHandlerSelectors.basePackage("org.springframework.boot").negate())
                .apis(RequestHandlerSelectors.basePackage("org.springframework.hateoas").negate()).build()
                .alternateTypeRules(
                        newRule(typeResolver.resolve(Pageable.class), pageableMixin(), Ordered.HIGHEST_PRECEDENCE))
                .directModelSubstitute(LocalTime.class, String.class)
                .apiInfo(this.metadata());
    }

    /**
     * Metadata.
     *
     * @return the api info
     */
    private ApiInfo metadata() {
        return (new ApiInfoBuilder()).title(this.title).description(this.description).version(this.version).build();
    }

    private Type pageableMixin() {
        return new AlternateTypeBuilder()
                .fullyQualifiedClassName(String.format("%s.generated.%s", Pageable.class.getPackage().getName(),
                        Pageable.class.getSimpleName()))
                .property(p -> p.name("page").type(Integer.class).canRead(true).canWrite(true))
                .property(p -> p.name("size").type(Integer.class).canRead(true).canWrite(true))
                .property(p -> p.name("sort").type(String.class).canRead(true).canWrite(true))
                .build();
    }


    @Bean
    public SecurityConfiguration security() {
        return SecurityConfigurationBuilder.builder().scopeSeparator(",")
                .additionalQueryStringParams(null)
                .useBasicAuthenticationWithAccessCodeGrant(false).build();
    }

}
