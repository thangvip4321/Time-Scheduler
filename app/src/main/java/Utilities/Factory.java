package Utilities;

import repositories.PostgreAdapter;
import usecases.Services;


/** 
    this is just a helper class for ease of creating some object.
 */
public class Factory {
    
    /** 
     * @return Services
     */
    public static Services createService(){
        return new Services(new PostgreAdapter());
    }
}
