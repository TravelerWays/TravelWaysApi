package travel.ways.travelwaysapi._core.config;

import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import springfox.documentation.builders.ExampleBuilder;
import springfox.documentation.builders.OperationBuilder;
import springfox.documentation.builders.RequestParameterBuilder;
import springfox.documentation.builders.ResponseBuilder;
import springfox.documentation.schema.Example;
import springfox.documentation.schema.ScalarType;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.ApiListingScannerPlugin;
import springfox.documentation.spi.service.contexts.DocumentationContext;
import springfox.documentation.spring.web.readers.operation.CachingOperationNameGenerator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.util.Collections.singletonList;

public class SwaggerLoginConfig implements ApiListingScannerPlugin {
    private final CachingOperationNameGenerator operationNames;

    public SwaggerLoginConfig(CachingOperationNameGenerator operationNames) {
        this.operationNames = operationNames;
    }

    @Override
    public List<ApiDescription> apply(DocumentationContext context) {
        return new ArrayList<>(
                List.of(
                        new ApiDescription(
                                "account-controller",
                                "/api/auth/login",
                                "Login",
                                "Login",
                                getOperations(),
                                false)));
    }

    private Collection<Response> responses() {
        return singletonList(new ResponseBuilder()
                .code("200")
                .description("logged in")
                .representation(MediaType.ALL)
                .apply(r -> r.model(m -> m.scalarModel(ScalarType.STRING))
                        .build())
                .build());
    }

    @Override
    public boolean supports(DocumentationType delimiter) {
        return DocumentationType.SWAGGER_2.equals(delimiter);
    }

    private List<Operation> getOperations() {
        return List.of(
                new OperationBuilder(operationNames)
                        .authorizations(new ArrayList<>())
                        .codegenMethodNameStem("login")
                        .method(HttpMethod.POST)
                        .notes("This is a login method")
                        .requestParameters(getRequestParameters())
                        .responses(responses())
                        .build());
    }

    private List<RequestParameter> getRequestParameters() {
        return List.of(
                new RequestParameterBuilder()
                        .name("LoginForm")
                        .description("LoginForm")
                        .in(ParameterType.BODY)
                        .query(q -> q.model(m -> m.scalarModel(ScalarType.STRING)))
                        .example(getExample())
                        .build());
    }

    private Example getExample() {
        return new ExampleBuilder()
                .value("""
                        {
                            "username":"JD",
                            "password":"elo"
                        }
                        """)
                .build();
    }
}