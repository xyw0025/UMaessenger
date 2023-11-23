package com.group.cs520.service;
import com.group.cs520.model.Profile;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.group.cs520.model.Preference;
import com.group.cs520.model.User;
import com.group.cs520.repository.ProfileRepository;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.lang.reflect.Field;

@Service
public class ProfileService {
    public ProfileService() {

    }

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private UserService userService;


    @Autowired
    private MongoTemplate mongoTemplate;

    public List<Profile> allProfiles() {
        return profileRepository.findAll();
    }

    public Profile singleProfile(ObjectId id) {
        return profileRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Profile not found"));
    }


    public Profile create(Map<String, String> profileMap) {
        // String[] keys = {"displayName", "gender", "age", "bio"};
        // Map<String, String> map = profileMap.entrySet().stream().filter(x -> x.getKey())
        Profile profile = new Profile(profileMap);
        profileRepository.insert(profile);

        userService.setProfile(profileMap.get("user_id"), profile);
        return profile;
    }

    public Profile update(ObjectId id,Map<String, String> profileMap) {
        Profile profile = this.singleProfile(id);

        Class<?> profileClass = Profile.class;
        Field[] fields = profileClass.getDeclaredFields();

        for (Field field : fields) {
            String fieldName = field.getName();
            if (profileMap.containsKey(fieldName)) {
                setField(profile, field, profileMap.get(fieldName));
            }
        }
        profileRepository.save(profile);
        return profile;
    }

     private void setField(Object object, Field field, String value) {
        try {
            field.setAccessible(true);
            Class<?> fieldType = field.getType();

            if (fieldType.equals(Integer.class) || fieldType.equals(int.class)) {
                field.set(object, Integer.parseInt(value));
            } else {
                field.set(object, value);
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Error setting field: " + field.getName(), e);
        }
    }

    public void setPreference(String profile_id, Preference preference) {
        // mongoTemplate.update(Profile.class)
        //     .matching(Criteria.where("id").is(profile_id))
        //     .apply(new Update().push("preferences").value(preference))
        //     .first();
        Profile profile = this.singleProfile(TypeUtil.objectIdConverter(profile_id));
        List<Preference> preferences = profile.getPreferences();
        preferences.add(preference);
        profile.setPreferences(preferences);
    
        profileRepository.save(profile);
    }

}