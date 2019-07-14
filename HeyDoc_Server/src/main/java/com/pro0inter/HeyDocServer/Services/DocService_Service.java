package com.pro0inter.HeyDocServer.Services;

import com.pro0inter.HeyDocServer.Domain.DocService;
import com.pro0inter.HeyDocServer.Repositories.DocServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DocService_Service {
    @Autowired
    private DocServiceRepository docServiceRepository;


    public DocService get_docService(Long docService_id) {
        Optional<DocService> optionalDocService = docServiceRepository.findById(docService_id);
        if (optionalDocService.isPresent())
            return optionalDocService.get();
        return null;
    }

    public DocService get_docService(String docService_title) {
        Optional<DocService> optionalDocService = docServiceRepository.findByTitleIgnoreCase(docService_title);
        if (optionalDocService.isPresent())
            return optionalDocService.get();
        return null;
    }

    public DocService add_docService(DocService docService) {
        return docServiceRepository.save(docService);
    }

    public DocService update_docService(DocService docService) {
        return docServiceRepository.save(docService);
    }

    public void delete_docService(Long docService_id) {
        docServiceRepository.deleteById(docService_id);
    }


    public List<DocService> getAll() {
        return docServiceRepository.findAll();
    }
}
