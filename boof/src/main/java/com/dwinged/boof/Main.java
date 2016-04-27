package com.dwinged.boof;


import boofcv.abst.feature.associate.AssociateDescription;
import boofcv.abst.feature.associate.ScoreAssociation;
import boofcv.abst.feature.detdesc.DetectDescribePoint;
import boofcv.abst.feature.detect.interest.ConfigFastHessian;
import boofcv.factory.feature.associate.FactoryAssociation;
import boofcv.factory.feature.detdesc.FactoryDetectDescribe;
import boofcv.struct.feature.BrightFeature;
import boofcv.struct.image.GrayF32;
import org.ddogleg.struct.FastQueue;

import java.io.File;
import java.util.*;


public class Main {

    private static final String FOLDER_PATH = "images";


    public static void main(String[] args) {
        System.out.println(".... Starting analysis");

        Class imageType = GrayF32.class;

        Map<String, FastQueue<BrightFeature>> descriptorsMap = new HashMap<String, FastQueue<BrightFeature>>();

        // select which algorithms to use for detecting interest points.
        DetectDescribePoint detDesc = FactoryDetectDescribe
                .surfStable(new ConfigFastHessian(1, 2, 300, 1, 9, 4, 4), null,null, imageType);
        InterestPointsAnalyzer pointsAnalyzer = new InterestPointsAnalyzer(detDesc, imageType);
        List<File> imagesList = getImagesFiles(FOLDER_PATH);
        for (File image : imagesList) {
            FastQueue<BrightFeature> descriptors = pointsAnalyzer.getDescriptors(image.getAbsolutePath());
            descriptorsMap.put(image.getName(), descriptors);
        }

        // Get descriptors of sample
        FastQueue<BrightFeature> sampleDescriptors = pointsAnalyzer.getDescriptors(
                new File(FOLDER_PATH + "/" + args[0]).getAbsolutePath()
        );


        // select which algorithms to use to associations
        AssociateDescription associate = FactoryAssociation.greedy(
                FactoryAssociation.defaultScore(detDesc.getDescriptionType()),
                Double.MAX_VALUE,
                true
        );
        AssociateMatcher associateMatcher = new AssociateMatcher(associate);
        TreeMap<Float, String> matchesMap = new TreeMap<Float, String>();
        for (Map.Entry<String, FastQueue<BrightFeature>> entry : descriptorsMap.entrySet()) {
            Float match = associateMatcher.matchDescriptors(sampleDescriptors, entry.getValue());
            matchesMap.put(match, entry.getKey());
        }

        for (Map.Entry<Float, String> entry : matchesMap.entrySet()) {
            System.out.println(entry.getKey().toString() + " - " + entry.getValue());
        }


        System.out.println(".... Analysis completed");
    }


    private static void printDescriptors(String filePath, FastQueue<BrightFeature> descriptors) {
        System.out.println("Descriptors for image: " + filePath);
        System.out.println("Count: " + descriptors.getSize());

        for (int i = 0; i < descriptors.getSize(); i++) {
            double[] value = descriptors.get(i).getValue();
            for (double val : value) {
                System.out.print(" " + val);
            }
            System.out.println("");
        }
    }


    private static List<File> getImagesFiles(String folderName) {
        ArrayList<File> filesList = new ArrayList<File>();
        File folder = new File(folderName);
        File[] children = folder.listFiles();
        if (children != null) {
            Collections.addAll(filesList, children);
        }
        return filesList;
    }

}