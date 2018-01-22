package com.smartscity.ratelimit.wizard.annotation;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.SecurityContext;
import java.security.Principal;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.util.Objects.isNull;

/**
 * Created by apple on 2018/1/22.
 */
public enum Key implements KeyProvider {

    /**
     * The 'any' key will be the concatenation of the resource name and the first of:
     *
     * <p>
     * <ul>
     * <li>authenticated principle (Dropwizard auth)</li>
     * <li>X-Forwarded-For Header IP address</li>
     * <li>servlet remote address IP</li>
     * </ul>
     */
    ANY {
        @Override
        public Optional<String> create(final HttpServletRequest request,
                                       final ResourceInfo resource,
                                       final SecurityContext securityContext) {
            return requestKey(request, securityContext)
                    .map(requestKey -> combinedKey(resource, requestKey));
        }

        private Optional<String> requestKey(final HttpServletRequest request, final SecurityContext securityContext) {
            return selectOptional(
                    () -> userRequestKey(securityContext),
                    () -> xForwardedForRequestKey(request),
                    () -> ipRequestKey(request));
        }
    },

    /**
     * The 'authenticated' key will be the concatenation of the resource name and authenticated principle (Dropwizard auth).
     */
    AUTHENTICATED {
        @Override
        public Optional<String> create(final HttpServletRequest request,
                                       final ResourceInfo resource,
                                       final SecurityContext securityContext) {
            return requestKey(securityContext)
                    .map(requestKey -> combinedKey(resource, requestKey));
        }

        private Optional<String> requestKey(final SecurityContext securityContext) {
            return userRequestKey(securityContext);
        }
    },

    /**
     * The 'ip' key will be the concatenation of the resource name and IP (X-Forwarded-For Header or servlet remote address).
     */
    IP {
        @Override
        public Optional<String> create(final HttpServletRequest request,
                                       final ResourceInfo resource,
                                       final SecurityContext securityContext) {
            return requestKey(request)
                    .map(requestKey -> combinedKey(resource, requestKey));
        }

        private Optional<String> requestKey(final HttpServletRequest request) {
            return selectOptional(
                    () -> xForwardedForRequestKey(request),
                    () -> ipRequestKey(request));
        }
    },

    /**
     * The 'resource' key will be the of the resource name.
     */
    RESOURCE {
        @Override
        public Optional<String> create(final HttpServletRequest request,
                                       final ResourceInfo resource,
                                       final SecurityContext securityContext) {
            return Optional.of("rlj:" + resourceKey(resource));
        }
    };

    private static final String X_FORWARDED_FOR = "X-Forwarded-For";

    private static String combinedKey(ResourceInfo resource, String requestKey) {
        return "rlj:" + resourceKey(resource) + ":" + requestKey;
    }

    private static String resourceKey(ResourceInfo resource) {
        return resource.getResourceClass().getTypeName()
                + "#" + resource.getResourceMethod().getName();
    }

    static Optional<String> userRequestKey(SecurityContext securityContext) {
        Principal userPrincipal = securityContext.getUserPrincipal();
        if (isNull(userPrincipal)) {
            return Optional.empty();
        }
        return Optional.of("usr#" + userPrincipal.getName());
    }

    static Optional<String> xForwardedForRequestKey(HttpServletRequest request) {

        String header = request.getHeader(X_FORWARDED_FOR);
        if (isNull(header)) {
            return Optional.empty();
        }

        Optional<String> originatingClientIp = Stream.of(header.split(",")).findFirst();
        return originatingClientIp.map(ip -> "xfwd4#" + ip);
    }

    static Optional<String> ipRequestKey(HttpServletRequest request) {
        String remoteAddress = request.getRemoteAddr();
        if (isNull(remoteAddress)) {
            return Optional.empty();
        }
        return Optional.of("ip#" + remoteAddress);
    }

    @SafeVarargs
    static <T> Optional<T> selectOptional(Supplier<Optional<T>>... optionals) {
        return Arrays.stream(optionals)
                .reduce((s1, s2) -> () -> s1.get().map(Optional::of).orElseGet(s2))
                .orElse(Optional::empty).get();
    }
}
