package co.vasic;

import co.vasic.rest.RestService;

public class Main {

    public static void main(String[] args) {
        Thread restServiceT = new Thread(new RestService(10001));
        restServiceT.start();
    }

}
