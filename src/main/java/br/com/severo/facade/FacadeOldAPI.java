package br.com.severo.facade;

import br.com.severo.enums.EnumStatusTypes;
import br.com.severo.model.Account;
import br.com.severo.model.ResponseAccount;
import br.com.severo.model.ResponseStatus;
import br.com.severo.model.Status;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class FacadeOldAPI {

    private static final String CODE_COUNTRY_OK = "BRA";
    private static final List<EnumStatusTypes> VALUES_TYPES = Collections.unmodifiableList(Arrays.asList(EnumStatusTypes.values()));
    private static final int SIZE_ENUM_TYPES = VALUES_TYPES.size();
    private static final Random RANDOM = new Random();

    public Optional<ResponseAccount> requestAccount(Account account) {
        String generatedUUID = generateUUID();

        if (isProcessAccountReturnedOK(account)) {
            return Optional.of(ResponseAccount.builder()
                    .status(0)
                    .request_id(generatedUUID)
                    .accept_at(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME))
                    .build());
        }

        return Optional.of(ResponseAccount.builder()
                .status(-1)
                .request_id(generatedUUID)
                .accept_at(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME))
                .build());
    }


    public Optional<ResponseStatus> requestStatusAccount(String idRequest) {
        EnumStatusTypes request = VALUES_TYPES.get(RANDOM.nextInt(SIZE_ENUM_TYPES));

        ResponseStatus responseStatus = ResponseStatus.builder()
                .request_id(idRequest)
                .status(Status.builder()
                        .code(request.getCode())
                        .description(request.getDescription())
                        //.description(EnumStatusTypes.PROCESSING.getDescription())
                        .build())
                .build();

        return Optional.of(responseStatus);
    }

    public static String generateUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    private boolean isProcessAccountReturnedOK(Account account) {
        return Objects.nonNull(account) && Objects.nonNull(account.getCountry().getCode()) && account.getCountry().getCode().equalsIgnoreCase(CODE_COUNTRY_OK);
    }
}
