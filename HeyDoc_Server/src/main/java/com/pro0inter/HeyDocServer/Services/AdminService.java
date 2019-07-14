package com.pro0inter.HeyDocServer.Services;

import com.pro0inter.HeyDocServer.Domain.Admin;
import com.pro0inter.HeyDocServer.Repositories.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdminService {
    @Autowired
    private AdminRepository adminRepository;


    public Admin get_admin(Long admin_id) {
        Optional<Admin> optionalAdmin = adminRepository.findById(admin_id);
        if (optionalAdmin.isPresent())
            return optionalAdmin.get();
        return null;
    }

    public Admin add_admin(Admin admin) {
        return adminRepository.save(admin);
    }

    public Admin update_admin(Admin admin) {
        return adminRepository.save(admin);
    }

    public void delete_admin(Long admin_id) {
        adminRepository.deleteById(admin_id);
    }


    public List<Admin> getAll() {
        return adminRepository.findAll();
    }


    public boolean existByUserId(Long user_id) {
        return adminRepository.existByUserId(user_id) >= 1;
    }

    public Admin findByUserId(Long user_id) {
        Optional<Admin> optionalAdmin = adminRepository.findByUserId(user_id);
        if (optionalAdmin.isPresent())
            return optionalAdmin.get();
        return null;
    }

}
