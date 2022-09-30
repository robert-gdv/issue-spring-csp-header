package dummy;

import org.junit.jupiter.api.Test;
import org.mockserver.client.MockServerClient;
import org.mockserver.model.MediaType;
import org.mockserver.springtest.MockServerTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.gateway.filter.factory.SecureHeadersGatewayFilterFactory;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@MockServerTest({"webserver.url=http://localhost:${mockServerPort}"})
class CspTest {

    @SuppressWarnings("unused") // Injected by @MockServerTest
    protected MockServerClient webserverMock;

    @Autowired
    protected WebTestClient client;

    @Test
    void securedRoute() {
        webserverMock.when(request().withMethod("GET"))
                     .respond(response("<html/>").withContentType(MediaType.HTML_UTF_8));
        client.method(HttpMethod.GET).uri("/secure/")
              .exchange()
              .expectStatus().isOk()
              .expectHeader().doesNotExist("content-security-policy");
    }

    @Test
    void insecureRoute() {
        webserverMock.when(request().withMethod("GET"))
                .respond(response("<html/>").withContentType(MediaType.HTML_UTF_8));
        client.method(HttpMethod.GET).uri("/insecure/")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().doesNotExist("content-security-policy");
    }
}
