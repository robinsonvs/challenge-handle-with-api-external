package br.com.severo.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EnumStatusTypes {
    READY(1, "Ready"),
    CANCELLED(2, "Cancelled"),
    PROCESSING(3, "Processing");

    private Integer code;
    private String description;

    public static boolean isValidStatus(String description) {
        return EnumStatusTypes.READY.getDescription().equals(description) || EnumStatusTypes.CANCELLED.getDescription().equals(description);
    }
}
