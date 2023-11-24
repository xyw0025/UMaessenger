package com.group.cs520.service;
import com.group.cs520.model.Profile;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.group.cs520.model.Preference;
import com.group.cs520.model.User;
import com.group.cs520.repository.PreferenceRepository;
import com.group.cs520.repository.ProfileRepository;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.lang.reflect.Array;
import java.lang.reflect.Field;

@Service
public class ProfileService {
    public ProfileService() {

    }

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private PreferenceService preferenceService;

    @Autowired
    private UserService userService;


    @Autowired
    private MongoTemplate mongoTemplate;

    public List<Profile> allProfiles() {
        return profileRepository.findAll();
    }

    public Profile singleProfile(String id) {
        ObjectId profileId = TypeUtil.objectIdConverter(id);
        return profileRepository.findById(profileId).orElseThrow(() -> new IllegalArgumentException("Profile not found"));
    }


    public Profile create(Map<String, String> profileMap) {
        // String[] keys = {"displayName", "gender", "age", "bio"};
        // Map<String, String> map = profileMap.entrySet().stream().filter(x -> x.getKey())
        Profile profile = new Profile(profileMap);
        profileRepository.insert(profile);

        userService.setProfile(profileMap.get("userId"), profile);
        return profile;
    }

    public Profile update(String id, Map<String, String> profileMap) {
        Profile profile = this.singleProfile(id);

        Class<?> profileClass = Profile.class;
        Field[] fields = profileClass.getDeclaredFields();

        for (Field field : fields) {
            String fieldName = field.getName();
            if (profileMap.containsKey(fieldName)) {
                TypeUtil.setField(profile, field, profileMap.get(fieldName));
            }
        }
        profileRepository.save(profile);
        return profile;
    }


    public Profile updatePreferences(String profileId, Map<String, String> profileMap) {
        Profile profile = this.singleProfile(profileId);

        List<String> preferenceIds = TypeUtil.jsonStringArray(profileMap.get("preferenceIds"));
        List<Preference> preferences = new ArrayList<>();

        for (Integer ind = 0; ind < preferenceIds.size(); ind ++) {
            preferences.add(preferenceService.singlePreference(preferenceIds.get(ind)));
        }

        profile.setPreferences(preferences);
        profileRepository.save(profile);
        return profile;
    }

    public Profile getProfileByUser(String userId) {
        User user = userService.singleUser(userId);
        return user.getProfile();
    }
}
