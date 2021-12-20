package com.libumu.mubook.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class HttpRequestTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void indexRequest() {
        assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/",
                String.class)).contains("This is the Index");
    }

    @Test
    public void mainPageRequest() {
        //It should be redirected to /login
        assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/mainPage",
                String.class)).doesNotContain("Hello");

        //The redirected html (login)
        assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/mainPage",
                String.class)).contains("Log in with a username");
    }
}