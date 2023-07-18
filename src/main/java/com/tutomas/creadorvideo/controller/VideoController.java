package com.tutomas.creadorvideo.controller;

import com.tutomas.creadorvideo.service.Procesar;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/video")
@CrossOrigin(origins = "*")
public class VideoController {

    @PostMapping(value = "/cargar/imagen", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> CargarImagen(@RequestPart("imagen") MultipartFile imagen) throws IOException {
        String originalName = StringUtils.cleanPath(imagen.getOriginalFilename());
        String nuevoNombre = System.currentTimeMillis() + Math.round(Math.random() * 100) + originalName.substring(originalName.lastIndexOf("."));
        Path path = Paths.get("imagenes/" + nuevoNombre);
        imagen.transferTo(path);

        return ResponseEntity.ok(nuevoNombre);
    }

    @PostMapping("/generar")
    public ResponseEntity<String> GenerarVideo(@RequestBody List<String> imagenes) throws IOException, InterruptedException {
        List<String> imagenesConDirectorio = imagenes.stream().map(imagen -> "imagenes/" + imagen).toList();

        Procesar.convertirImagenesAVideo(imagenesConDirectorio, "video/generado.mp4", 5);
        return ResponseEntity.ok("Video generado exitosamente");
    }

    @GetMapping(value = "/stream/{fileName}", produces = "video/mp4")
    public ResponseEntity<byte[]> streaming(@PathVariable String fileName) throws IOException {
        Path path = Paths.get("video/" + fileName);

        long fileSize = Files.size(path);
        byte[] contenido = Files.readAllBytes(path);

        if (fileSize > 0) {
            Files.delete(path);
        }

        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .header("Accept-Ranges", "bytes")
                .header("Content-Range", "bytes 0-" + (fileSize - 1) + "/" + fileSize)
                .contentLength(fileSize)
                .body(contenido);
    }

}
