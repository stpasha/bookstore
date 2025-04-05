package com.bookstory.store.service;

import com.bookstory.store.web.dto.NewProductDTO;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class DefaultFileService implements FileService {

    @Value("${image.upload.dir}")
    private String uploadDir;

    @Override
    public Mono<String> saveImage(Mono<Tuple2<String, byte[]>> data) {
        return data.flatMap(tuple -> Mono.fromCallable(() -> {
            Path imagePath = Path.of(uploadDir, tuple.getT1());
            if (imagePath.getParent() != null) {
                Files.createDirectories(imagePath.getParent());
            }
            Files.write(imagePath, tuple.getT2());
            return "/uploads/images/" + tuple.getT1();
        })).onErrorMap(IOException.class, e -> new RuntimeException("Failed to save image", e));
    }

    @Override
    public Flux<NewProductDTO> getNewProductDtosFromFile(FilePart filePart) {
        return DataBufferUtils.join(filePart.content())
                .map(dataBuffer -> {
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    return new String(bytes, StandardCharsets.UTF_8);
                })
                .flatMapMany(content -> {
                    try (CSVParser parser = CSVParser.parse(content, CSVFormat.Builder.create().setDelimiter(',').setHeader().setSkipHeaderRecord(true).get())) {
                        return Flux.fromIterable(parser.getRecords()).flatMap(record -> {
                            try {
                                return Mono.just(NewProductDTO.builder()
                                        .title(record.get("title").trim())
                                        .description(record.get("description").trim())
                                        .price(new BigDecimal(record.get("price").trim()))
                                        .imageName(record.get("imageName").trim())
                                        .quantityAvailable(Long.parseLong(record.get("quantity").trim()))
                                        .baseImage(record.get("baseImage").trim())
                                        .build());
                            } catch (Exception e) {
                                return Mono.error(new RuntimeException("Ошибка парсинга CSV", e));
                            }
                        });
                    } catch (IOException e) {
                        return Flux.error(new RuntimeException("Ошибка чтения файла", e));
                    }
                });
    }
}

