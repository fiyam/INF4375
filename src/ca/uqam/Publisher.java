/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.uqam;


/**
 *
 * @author mchamseddine
 */
public class Publisher {
    
    private Boolean activity;    
    private Float temperature; 
    private String time; //potvrda o upravljanju  klimom
    private String door_lock_sensor;

    
    public Publisher( Boolean v, Float temp , String ti , String d){
        
        this.activity = v;
        this.temperature = temp;
        this.time = ti;
        this.door_lock_sensor = d;
    }
    
    
    
    
    public Boolean getActivity() {
        return activity;
    }

    public void setActivity(Boolean activity) {
        this.activity = activity;
    }

    public Float getTemperature() {
        return temperature;
    }

    public void setTemperature(Float temperature) {
        this.temperature = temperature;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDoor_lock_sensor() {
        return door_lock_sensor;
    }

    public void setDoor_lock_sensor(String door_lock_sensor) {
        this.door_lock_sensor = door_lock_sensor;
    }
    
    
    
    
 
    
    
    
    
}