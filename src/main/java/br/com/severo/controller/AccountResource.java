package br.com.severo.controller;

import br.com.severo.model.Account;
import br.com.severo.model.ReturnStatusClient;
import br.com.severo.service.AccountService;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/account")
public class AccountResource {

    private final AccountService accountService = new AccountService();

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ReturnStatusClient post(Account account) {
        return accountService.processAccount(account);
    }
}
