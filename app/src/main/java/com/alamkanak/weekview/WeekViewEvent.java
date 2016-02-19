package com.alamkanak.weekview;

import java.util.Calendar;

/**
 * Created by Raquib-ul-Alam Kanak on 7/21/2014.
 * Website: http://april-shower.com
 */
public class WeekViewEvent {
    private String mId;
    private Calendar mStartTime;
    private Calendar mEndTime;
    private String mName;
    private int mColor;

    //////////////////////////

    private String aula;
    private String materia;
    private String professore;
    private String flag;

    //////////////////////////

    public WeekViewEvent()  {

    }

    /**
     * Initializes the event for week view.
     * @param id The id of the event.
     * @param name Name of the event.
     * @param startYear Year when the event starts.
     * @param startMonth Month when the event starts.
     * @param startDay Day when the event starts.
     * @param startHour Hour (in 24-hour format) when the event starts.
     * @param startMinute Minute when the event starts.
     * @param endYear Year when the event ends.
     * @param endMonth Month when the event ends.
     * @param endDay Day when the event ends.
     * @param endHour Hour (in 24-hour format) when the event ends.
     * @param endMinute Minute when the event ends.
     */
    public WeekViewEvent(String id, String name, int startYear, int startMonth, int startDay, int startHour, int startMinute, int endYear, int endMonth, int endDay, int endHour, int endMinute) {
        this.mId = id;

        this.mStartTime = Calendar.getInstance();
        this.mStartTime.set(Calendar.YEAR, startYear);
        this.mStartTime.set(Calendar.MONTH, startMonth-1);
        this.mStartTime.set(Calendar.DAY_OF_MONTH, startDay);
        this.mStartTime.set(Calendar.HOUR_OF_DAY, startHour);
        this.mStartTime.set(Calendar.MINUTE, startMinute);

        this.mEndTime = Calendar.getInstance();
        this.mEndTime.set(Calendar.YEAR, endYear);
        this.mEndTime.set(Calendar.MONTH, endMonth-1);
        this.mEndTime.set(Calendar.DAY_OF_MONTH, endDay);
        this.mEndTime.set(Calendar.HOUR_OF_DAY, endHour);
        this.mEndTime.set(Calendar.MINUTE, endMinute);

        this.mName = name;
    }

    /**
     * Initializes the event for week view.
     * @param id The id of the event.
     * @param name Name of the event.
     * @param startTime The time when the event starts.
     * @param endTime The time when the event ends.
     */
    public WeekViewEvent(String id, String name, Calendar startTime, Calendar endTime) {
        this.mId = id;
        this.mName = name;
        this.mStartTime = startTime;
        this.mEndTime = endTime;
    }

    public Calendar getStartTime() {
        return mStartTime;
    }

    public void setStartTime(Calendar startTime) {
        this.mStartTime = startTime;
    }

    public Calendar getEndTime() {
        return mEndTime;
    }

    public void setEndTime(Calendar endTime) {
        this.mEndTime = endTime;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public int getColor() {
        return mColor;
    }

    public void setColor(int color) {
        this.mColor = color;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        this.mId = id;
    }

    //////////////////////////

    public String getMateria() {
        return materia;
    }

    public void setMateria(String materia) {
        this.materia = materia;
    }

    public String getAula() {
        return aula;
    }

    public void setAula(String aula) {
        this.aula = aula;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String aula) {
        this.flag = flag;
    }
    public String getProfessore() {
        return professore;
    }

    public void setProfessore(String professore) {
        this.professore = professore;
    }

    public WeekViewEvent(String id, String materia, String aula, String professore, Calendar startTime, Calendar endTime, String flag) {
        this.mId = id;
        this.mStartTime = startTime;
        this.mEndTime = endTime;
        this.professore=professore;
        this.mName=materia;
        this.materia=materia;
        this.aula=aula;
        this.flag= flag;
    }

    public String getData() {
        return String.format("%s/%d dalle %02d:%02d alle %02d:%02d",mStartTime.get(Calendar.MONTH)+1, mStartTime.get(Calendar.DAY_OF_MONTH), mStartTime.get(Calendar.HOUR_OF_DAY), mStartTime.get(Calendar.MINUTE), mEndTime.get(Calendar.HOUR_OF_DAY), mEndTime.get(Calendar.MINUTE));
    }

    /////////////////////////
}
