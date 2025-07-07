package br.com.meli.times_futebol.enums;

public enum EstadoBr {
    AC, AL, AP, AM, BA, CE, DF,
    ES, GO, MA, MT, MS, MG, PA,
    PB, PR, PE, PI, RJ, RN, RS,
    RO, RR, SC, SP, SE, TO;

    // validacao generica do estado.. vale pra tudo

    public static boolean validaEstadoBr(String estado) {
        if (estado == null || estado.trim().isEmpty()) return false;
        for (EstadoBr e : values()) {
            if (e.name().equalsIgnoreCase(estado.trim())) {
                return true;
            }
        }
        return false;
    }
}
