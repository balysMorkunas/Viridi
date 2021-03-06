package gogreenserver.controller;

import com.google.gson.Gson;
import model.ActionLog;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.Base64;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import gogreenserver.repository.*;
import model.*;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.junit.Rule;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import com.google.gson.JsonObject;

import java.io.DataInput;
import java.io.File;
import java.lang.reflect.Array;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;


@ExtendWith(SpringExtension.class)
@WebMvcTest(value = BasicController.class, secure = false)
public class BasicController5Test {
    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserRepository personService;

    @MockBean
    private ActionRepository actionService;

    @MockBean
    private FoodLogRepository foodLogService;

    @MockBean
    private FollowerRepository followerService;

    @MockBean
    private AchievementRepository achievementRepository;

    @MockBean
    private AchievementLogRepository achievementLogRepository;

    @MockBean
    private VoteRepository voteService;

    Person test1 = new Person(1, "goerge@test.com","Gorge", "La", "$2a$10$XbwoFtp0b0IcuMeVVBC6VeKQu.U9V/vJIsOOsU/Vd6sZ7kpXVmEom", 200);

    Person test2 = new Person(2, "blue@test.com","Blue", "Red", "$2a$10$XbwoFtp0b0IcuMeVVBC6VeKQu.U9V/vJIsOOsU/Vd6sZ7kpXVmEom", 250);

    Person test3 = new Person(3,"test@hotmail.com", "Steve", "Smith", "$2a$10$XbwoFtp0b0IcuMeVVBC6VeKQu.U9V/vJIsOOsU/Vd6sZ7kpXVmEom", 50);

    Person test4 = new Person(4,"carlemail@hotmail.com", "Carl", "Midge", "$2a$10$XH6Pj.z1YHEMNJ4hh/cfgupqEtU94vRlaNVFKdv1s8S91pe1XG2OO", 500);

    Person test5 = new Person(5,"beans@hotmail.com", "Elisabet", "Frits", "$2a$10$XbwoFtp0b0IcuMeVVBC6VeKQu.U9V/vJIsOOsU/Vd6sZ7kpXVmEom", 70);

    Date dateTime = new Date();

    ActionLog actionLog = new ActionLog(0, "vegetarian", "Description", 50, dateTime, 5, "Bob");


    Follower follow1 = new Follower(1, 1, 2);

    Follower follow2 = new Follower(2,1,3);

    ArrayList<Person> followerList = new ArrayList<Person>();

    String image = "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";

    @org.junit.jupiter.api.Test
    void imageTrue() throws Exception {

        ActionLog testLog1 = new ActionLog(1, "vegetarian", "Eating a veg meal", 50, new Date(), 1, "Steve");

        new File(System.getProperty("user.home") + "\\test\\").mkdirs();
        File file = new File(System.getProperty("user.home") + "\\test\\", "a" + 1 + ".jpg");
        file.createNewFile();
        byte[] decodedBytes = Base64.getDecoder().decode(image);
        FileUtils.writeByteArrayToFile(file, decodedBytes);
        file.deleteOnExit();

        testLog1.setFilePath(file.getPath());

        String encodedImage = BasicController.getEncodedImageActionLog(testLog1);

        assertEquals(image, encodedImage);
    }

    @org.junit.jupiter.api.Test
    void updateImageTrue() throws Exception {
        test1.setImage(null);

        Mockito.when(personService.findByEmail(Mockito.anyString())).thenReturn(test1);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/login?email=goerge@test.com&password=12345").accept(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(requestBuilder).andReturn();

        ObjectMapper mapper = new ObjectMapper();

        AuthKey key = mapper.readValue(result.getResponse().getContentAsString(), AuthKey.class);



        JsonObject json = new JsonObject();
        json.addProperty("Image", image);
        json.addProperty("AuthKey", key.getAuthKey());
        String jsonAsString = json.toString();

        RequestBuilder requestBuilder2 = post("/updateImage").accept(MediaType.APPLICATION_JSON).content( jsonAsString);
        MvcResult result2 = mvc.perform(requestBuilder2).andReturn();

        Boolean sendFood = mapper.readValue(result2.getResponse().getContentAsString(), Boolean.class);

        assertTrue(sendFood);
    }

    @org.junit.jupiter.api.Test
    void updateImageFalse() throws Exception {

        Mockito.when(personService.findByEmail(Mockito.anyString())).thenReturn(test1);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/login?email=goerge@test.com&password=12345").accept(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(requestBuilder).andReturn();

        ObjectMapper mapper = new ObjectMapper();

        AuthKey key = mapper.readValue(result.getResponse().getContentAsString(), AuthKey.class);

        JsonObject json = new JsonObject();
        json.addProperty("Image", "blue");
        json.addProperty("AuthKey", key.getAuthKey() + "False");
        String jsonAsString = json.toString();

        RequestBuilder requestBuilder2 = post("/updateImage").accept(MediaType.APPLICATION_JSON).content( jsonAsString);
        MvcResult result2 = mvc.perform(requestBuilder2).andReturn();

        Boolean sendFood = mapper.readValue(result2.getResponse().getContentAsString(), Boolean.class);

        assertFalse(sendFood);
    }

    @org.junit.jupiter.api.Test
    void setImage() throws Exception{
        test1.setImage(null);

        BasicController.setImage(test1, image);

    }

    @org.junit.jupiter.api.Test
    void setImageNull() throws Exception{
        test1.setImage(null);

        BasicController.setImage(test1,  null);

    }

    @org.junit.jupiter.api.Test
    void getPoints01() {
        int result = BasicController.getPointsFromApi(1, 200, "blue");
        assertEquals(result, 1507);
    }

    @org.junit.jupiter.api.Test
    void getPoint02() {
        int result = BasicController.getPointsFromApi(4, 200, "null");
        assertEquals(0, result);
    }




}
