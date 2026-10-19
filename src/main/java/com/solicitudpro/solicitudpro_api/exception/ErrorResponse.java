package com.solicitudpro.solicitudpro_api.exception;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        name = "ErrorResponse",
        description = "Información de un error ocurrido"
)
public class ErrorResponse {

    @Schema(
        description = "Código de estado HTTP",
        example = "400"
    )
    private int status;

    @Schema(
        description = "Mensaje de error",
        example = "Error en la solicitud"
    )
    private String error;

    @Schema(
        description = "Mensaje detallado del error",
        example = "El campo 'titulo' es obligatorio"
    )
    private String message;

    public ErrorResponse() {
    }

    public ErrorResponse(int status, String error, String message) {
        this.status = status;
        this.error = error;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}