/**
 *
 */
package com.indago.wekatest;

import java.io.FileOutputStream;
import java.io.PrintStream;

import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Normalize;
import weka.filters.unsupervised.attribute.ReplaceMissingValues;


/**
 * @author jug
 */
public class SimpleDecisionForestExample {

	public static FileOutputStream Output;
	public static PrintStream file;

	public static void main( final String[] args ) throws Exception {
		// load training data
		final weka.core.Instances training_data = new weka.core.Instances(
						new java.io.FileReader( "src/main/resources/synthetic/0001_z63/FeatureExampleOnRealSegments_traindata.arff" ) );

		//load test data
		final weka.core.Instances test_data = new weka.core.Instances(
						new java.io.FileReader( "src/main/resources/synthetic/0001_z63/FeatureExampleOnRealSegments_testdata.arff" ) );

		//Clean up training data
		final ReplaceMissingValues replace = new ReplaceMissingValues();
		replace.setInputFormat( training_data );
		final Instances training_data_filter1 = Filter.useFilter( training_data, replace );

		//Normalize training data
		final Normalize norm = new Normalize();
		norm.setInputFormat( training_data_filter1 );
		final Instances processed_training_data = Filter.useFilter( training_data_filter1, norm );

		//Set class attribute for pre-processed training data
		processed_training_data.setClassIndex( processed_training_data.numAttributes() - 1 );

		//output to file
		Output = new FileOutputStream( "src/main/resources/synthetic/0001_z63/test.txt" );
		file = new PrintStream( Output );

		//build classifier
		final RandomForest tree = new RandomForest();
		tree.buildClassifier( processed_training_data );

		//Clean up test data
		replace.setInputFormat( test_data );
		final Instances test_data_filter1 = Filter.useFilter( test_data, replace );

		//Normalize test data
		norm.setInputFormat( training_data_filter1 );
		final Instances processed_test_data = Filter.useFilter( test_data_filter1, norm );

		//Set class attribute for pre-processed training data
		processed_test_data.setClassIndex( processed_test_data.numAttributes() - 1 );

		//int num_correct=0;
		for ( int i = 0; i < processed_test_data.numInstances(); i++ ) {
			final weka.core.Instance currentInst = processed_test_data.instance( i );
			final int predictedClass = ( int ) tree.classifyInstance( currentInst );
			System.out.println( predictedClass );
			file.println( "O" + predictedClass );
		}
	}
}