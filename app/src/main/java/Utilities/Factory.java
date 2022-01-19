package Utilities;

import repositories.PostgreAdapter;
import usecases.Services;

public class Factory {
    public static Services servicesFactory(){
        return new Services(new PostgreAdapter());
    }
}
