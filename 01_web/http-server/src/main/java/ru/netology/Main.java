package ru.netology;

import java.io.*;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

public class Main {
  public static void main(String[] args) throws IOException {
    Server server = new Server();
    server.startServer(9999);

  }
}


