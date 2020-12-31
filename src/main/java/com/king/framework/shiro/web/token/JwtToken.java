package com.king.framework.shiro.web.token;

import org.apache.shiro.authc.AuthenticationToken;

/**
 * @author 金振林
 * @version v1.0
 * @date 2020/12/22 9:21
 */
public class JwtToken implements AuthenticationToken {

    private String token;

    public JwtToken(String token) {
        this.token = token;
    }

    @Override
    public Object getPrincipal() {
        return this.token;
    }

    @Override
    public Object getCredentials() {
        return this.token;
    }
}
