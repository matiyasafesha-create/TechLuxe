package org.yearup.data;


import org.yearup.models.Profile;

import java.util.List;

public interface ProfileDao
{
    List<Profile> getAllProfiles();
    Profile create(Profile profile);
    List<Profile> getProfilesByUserId(int userId);
}
