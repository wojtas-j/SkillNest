package com.wojtasj.SkillNest.misc;

public final class ApiPaths {
    private ApiPaths() {}

    public static final class Users {
        public static final String BASE = "/api/users";
        public static final String REGISTER = "/register";
        public static final String LOGIN = "/login";
        public static final String UPDATE_EMAIL = "/{id}/email";
        public static final String UPDATE_PASSWORD = "/{id}/password";
        public static final String DELETE = "deleteUser";
        public static final String REFRESH_TOKEN = "/refresh_token";
        public static final String LOGOUT = "/logout";

        private Users() {}
    }
}
