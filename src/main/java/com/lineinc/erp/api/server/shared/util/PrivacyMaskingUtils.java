package com.lineinc.erp.api.server.shared.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.lineinc.erp.api.server.domain.user.entity.User;
import com.lineinc.erp.api.server.domain.user.service.v1.UserService;
import com.lineinc.erp.api.server.infrastructure.config.security.CustomUserDetails;

@Component
public class PrivacyMaskingUtils {
    private static UserService userService;

    public PrivacyMaskingUtils(final UserService userService) {
        PrivacyMaskingUtils.userService = userService;
    }

    /**
     * 주민등록번호 마스킹 처리
     * 현재 로그인한 사용자의 권한을 자동으로 체크하여 마스킹 여부 결정
     * 
     * @param residentNumber 주민등록번호 (예: 860101-1234567)
     * @return 마스킹된 주민등록번호 (예: 860101-1******) 또는 원본
     */
    public static String maskResidentNumber(final String residentNumber) {
        final boolean hasUnmaskPermission = checkUnmaskPermission();
        return maskResidentNumber(residentNumber, hasUnmaskPermission);
    }

    /**
     * 주민등록번호 마스킹 처리 (권한 직접 지정)
     * 
     * @param residentNumber      주민등록번호 (예: 860101-1234567)
     * @param hasUnmaskPermission 마스킹 해제 권한 여부
     * @return 마스킹된 주민등록번호 또는 원본
     */
    public static String maskResidentNumber(final String residentNumber, final boolean hasUnmaskPermission) {
        if (residentNumber == null || residentNumber.isEmpty()) {
            return null;
        }

        // 마스킹 해제 권한이 있으면 원본 반환
        if (hasUnmaskPermission) {
            return residentNumber;
        }

        // 하이픈 제거
        final String cleanNumber = residentNumber.replace("-", "");

        if (cleanNumber.length() != 13) {
            return residentNumber; // 유효하지 않은 형식이면 원본 반환
        }

        // 앞 6자리 + 하이픈 + 뒤 7자리 중 첫 번째만 + 나머지 6자리는 *
        final String front = cleanNumber.substring(0, 6);
        final String back = cleanNumber.substring(6);
        final String firstDigit = back.substring(0, 1);
        final String masked = "*".repeat(6);

        return front + "-" + firstDigit + masked;
    }

    /**
     * 현재 로그인한 사용자가 마스킹 해제 권한이 있는지 체크
     * 
     * @return 마스킹 해제 권한 여부
     */
    private static boolean checkUnmaskPermission() {
        try {
            final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return false;
            }

            final Object principal = authentication.getPrincipal();
            if (!(principal instanceof CustomUserDetails)) {
                return false;
            }

            final CustomUserDetails userDetails = (CustomUserDetails) principal;
            final Long userId = userDetails.getUserId();

            // DB에서 사용자 조회하여 Role의 hasUnmaskPermission 체크
            final User user = userService.getUserByIdOrThrow(userId);
            if (user == null) {
                return false;
            }

            final boolean hasPermission = user.getUserRoles().stream()
                    .filter(userRole -> !userRole.isDeleted())
                    .anyMatch(userRole -> Boolean.TRUE.equals(userRole.getRole().getHasUnmaskPermission()));

            return hasPermission;
        } catch (final Exception e) {
            System.out.println("checkUnmaskPermission error: " + e.getMessage());
            return false;
        }
    }

}
