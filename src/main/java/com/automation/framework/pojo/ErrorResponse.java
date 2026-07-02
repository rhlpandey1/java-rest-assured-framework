package com.automation.framework.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Standard error envelope returned by services. */
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ErrorResponse {
    private int    status;
    private String error;
    private String message;
    private String path;
    private String timestamp;
}
