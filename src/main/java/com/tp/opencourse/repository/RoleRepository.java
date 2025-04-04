package com.tp.opencourse.repository;

import com.tp.opencourse.entity.Role;

import java.util.List;
import java.util.Optional;

public interface RoleRepository {
    List<Role> findDefaultRolesForNewlyLoggedInUser();
}
