package com.dwinged.boof;


import boofcv.abst.feature.detdesc.DetectDescribePoint;
import boofcv.alg.descriptor.UtilFeature;
import boofcv.io.image.ConvertBufferedImage;
import boofcv.io.image.UtilImageIO;
import boofcv.struct.feature.TupleDesc;
import boofcv.struct.image.ImageGray;
import org.ddogleg.struct.FastQueue;

import java.awt.image.BufferedImage;
import java.io.File;


public class InterestPointsAnalyzer<T extends ImageGray, TD extends TupleDesc> {

    private DetectDescribePoint<T, TD> detDesc;

    private Class<T> imageType;


    /**
     * Analyzer constructor.
     *
     * @param detDesc algorithm used to detect and describe interest points.
     * @param imageType type of image to use.
     */
    public InterestPointsAnalyzer(DetectDescribePoint<T, TD> detDesc, Class<T> imageType) {
        this.detDesc = detDesc;
        this.imageType = imageType;
    }


    /**
     * Gets descriptors of image.
     *
     * @param path absolute path.
     * @return descriptors.
     */
    public FastQueue<TD> getDescriptors(String path) {
        BufferedImage bufferedImage = UtilImageIO.loadImage(path);
        T input = ConvertBufferedImage.convertFromSingle(bufferedImage, null, imageType);

        // stores the description of detected interest points
        FastQueue<TD> descriptors = UtilFeature.createQueue(detDesc, 100);

        detDesc.detect(input);
        for(int i = 0; i < detDesc.getNumberOfFeatures(); i++) {
            descriptors.grow().setTo(detDesc.getDescription(i));
        }

        return descriptors;
    }

}
