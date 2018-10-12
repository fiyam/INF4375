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
public class Subscriber {
    
    private Boolean heater;    
    private Boolean ac; 
    private Boolean lights; //potvrda o upravljanju  klimom
    private Boolean door_lock;

    
      public Subscriber( Boolean h, Boolean a , Boolean l , Boolean d){
        
        this.heater = h;
        this.ac = a;
        this.lights = l;
        this.door_lock = d;
    }
    
    
    
    
    
    public Boolean getHeater() {
        return heater;
    }

    public void setHeater(Boolean heater) {
        this.heater = heater;
    }

    public Boolean getAc() {
        return ac;
    }

    public void setAc(Boolean ac) {
        this.ac = ac;
    }

    public Boolean getLights() {
        return lights;
    }

    public void setLights(Boolean lights) {
        this.lights = lights;
    }

    public Boolean getDoor_lock() {
        return door_lock;
    }

    public void setDoor_lock(Boolean door_lock) {
        this.door_lock = door_lock;
    }

    
    
    
    
}
