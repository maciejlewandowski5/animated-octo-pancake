package model;

import junit.framework.TestCase;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class UserTest {

    @Test
    public void additionUser_isCorrect(){
        User user = new User("Name");
        assertNotNull(user);
    }

}