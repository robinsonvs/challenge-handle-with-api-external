package br.com.codetest;

import br.com.severo.Main;
import br.com.severo.enums.EnumStatusTypes;
import br.com.severo.model.Account;
import br.com.severo.model.Country;
import br.com.severo.model.ReturnStatusClient;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import org.glassfish.grizzly.http.server.HttpServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class AccountResourceTest {
    private static final String PATH_ACCOUNT = "/account";

    private HttpServer server;
    private WebTarget target;

    @Before
    public void setUp() {
        server = Main.startServer();
        Client client = ClientBuilder.newClient();
        target = client.target(Main.BASE_URI);
    }

    @After
    public void tearDown() {
        server.shutdownNow();
    }


    @Test
    public void testPostIt() {
        Account account = buidAccountRequest();

        ReturnStatusClient response = target.path(PATH_ACCOUNT)
                .request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.json(account), ReturnStatusClient.class);

        ReturnStatusClient returnStatusClientReadyOKExpected = ReturnStatusClient.builder().status(EnumStatusTypes.READY.getDescription()).build();
        assertEquals(returnStatusClientReadyOKExpected, response);
    }

    @Test
    public void testPostFailIt() {
        Account account = buidAccountRequest();
        account.getCountry().setCode("UK");

        ReturnStatusClient response = target.path(PATH_ACCOUNT)
                .request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.json(account), ReturnStatusClient.class);

        ReturnStatusClient returnStatusProcessingOKExpected = ReturnStatusClient.builder().status(EnumStatusTypes.PROCESSING.getDescription()).build();
        assertNotEquals(returnStatusProcessingOKExpected, response);
    }

    private Account buidAccountRequest() {
        Account account = Account.builder()
                .name("John")
                .mail("john.lennon@jmail.com")
                .date_of_birth(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME))
                .country(Country.builder()
                        .code("UK")
                        .build())
                .build();
        return account;
    }
}
