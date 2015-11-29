package it.polito.mobilecourseproject.poliapp;

import com.parse.ParseException;
import com.parse.ParseUser;

/**
 *
 */
public class AccountManager {




    public static ParseUser login(String username, String password) throws ParseException {
        return ParseUser.logIn(username, password);
    }


    public static void signup(String firstName, String lastName, String email, String password, String university) throws ParseException {

        //create ParseUser
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(email);
        user.setPassword(password);
        user.setUniversity(university);
        user.signUp();
        ParseUser.logOut();

    }




    public static boolean checkIfLoggedIn(){
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            // do stuff with the user
            return true;
        } else {
            // show the signup or login screen
            return false;
        }
    }



    public static User getCurrentUser() throws Exception {
       if(checkIfLoggedIn())return (User)ParseUser.getCurrentUser();
        else throw new Exception("No user logged");

    }

   public static void logout(){
       ParseUser.logOut();

   }

}
