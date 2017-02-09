package com.rtree.core.rtree;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.concurrent.Callable;

final class ImageSaver {

    private ImageSaver() {
    }

    static void save(final BufferedImage image, final File file,
                     final String imageFormat) {
        Callable<Void> callable = () -> {
            ImageIO.write(image, imageFormat, file);
            return null;
        };
        run(callable);
    }

    private static void run(Callable<Void> callable) {
        try {
            callable.call();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
