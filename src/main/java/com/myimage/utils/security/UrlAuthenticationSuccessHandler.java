/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.myimage.utils.security;
import java.io.IOException;
import java.util.Collection;
 
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
 
public class UrlAuthenticationSuccessHandler implements
        AuthenticationSuccessHandler {
 
         protected final Log logger = LogFactory.getLog(this.getClass());
 
        private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
         
        protected UrlAuthenticationSuccessHandler() {
            super();
        }
 
        // API
 
        @Override
        public void onAuthenticationSuccess(final HttpServletRequest request,
                                            final HttpServletResponse response,
                                            final Authentication authentication) throws IOException {
            
            handle(request, response, authentication);
            
            clearAuthenticationAttributes(request);
            
        }
 
        // IMPL
         
        protected void handle(final HttpServletRequest request, final HttpServletResponse response,
                              final Authentication authentication) throws IOException {
             
            final String targetUrl = determineTargetUrl(authentication);
 
            if (response.isCommitted()) {
                logger.debug("Response has already been committed. Unable to redirect to " + targetUrl);
                return;
            }
            redirectStrategy.sendRedirect(request, response, targetUrl);
        }
 
        protected String determineTargetUrl(final Authentication authentication) {
            boolean isCommon = false;
            boolean isAdmin = false;
            final Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            for (final GrantedAuthority grantedAuthority : authorities) {
                if (grantedAuthority.getAuthority().equals("ROLE_COMMON")) {
                    isCommon = true;
                    break;
                } else if (grantedAuthority.getAuthority().equals("ROLE_ADMIN")) {
                    isAdmin = true;
                    break;
                }
            }
            if (isCommon) {
                return "/user_common/principal.xhtml";
            } else if (isAdmin) {
                return "/admin/admin.xhtml";
            } else {
                throw new IllegalStateException();
            }
        }
 
        /**
        * Remove qualquer autenticação que estava antes armazenada na sessão.
        */
        protected final void clearAuthenticationAttributes(final HttpServletRequest request) {
            final HttpSession session = request.getSession(false);
 
            if (session == null) {
                return;
            }
            session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
        }        
 
        public void setRedirectStrategy(final RedirectStrategy redirectStrategy) {
            this.redirectStrategy = redirectStrategy;
        }
 
        protected RedirectStrategy getRedirectStrategy() {
            return redirectStrategy;
        }
}
