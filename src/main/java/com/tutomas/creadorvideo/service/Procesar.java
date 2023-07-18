package com.tutomas.creadorvideo.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Procesar {

    public static void convertirImagenesAVideo(List<String> imagenes, String videoPath, int duracion) throws IOException, InterruptedException {
        File listFile = crearListado(imagenes, duracion);

        Path video = Path.of(videoPath);
        if (Files.exists(video)) {
            Files.delete(video);
        }

        // sudo apt install ffmpeg
        // ffmpeg -f concat -i listado.txt -vf fps=30 -c:v libx264 -crf 23 -pix_fmt yuv420p video.mp4
        ProcessBuilder proceso = new ProcessBuilder(
                "ffmpeg",
                "-f", "concat",
                "-i", listFile.getAbsolutePath(),
                "-vf", "fps=30",
                "-c:v", "libx264",
                "-crf", "23",
                "-pix_fmt", "yuv420p",
                videoPath
        );

        Process p = proceso.start();
        p.waitFor();

        Files.delete(listFile.toPath());

        imagenes.forEach(imagen -> {
            try {
                Files.delete(Path.of(imagen));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static File crearListado(List<String> imagenes, int duracion) {
        File listFile = new File("listado.txt");
        try (PrintWriter writer = new PrintWriter(listFile)) {
            for (String imagen : imagenes) {
                writer.println("file '" + imagen + "'");
                writer.println("duration " + duracion);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return listFile;
    }

}
