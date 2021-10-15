package it.pagopa.selfcare.product.web.config;

import com.fasterxml.classmate.TypeResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * The Class SwaggerConfig.
 */
@Configuration
public class SwaggerConfig {

    public static final String AUTH_SCHEMA_NAME = "bearerAuth";

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
        return (new Docket(DocumentationType.OAS_30))
                .apiInfo(this.metadata())
                .select().apis(RequestHandlerSelectors.basePackage("it.pagopa.selfcare.product.web")).build()
                .tags(new Tag("product", "Product endpoints for CRUD operations"))
                .directModelSubstitute(LocalTime.class, String.class)
                .securityContexts(Collections.singletonList(securityContext()))
                .securitySchemes(Collections.singletonList(HttpAuthenticationScheme.JWT_BEARER_BUILDER.name(AUTH_SCHEMA_NAME).build()));
    }
//    @Bean
//    public Docket swaggerSpringPlugin(@Autowired TypeResolver typeResolver) {
//        return (new Docket(DocumentationType.OAS_30)).select()
//                .apis(RequestHandlerSelectors.basePackage("it.pagopa.selfcare.product.web")).build()
//                .directModelSubstitute(LocalTime.class, String.class)
//                .apiInfo(this.metadata())
//                .securityContexts(Collections.singletonList(securityContext()))
//                .securitySchemes(Collections.singletonList(HttpAuthenticationScheme.JWT_BEARER_BUILDER.name(AUTH_SCHEMA_NAME).build()));
//    }

    /**
     * Metadata.
     *
     * @return the api info
     */
    private ApiInfo metadata() {
        return (new ApiInfoBuilder()).title(this.title).description(this.description).version(this.version).build();
    }


    private SecurityContext securityContext() {
        return SecurityContext.builder().securityReferences(defaultAuth()).build();
    }


    private List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Arrays.asList(new SecurityReference(AUTH_SCHEMA_NAME, authorizationScopes));
    }


//    @Bean
//    public OpenAPI springShopOpenAPI() {
//        return new OpenAPI()
//                .info(new Info().title(title)
//                        .description(description)
//                        .version(version)
//                        .license(new License().name("PagoPA").url("https://www.pagopa.it/")));
//                .externalDocs(new ExternalDocumentation()
//                        .description("SpringShop Wiki Documentation")
//                        .url("https://springshop.wiki.github.org/docs"));
//    }

}
