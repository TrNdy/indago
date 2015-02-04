package com.indago.wekatest;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

public class SimpleNaiveBayesExample {

    public static void main(final String[] args) throws Exception{

         // Declare two numeric attributes
         final Attribute Attribute1 = new Attribute("firstNumeric");
         final Attribute Attribute2 = new Attribute("secondNumeric");

         // Declare a nominal attribute along with its values
         final FastVector fvNominalVal = new FastVector(3);
         fvNominalVal.addElement("blue");
         fvNominalVal.addElement("gray");
         fvNominalVal.addElement("black");
         final Attribute Attribute3 = new Attribute("aNominal", fvNominalVal);

         // Declare the class attribute along with its values
         final FastVector fvClassVal = new FastVector(2);
         fvClassVal.addElement("positive");
         fvClassVal.addElement("negative");
         final Attribute ClassAttribute = new Attribute("theClass", fvClassVal);

         // Declare the feature vector
         final FastVector fvWekaAttributes = new FastVector(4);
         fvWekaAttributes.addElement(Attribute1);
         fvWekaAttributes.addElement(Attribute2);
         fvWekaAttributes.addElement(Attribute3);
         fvWekaAttributes.addElement(ClassAttribute);

         // Create an empty training set
         final Instances isTrainingSet = new Instances("Rel", fvWekaAttributes, 10);

         // Set class index
         isTrainingSet.setClassIndex(3);

         // Create the instance
         final Instance iExample = new Instance(4);
         iExample.setValue((Attribute)fvWekaAttributes.elementAt(0), 1.0);
         iExample.setValue((Attribute)fvWekaAttributes.elementAt(1), 0.5);
         iExample.setValue((Attribute)fvWekaAttributes.elementAt(2), "gray");
         iExample.setValue((Attribute)fvWekaAttributes.elementAt(3), "positive");

         // add the instance
         isTrainingSet.add(iExample);
         final Classifier cModel = new NaiveBayes();
         cModel.buildClassifier(isTrainingSet);

         // Test the model
         final Evaluation eTest = new Evaluation(isTrainingSet);
         eTest.evaluateModel(cModel, isTrainingSet);

         // Print the result à la Weka explorer:
         final String strSummary = eTest.toSummaryString();
         System.out.println(strSummary);

         // Get the confusion matrix
         final double[][] cmMatrix = eTest.confusionMatrix();
         for(int row_i=0; row_i<cmMatrix.length; row_i++){
             for(int col_i=0; col_i<cmMatrix.length; col_i++){
                 System.out.print(cmMatrix[row_i][col_i]);
                 System.out.print("|");
             }
             System.out.println();
         }
    }
}