package co.vasic.rest;

import java.net.ServerSocket;

public interface RestServiceInterface {
    void listen();

    void close();

    ServerSocket getListener();
}
