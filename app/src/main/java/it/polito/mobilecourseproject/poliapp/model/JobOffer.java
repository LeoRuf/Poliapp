package it.polito.mobilecourseproject.poliapp.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;

import java.util.List;

/**
 * Created by Enrico on 19/06/15.
 */
@ParseClassName("JobOffer")
public class JobOffer extends ParseObject {





    public String getTitle() {
        return getString("title");
    }

    public void setTitle(String value) {
        put("title", value);
    }


    public String getDescription() {
        return getString("description");
    }

    public void setDescription(String value) {
        put("description", value);
    }

    public String getCompany() {
        return getString("company");
    }

    public void setCompany(String value) {
        put("company", value);
    }

    public String getCompanyDescription() {
        return getString("companyDescription");
    }

    public void setCompanyDescription(String value) {
        put("companyDescription", value);
    }

    public String getLocation() {
        return getString("location");
    }

    public void setLocation(String value) {
        put("location", value);
    }

    public String getEmailAddress() {
        return getString("emailAddress");
    }

    public void setEmailAddress(String value) {
        put("emailAddress", value);
    }

    public String getWebsite() {
        return getString("website");
    }

    public void setWebsite(String value) {
        put("website", value);
    }

    public String getEmploymentType() {
        return getString("employmentType");
    }

    public void setEmploymentType(String value) {
        put("employmentType", value);
    }

    public String getPrerequisites() {
        return getString("prerequisites");
    }

    public void setPrerequisites(String value) {
        put("prerequisites", value);
    }

    public String getResponsibilities() {
        return getString("responsibilities");
    }

    public void setResponsibilities(String value) {
        put("responsibilities", value);
    }


    public User getPublisher() {
        return (User)getParseUser("user");
    }

    public void setUser(User value) {
        put("user", value);
    }

}
