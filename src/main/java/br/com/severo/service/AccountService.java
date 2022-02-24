package br.com.severo.service;

import br.com.severo.enums.EnumStatusTypes;
import br.com.severo.facade.FacadeOldAPI;
import br.com.severo.model.Account;
import br.com.severo.model.ResponseAccount;
import br.com.severo.model.ResponseStatus;
import br.com.severo.model.ReturnStatusClient;
import br.com.severo.retry.RetryAllowedException;
import br.com.severo.retry.RetryBreakException;
import br.com.severo.retry.RetryProcess;

import java.time.Duration;
import java.util.Optional;

public class AccountService {

    private static final Integer STATUS_OK = 0;
    private static final int MAX_ATTEMPTS = 4;
    private static final Duration DELAY = Duration.ofSeconds(1);

    private final FacadeOldAPI facadeOldAPI = new FacadeOldAPI();
    private final RetryProcess retryProcess = new RetryProcess();


    public ReturnStatusClient processAccount(Account account) {
        Optional<ResponseAccount> responseStatus = facadeOldAPI.requestAccount(account);

        if (isSuccessProcessing(responseStatus)) {
            return ReturnStatusClient.builder()
                    .status(EnumStatusTypes.READY.getDescription())
                    .build();
        } else {
            Optional<Object> returnStatusClient = validateRecursiveStatusProcessing(responseStatus.get().getRequest_id());
            return ReturnStatusClient.builder()
                    .status(returnStatusClient.get().toString())
                    .build();
        }
    }


    private ReturnStatusClient validateStatusAccountReturned(String id) {
        Optional<ResponseStatus> responseStatus = facadeOldAPI.requestStatusAccount(id);
        if (responseStatus.isPresent()) {
            if (EnumStatusTypes.isValidStatus(responseStatus.get().getStatus().getDescription())) {
                throw new RetryBreakException(responseStatus.get().getStatus().getDescription());
            }
            throw new RetryAllowedException(responseStatus.get().getStatus().getDescription());
        }
        return buildReturnStatusClient(responseStatus.get());
    }


    private ReturnStatusClient buildReturnStatusClient(ResponseStatus responseStatus) {
        return ReturnStatusClient.builder()
                .status(responseStatus.getStatus().getDescription())
                .build();
    }


    private Optional<Object> validateRecursiveStatusProcessing(String idRequest) {
        return retryProcess
                .maxAttempts(MAX_ATTEMPTS)
                .delay(DELAY)
                .allowedIn(RetryAllowedException.class)
                .abortIf(RetryBreakException.class)
                .process(() -> validateStatusAccountReturned(idRequest));
    }

    private boolean isSuccessProcessing(Optional<ResponseAccount> responseStatus) {
        return responseStatus.isPresent() && responseStatus.get().getStatus().equals(STATUS_OK);
    }

}
