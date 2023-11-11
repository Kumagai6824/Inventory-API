package com.raisetech.work09;

import java.util.List;

public interface NameService {
    List<Name> findAll();

    Name findById(int id) throws Exception;

    void createName(Name name);

    void patchById(int id, String name) throws Exception;

    void deleteById(int id) throws Exception;
}
