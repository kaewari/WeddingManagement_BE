package com.qltc.service.impl;

import com.qltc.pojo.Permission;
import com.qltc.pojo.User;
import com.qltc.pojo.UserGroup;
import com.qltc.repository.PermissionRepository;
import com.qltc.service.PermissionService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PermissionServiceImpl implements PermissionService {

    @Autowired
    private PermissionRepository permissionRepo;

    @Override
    public List<Permission> findAll() {
        return permissionRepo.findAll();
    }

    @Override
    public Permission findById(int id) {
        return permissionRepo.findById(id);
    }

    @Override
    public List<Permission> findByName(String name) {
        return permissionRepo.findByName(name);
    }

    @Override
    public boolean getUserPermissionState(User user, Permission permission) {
        return permissionRepo.getUserPermissionState(user, permission);
    }

    @Override
    public boolean getUserGroupPermissionState(UserGroup userGroup, Permission permission) {
        return permissionRepo.getUserGroupPermissionState(userGroup, permission);
    }

    @Override
    public boolean grantPermissionForUser(User user, List<Permission> permissions) {
        return permissionRepo.grantOrDenyPermissionForUser(user, permissions, true);
    }

    @Override
    public boolean denyPermissionForUser(User user, List<Permission> permissions) {
        return permissionRepo.grantOrDenyPermissionForUser(user, permissions, false);
    }

    @Override
    public boolean grantPermissionForUserGroup(UserGroup userGroup, List<Permission> permissions) {
        return permissionRepo.grantOrDenyPermissionForGroupUser(userGroup, permissions, true);
    }

    @Override
    public boolean denyPermissionForUserGroup(UserGroup userGroup, List<Permission> permissions) {
        return permissionRepo.grantOrDenyPermissionForGroupUser(userGroup, permissions, false);
    }

    @Override
    public boolean resetAllPermissionsOfUser(User user){
        return permissionRepo.resetPermisisonOfUser(user);
    }

    @Override
    public boolean resetAllPermissionsOfUserGroup(UserGroup userGroup) {
        return permissionRepo.resetPermissionOfUserGroup(userGroup);
    }

}
