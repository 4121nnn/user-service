package io.nuri.user_service.security.oauth2;

import io.nuri.user_service.security.jwt.JwtService;
import io.nuri.user_service.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
public class Oauth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        DefaultOAuth2User oauth2User = (DefaultOAuth2User)  authentication.getPrincipal();

        if(oauth2User == null) {
            log.error("OAuth2 user is null");
        }

        String name = (String) oauth2User.getAttributes().get("name");
        Map<String, Object> attributes = oauth2User.getAttributes();
        String email = null;
        if(attributes.containsKey("sub")){
            // google
            email = (String) attributes.get("email");
        }else if(attributes.containsKey("login")){
            // github
            email =  "[GITHUB]-" + (String) attributes.get("login");
        }

        String token = JwtService.generateOneTimeToken(name, email);
        String redirectUrl = "http://localhost:5173/oauth2-success?token=" + URLEncoder.encode(token, StandardCharsets.UTF_8);

        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}
