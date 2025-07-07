package br.com.meli.times_futebol.Exception;

public class ApiError {

    final String mensagem;

    public ApiError(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getMensagem() {
        return mensagem;
    }
}
