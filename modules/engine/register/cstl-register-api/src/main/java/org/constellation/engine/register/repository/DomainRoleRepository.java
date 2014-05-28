package org.constellation.engine.register.repository;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.constellation.engine.register.Domain;
import org.constellation.engine.register.DomainRole;
import org.constellation.engine.register.Permission;
import org.constellation.engine.register.User;

public interface DomainRoleRepository {

    List<DomainRole> findAll();
    
    DomainRole save(DomainRole group);

    DomainRole update(DomainRole group);

    void delete(int id);

    DomainRole findOneWithPermission(int id);
    
    List<Permission> allPermission();
   
    Map<DomainRole, List<Pair<User, List<Domain>>>> findAllWithMembers();
    

}