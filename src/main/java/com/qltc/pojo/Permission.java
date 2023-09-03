package com.qltc.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.util.Set;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Data;


@Entity
@Data
@Table(name = "permissions")
public class Permission implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    private Integer id;
    
    @Basic(optional = false)
    private String value;
    
    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "permission", fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<UserPermission> userPermissions;
    
    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "permission", fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<UserGroupPermission> userGroupPermissions;
    
    public void addUserPermission(UserPermission userPermission) {
        userPermissions.add(userPermission);
        userPermission.setPermission(this);
    }
    
    public void removeUserPermission(UserPermission userPermission) {
        userPermissions.remove(userPermission);
        userPermission.setPermission(null);
    }
    
    public void addUserGroupPermission(UserGroupPermission userGroupPermission) {
        userGroupPermissions.add(userGroupPermission);
        userGroupPermission.setPermission(this);
    }
    
    public void removeUserGroupPermission(UserGroupPermission userGroupPermission) {
        userGroupPermissions.remove(userGroupPermission);
        userGroupPermission.setPermission(null);
    }
}
