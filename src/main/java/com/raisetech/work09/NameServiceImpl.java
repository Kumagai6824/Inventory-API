package com.raisetech.work09;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NameServiceImpl implements NameService {
    private final NameMapper nameMapper;

    public NameServiceImpl(NameMapper nameMapper) {
        this.nameMapper = nameMapper;
    }

    @Override
    public List<Name> findAll() {
        return nameMapper.findAll();
    }

    @Override
    public Name findById(int id) {
        return this.nameMapper.findById(id).orElseThrow(() -> new ResourceNotFoundException("resource not found with id: " + id));
    }

    @Override
    public void createName(Name name) {
        nameMapper.createName(name);
    }

    @Override
    public void patchById(int id, String name) {
        nameMapper.findById(id).orElseThrow(() -> new ResourceNotFoundException("resource not found with id: " + id));
        nameMapper.patchById(id, name);
    }

    @Override
    public void deleteById(int id) {
        nameMapper.findById(id).orElseThrow(() -> new ResourceNotFoundException("resource not found with id: " + id));
        nameMapper.deleteById(id);
    }
}
