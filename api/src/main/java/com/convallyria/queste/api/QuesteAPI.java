package com.convallyria.queste.api;

public final class QuesteAPI {

    private QuesteAPI() {}

    private static IQuesteAPI api;

    public static IQuesteAPI getAPI() {
        return api;
    }

    public static void setAPI(IQuesteAPI api) {
        if (QuesteAPI.api != null && api != null) throw new IllegalStateException("API already set");
        QuesteAPI.api = api;
    }
}
