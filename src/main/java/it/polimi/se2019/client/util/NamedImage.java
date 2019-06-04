package it.polimi.se2019.client.util;

import javafx.scene.image.Image;

/**
 * NamedImage extends the base Image class by adding a string type member
 * variable to store the name of the image.
 */
public class NamedImage extends Image {

        private String name;

        public NamedImage(String url, String regex) {
            super(url);
            name = url.split(regex)[1].split(".png")[0];
        }

        public String getName() {
            return name;
        }
}
