package com.example.usergenerator.vo.user;

public class UserLoginVO {

    private String token;

    private String role;

    private UserVO user;

    public UserLoginVO() {
    }

    public UserLoginVO(String token, String role, UserVO user) {
        this.token = token;
        this.role = role;
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public UserVO getUser() {
        return user;
    }

    public void setUser(UserVO user) {
        this.user = user;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String token;
        private String role;
        private UserVO user;

        public Builder token(String token) {
            this.token = token;
            return this;
        }

        public Builder role(String role) {
            this.role = role;
            return this;
        }

        public Builder user(UserVO user) {
            this.user = user;
            return this;
        }

        public UserLoginVO build() {
            return new UserLoginVO(token, role, user);
        }
    }
}
