package com.flytecnologia.core.user;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface FlyUserService {
    String getMessageInvalidLogin();
    Optional<FlyUser> findByLoginOrEmail(String loginOrEmail);
    List<FlyUserPermission> getPermissions(String loginOrEmail, String tenant);
    List<String> listAllSchemas();
    Map<String, Object> getAdditionalTokenInformation(FlyUser flyUser, String login, Long id);

    void sendNewPassword(String username);
    void resetPassword(String changePasswordKey, String password, String confirmPassword);
}
