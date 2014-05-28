package org.constellation.engine.register.jooq;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.constellation.engine.register.Data;
import org.constellation.engine.register.Domain;
import org.constellation.engine.register.User;
import org.constellation.engine.register.Provider;

public class TestSamples {

    public static User newAdminUser() {
        User user = new User();
        user.setFirstname("olivier");
        user.setLastname("Nouguier");
        user.setLogin("olivier");
        user.setEmail("olvier.nouguier@gmail.com");
        user.setPassword("zozozozo");

        user.setFirstname("olivier");


        return user;
    }

    public static Domain newDomain() {
        return new Domain("Domain1", "Test domain");
    }
    
    
    public static Data newData(User owner, Provider provider) {
        Data data = new Data();
        data.setDate(new Date().getTime());
        data.setDescription(1);
        data.setName("testdata");
        data.setNamespace("");
        data.setOwner(owner.getLogin());
        data.setType("type");
        data.setProviderId(provider.getId());
        return data;
    }

    public static Provider newProvider(User owner) {
        Provider provider = new Provider();
        provider.setIdentifier("test");
        provider.setImpl("immmmp");
        provider.setOwner(owner.getLogin());
        provider.setType("coverage");
        provider.setParent("");
        provider.setConfig("<root />");
        return provider;
    }

    public static List<String> adminRoles() {
        return Collections.singletonList("cstl-admin");
    }

}