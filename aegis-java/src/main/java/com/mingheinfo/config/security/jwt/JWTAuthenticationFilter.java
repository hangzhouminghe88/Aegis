package com.mingheinfo.config.security.jwt;


import cn.hutool.core.util.StrUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mingheinfo.common.constant.SecurityConstant;
import com.mingheinfo.common.utils.ResponseUtils;
import com.mingheinfo.common.utils.SecurityUtil;
import com.mingheinfo.common.vo.TokenUser;
import com.mingheinfo.config.properties.TokenProperties;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
public class JWTAuthenticationFilter extends BasicAuthenticationFilter   {

    private TokenProperties tokenProperties;

    private StringRedisTemplate redisTemplate;

    private SecurityUtil securityUtil;

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager,
                                   TokenProperties tokenProperties,
                                   StringRedisTemplate redisTemplate, SecurityUtil securityUtil) {
        super(authenticationManager);
        this.tokenProperties = tokenProperties;
        this.redisTemplate = redisTemplate;
        this.securityUtil = securityUtil;
    }

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager, AuthenticationEntryPoint authenticationEntryPoint) {
        super(authenticationManager, authenticationEntryPoint);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {

        String header = request.getHeader(SecurityConstant.HEADER);
        if(StrUtil.isBlank(header)){
            header = request.getParameter(SecurityConstant.HEADER);
        }
        Boolean notValid = StrUtil.isBlank(header) || (!tokenProperties.getRedis() && !header.startsWith(SecurityConstant.TOKEN_SPLIT));
        if (notValid) {
            chain.doFilter(request, response);
            return;
        }
        try {
            UsernamePasswordAuthenticationToken authentication = getAuthentication(header, response);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }catch (Exception e){
            e.toString();
        }

        chain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(String header, HttpServletResponse response) {

        // ?????????
        String username = null;
        // ??????
        List<GrantedAuthority> authorities = new ArrayList<>();

        if(tokenProperties.getRedis()){
            // redis
            String v = redisTemplate.opsForValue().get(SecurityConstant.TOKEN_PRE + header);
            if(StrUtil.isBlank(v)){
                ResponseUtils.out(response, ResponseUtils.resultMap(false,401,"?????????????????????????????????"));
                return null;
            }
            TokenUser user = new Gson().fromJson(v, TokenUser.class);
            username = user.getUsername();
            if(tokenProperties.getStorePerms()){
                // ???????????????
                for(String ga : user.getPermissions()){
                    authorities.add(new SimpleGrantedAuthority(ga));
                }
            }else{
                // ????????? ??????????????????
                authorities = securityUtil.getCurrUserPerms(username);
            }
            if(!user.getSaveLogin()){
                // ????????????????????????????????????????????????
                redisTemplate.opsForValue().set(SecurityConstant.USER_TOKEN + username, header, tokenProperties.getTokenExpireTime(), TimeUnit.MINUTES);
                redisTemplate.opsForValue().set(SecurityConstant.TOKEN_PRE + header, v, tokenProperties.getTokenExpireTime(), TimeUnit.MINUTES);
            }
        }else{
            // JWT
            try {
                // ??????token
                Claims claims = Jwts.parser()
                        .setSigningKey(SecurityConstant.JWT_SIGN_KEY)
                        .parseClaimsJws(header.replace(SecurityConstant.TOKEN_SPLIT, ""))
                        .getBody();

                // ???????????????
                username = claims.getSubject();
                // ????????????
                if(tokenProperties.getStorePerms()) {
                    // ???????????????
                    String authority = claims.get(SecurityConstant.AUTHORITIES).toString();
                    if(StrUtil.isNotBlank(authority)){
                        List<String> list = new Gson().fromJson(authority, new TypeToken<List<String>>(){}.getType());
                        for(String ga : list){
                            authorities.add(new SimpleGrantedAuthority(ga));
                        }
                    }
                }else{
                    // ????????? ??????????????????
                    authorities = securityUtil.getCurrUserPerms(username);
                }
            } catch (ExpiredJwtException e) {
                ResponseUtils.out(response, ResponseUtils.resultMap(false,401,"?????????????????????????????????"));
            } catch (Exception e){
                log.error(e.toString());
                ResponseUtils.out(response, ResponseUtils.resultMap(false,500,"??????token??????"));
            }
        }

        if(StrUtil.isNotBlank(username)) {
            //Exrick???????????? ??????password?????????null
            User principal = new User(username, "", authorities);
            return new UsernamePasswordAuthenticationToken(principal, null, authorities);
        }
        return null;
    }
}

