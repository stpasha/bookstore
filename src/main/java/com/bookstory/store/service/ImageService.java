package com.bookstory.store.service;

import java.io.IOException;

public interface ImageService {
    String saveImage(String filename, byte[] data) throws IOException;
}