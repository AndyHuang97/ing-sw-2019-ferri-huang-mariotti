package it.polimi.se2019.client.util;

import javafx.scene.image.Image;

/**
 * NamedImage extends the base Image class by adding a string type member
 * variable to store the name of the image.
 *
 * @author andreahuang
 */
public class NamedImage extends Image {

        private String name;

    /**
     * The constructor of the NamedImage, that stores the name of an image.
     *
     * @param  url is the url path of the image.
     * @param regex is the regex used to remove the path of image, and get only its name.
     */
    public NamedImage(String url, String regex) {
        super(url);
        name = url.split(regex)[1].split(".png")[0];
    }

    /**
     * Getter for  name.
     * @return the name of the image.
     */
    public String getName() {
        return name;
    }
}
