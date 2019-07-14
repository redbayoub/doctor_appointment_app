package com.pro0inter.HeyDocServer.utils;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ObjectMapperUtils {
    private ModelMapper modelMapper = new ModelMapper();

    public ObjectMapperUtils() {
    }

    public ObjectMapperUtils(PropertyMap... configs) {
        for (PropertyMap pm : configs) {
            modelMapper.addMappings(pm);
        }
    }

    public ModelMapper getModelMapper() {
        return modelMapper;
    }

    public <D, T> D map(final T entity, Class<D> outClass) {
        return modelMapper.map(entity, outClass);
    }

    public <D, T> List<D> mapAll(final Collection<T> entityList, Class<D> outCLass) {
        return entityList.stream()
                .map(entity -> map(entity, outCLass))
                .collect(Collectors.toList());
    }


}
