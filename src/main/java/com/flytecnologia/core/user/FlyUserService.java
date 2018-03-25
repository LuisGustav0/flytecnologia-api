package com.flytecnologia.core.user;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface FlyUserService {
    String getMessageInvalidLogin();
    Optional<FlyUser> findByLogin(String login);
    List<FlyUserPermission> getPermissions(String login, String tenant);
    List<String> listAllSchemas();
    Map<String, Object> getAdditionalTokenInformation(Long id);

    void sendNewPassword(String username);
    void resetPassword(String changePasswordKey, String password, String confirmPassword);
}
