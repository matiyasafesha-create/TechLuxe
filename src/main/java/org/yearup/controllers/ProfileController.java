package org.yearup.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.ProfileDao;
import org.yearup.data.UserDao;
import org.yearup.models.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/profile")
@PreAuthorize("isAuthenticated()")
@CrossOrigin

public class ProfileController {

    private final ProfileDao profileDao;
    private final UserDao userDao;

    @Autowired

    public ProfileController(ProfileDao profileDao, UserDao userDao) {
        this.profileDao = profileDao;
        this.userDao = userDao;


    }

    @GetMapping
    public Profile getAllProfiles(Principal principal) {

        List<Profile> profiles = profileDao.getAllProfiles();

        Profile profileList = new Profile();
        profiles.forEach(profileList::add);

        return profileList;
    }


    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public Profile getMyProfile(Principal principal) {

        String username = principal.getName();
        User user = userDao.getByUserName(username);

        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        List<Profile> profiles =
                profileDao.getProfilesByUserId(user.getId());

        Profile profileList = new Profile();
        profiles.forEach(profileList::add);

        return profileList;
    }
}

