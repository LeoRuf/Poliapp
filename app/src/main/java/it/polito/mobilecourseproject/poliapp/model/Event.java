package it.polito.mobilecourseproject.poliapp.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by Leo on 03/02/2016.
 */
@ParseClassName("Event")
public class Event extends ParseObject {

    public String getMateria() {
        return getString("materia");
    }

    public String getProfessore() {
        return getString("professore");
    }

    public String getData() {
        return getString("data");
    }

    public String getAula() {
        return getString("aula");
    }

    public void setMateria(String value) {
        put("materia", value);
    }

    public void setProfessore(String value) {
        put("professore", value);
    }

    public void setAula(String value) {
        put("aula", value);
    }

    public void setData(String value) {
        put("data", value);
    }
}
