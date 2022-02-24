package br.com.severo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResponseAccount {
    private Integer status;
    private String request_id;
    private String accept_at;
    private String error_message;
}
