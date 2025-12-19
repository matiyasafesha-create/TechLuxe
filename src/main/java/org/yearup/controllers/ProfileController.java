package org.yearup.controllers;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
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
@Slf4j
public class ProfileController {

    private static final Logger log = LoggerFactory.getLogger(ProfileController.class);
    private final ProfileDao profileDao;
    private final UserDao userDao;

    @Autowired

    public ProfileController(ProfileDao profileDao, UserDao userDao) {
        this.profileDao = profileDao;
        this.userDao = userDao;


    }

    @GetMapping
    public Profile getProfile (Principal principal) {

        List<Profile> profiles = profileDao.getAllProfiles();

        Profile profileList = new Profile();
        profiles.forEach(profileList::add);

        return profileList;
    }


   @PutMapping
    @PreAuthorize("isAuthenticated()")
    public Profile getMyProfile(Principal principal,Profile profile) {

        String username = principal.getName();
        log.info("This is user {}", username);
        User user = userDao.getByUserName(username);


        //get the user by id



       //
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        }
//        user.getUsername()


        List<Profile> profiles =
                profileDao.getProfilesByUserId(user.getId());

        Profile profileList = new Profile();
        profiles.forEach(profileList::add);

        return profileList;
    }
}

