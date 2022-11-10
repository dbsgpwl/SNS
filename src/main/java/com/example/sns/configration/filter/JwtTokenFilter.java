package com.example.sns.configration.filter;

import com.example.sns.model.User;
import com.example.sns.service.UserService;
import com.example.sns.util.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {  // OncePerRequestFilter: 매회 요청시 마다 필터링하도록

    private final String key;
    private final UserService service;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 실제유저가 유효한지 필터링하는 과정 진행 (토큰필터 로직 구현)
        //get header
        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if(header == null || !header.startsWith("Bearer")){
            log.error("Error occurs while getting header. header is null or invalid");
            filterChain.doFilter(request, response);
            return;
        }

        try{
            final String token = header.split(" ")[1].trim();        // 토큰 값만 따로 빼오기, bearer과 토큰 사이 스페이스있어서 split으로 잘라주고, trim값 가져오기

            // 토큰이 유효한지(시간이 지나진 않았는지) 체크
            if(JwtTokenUtils.isExpired(token, key)){
                log.error("Key is expired");
                filterChain.doFilter(request, response);
                return;
            }

            // 토큰에 있는 userName 가져오기
            String userName = JwtTokenUtils.getUserName(token,key);

            // 가져온 userName이 유효한지 체크
            User user = service.loadUserByUserName(userName);

            // 모두 유효하면 request에 넣어서 controller로 넘김
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    user, null, user.getAuthorities()
            );

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authentication);

        }catch (RuntimeException e){
            log.error("Error occurs while validating. {}", e.toString());
            filterChain.doFilter(request, response);
            return;
        }

        filterChain.doFilter(request, response);

    }
}
