package it.polito.mobilecourseproject.poliapp;

import android.graphics.Bitmap;

import com.parse.ParseException;
import com.parse.ParseUser;

import it.polito.mobilecourseproject.poliapp.messages.MessageService;
import it.polito.mobilecourseproject.poliapp.model.User;

/**
 *
 */
public class AccountManager {




    public static ParseUser login(String username, String password) throws ParseException {
        return ParseUser.logIn(username, password);
    }


    public static void signup(String firstName, String lastName,String companyName, String email, String password, String university,boolean isCompany,Bitmap b) throws ParseException {

        //create ParseUser
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setCompanyName(companyName);
        user.setUsername(email);
        user.setPassword(password);
        user.setUniversity(university);
        if(!isCompany)user.setStudent();
        else user.setCompany();
        user.signUp();

        if(b!=null){
            try {
                user.updatePhotoSync(b);
            }catch (Exception e){
                e.printStackTrace();
            }

        }

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


    public static boolean checkIfStudentLoggedIn(){
        User currentUser = null;
        try {
            currentUser = AccountManager.getCurrentUser();
            if (currentUser != null) {
                if(currentUser.isCompany())return false;
                else return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }



    public static User getCurrentUser() throws Exception {
       if(checkIfLoggedIn())return (User)ParseUser.getCurrentUser();
        else throw new Exception("No user logged");

    }

   public static void logout(){
       PoliApp.getModel().flush();

       ParseUser.logOut();
       MessageService.restart();
   }

}
