package com.bookstory.store.service;

import com.bookstory.store.web.dto.NewProductDTO;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

public interface FileService {
    Mono<String> saveImage(Mono<Tuple2<String, byte[]>> data);
    Flux<NewProductDTO> getNewProductDtosFromFile(FilePart imageFile);
}