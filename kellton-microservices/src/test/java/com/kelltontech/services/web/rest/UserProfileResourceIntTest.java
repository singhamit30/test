package com.kelltontech.services.web.rest;

import com.kelltontech.services.MicroservicesApp;

import com.kelltontech.services.domain.UserProfile;
import com.kelltontech.services.service.UserProfileServices;
import com.kelltontech.services.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

import static com.kelltontech.services.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the UserProfileResource REST controller.
 *
 * @see UserProfileResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MicroservicesApp.class)
public class UserProfileResourceIntTest {

    private static final String DEFAULT_USER_NAME = "AAAAAAAAAA";
    private static final String UPDATED_USER_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_USER_EMAIL_ID = "AAAAAAAAAA";
    private static final String UPDATED_USER_EMAIL_ID = "BBBBBBBBBB";

    private static final Long DEFAULT_USER_MOBILE_NO = 1L;
    private static final Long UPDATED_USER_MOBILE_NO = 2L;

    @Autowired
    private UserProfileServices userProfileServices;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restUserProfileMockMvc;

    private UserProfile userProfile;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final UserProfileResource userProfileResource = new UserProfileResource(userProfileServices);
        this.restUserProfileMockMvc = MockMvcBuilders.standaloneSetup(userProfileResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserProfile createEntity(EntityManager em) {
        UserProfile userProfile = new UserProfile()
            .userName(DEFAULT_USER_NAME)
            .userEmailId(DEFAULT_USER_EMAIL_ID)
            .userMobileNo(DEFAULT_USER_MOBILE_NO);
        return userProfile;
    }

    @Before
    public void initTest() {
        userProfile = createEntity(em);
    }

    @Test
    @Transactional
    public void createUserProfile() throws Exception {
        int databaseSizeBeforeCreate = userProfileServices.findAllUsers().size();

        // Create the UserProfile
        restUserProfileMockMvc.perform(post("/api/user-profiles")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(userProfile)))
            .andExpect(status().isCreated());

        // Validate the UserProfile in the database
        List<UserProfile> userProfileList = userProfileServices.findAllUsers();
        assertThat(userProfileList).hasSize(databaseSizeBeforeCreate + 1);
        UserProfile testUserProfile = userProfileList.get(userProfileList.size() - 1);
        assertThat(testUserProfile.getUserName()).isEqualTo(DEFAULT_USER_NAME);
        assertThat(testUserProfile.getUserEmailId()).isEqualTo(DEFAULT_USER_EMAIL_ID);
        assertThat(testUserProfile.getUserMobileNo()).isEqualTo(DEFAULT_USER_MOBILE_NO);
    }

    @Test
    @Transactional
    public void createUserProfileWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = userProfileServices.findAllUsers().size();

        // Create the UserProfile with an existing ID
        userProfile.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restUserProfileMockMvc.perform(post("/api/user-profiles")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(userProfile)))
            .andExpect(status().isBadRequest());

        // Validate the UserProfile in the database
        List<UserProfile> userProfileList = userProfileServices.findAllUsers();
        assertThat(userProfileList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllUserProfiles() throws Exception {
        // Initialize the database
    	userProfileServices.save(userProfile);

        // Get all the userProfileList
        restUserProfileMockMvc.perform(get("/api/user-profiles?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userProfile.getId().intValue())))
            .andExpect(jsonPath("$.[*].userName").value(hasItem(DEFAULT_USER_NAME.toString())))
            .andExpect(jsonPath("$.[*].userEmailId").value(hasItem(DEFAULT_USER_EMAIL_ID.toString())))
            .andExpect(jsonPath("$.[*].userMobileNo").value(hasItem(DEFAULT_USER_MOBILE_NO.intValue())));
    }

    @Test
    @Transactional
    public void getUserProfile() throws Exception {
        // Initialize the database
    	userProfileServices.saveAndFlush(userProfile);

        // Get the userProfile
        restUserProfileMockMvc.perform(get("/api/user-profiles/{id}", userProfile.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(userProfile.getId().intValue()))
            .andExpect(jsonPath("$.userName").value(DEFAULT_USER_NAME.toString()))
            .andExpect(jsonPath("$.userEmailId").value(DEFAULT_USER_EMAIL_ID.toString()))
            .andExpect(jsonPath("$.userMobileNo").value(DEFAULT_USER_MOBILE_NO.intValue()));
    }

    @Test
    @Transactional
    public void getNonExistingUserProfile() throws Exception {
        // Get the userProfile
        restUserProfileMockMvc.perform(get("/api/user-profiles/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateUserProfile() throws Exception {
        // Initialize the database
    	userProfileServices.saveAndFlush(userProfile);
        int databaseSizeBeforeUpdate = userProfileServices.findAllUsers().size();

        // Update the userProfile
        UserProfile updatedUserProfile = userProfileServices.findById(userProfile.getId());
        // Disconnect from session so that the updates on updatedUserProfile are not directly saved in db
        em.detach(updatedUserProfile);
        updatedUserProfile
            .userName(UPDATED_USER_NAME)
            .userEmailId(UPDATED_USER_EMAIL_ID)
            .userMobileNo(UPDATED_USER_MOBILE_NO);

        restUserProfileMockMvc.perform(put("/api/user-profiles")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedUserProfile)))
            .andExpect(status().isOk());

        // Validate the UserProfile in the database
        List<UserProfile> userProfileList = userProfileServices.findAllUsers();
        assertThat(userProfileList).hasSize(databaseSizeBeforeUpdate);
        UserProfile testUserProfile = userProfileList.get(userProfileList.size() - 1);
        assertThat(testUserProfile.getUserName()).isEqualTo(UPDATED_USER_NAME);
        assertThat(testUserProfile.getUserEmailId()).isEqualTo(UPDATED_USER_EMAIL_ID);
        assertThat(testUserProfile.getUserMobileNo()).isEqualTo(UPDATED_USER_MOBILE_NO);
    }

    @Test
    @Transactional
    public void updateNonExistingUserProfile() throws Exception {
        int databaseSizeBeforeUpdate = userProfileServices.findAllUsers().size();

        // Create the UserProfile

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restUserProfileMockMvc.perform(put("/api/user-profiles")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(userProfile)))
            .andExpect(status().isCreated());

        // Validate the UserProfile in the database
        List<UserProfile> userProfileList = userProfileServices.findAllUsers();
        assertThat(userProfileList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteUserProfile() throws Exception {
        // Initialize the database
    	userProfileServices.saveAndFlush(userProfile);
        int databaseSizeBeforeDelete = userProfileServices.findAllUsers().size();

        // Get the userProfile
        restUserProfileMockMvc.perform(delete("/api/user-profiles/{id}", userProfile.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<UserProfile> userProfileList = userProfileServices.findAllUsers();
        assertThat(userProfileList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserProfile.class);
        UserProfile userProfile1 = new UserProfile();
        userProfile1.setId(1L);
        UserProfile userProfile2 = new UserProfile();
        userProfile2.setId(userProfile1.getId());
        assertThat(userProfile1).isEqualTo(userProfile2);
        userProfile2.setId(2L);
        assertThat(userProfile1).isNotEqualTo(userProfile2);
        userProfile1.setId(null);
        assertThat(userProfile1).isNotEqualTo(userProfile2);
    }
}
