package com.smartscity.ratelimit.wizard.annotation;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.SecurityContext;
import java.util.Optional;

public interface KeyProvider {

    Optional<String> create(HttpServletRequest request, ResourceInfo resourceInfo, SecurityContext securityContext);

}
