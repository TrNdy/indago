/**
 *
 */
package com.indago.wekatest;

import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintStream;

import weka.classifiers.trees.RandomForest;
import weka.core.Instances;


/**
 * @author jug
 */
public class SimpleDecisionForestExample {

	public static FileOutputStream Output;
	public static PrintStream file;

	public static void main( final String[] args ) throws Exception {
		// load training data
		final Instances training_data =
				new Instances(
						new FileReader( "src/main/resources/synthetic/0001_z63/FeatureExampleOnRealSegments_traindata.arff" ) );

		//load test data
		final Instances test_data =
				new Instances(
						new FileReader( "src/main/resources/synthetic/0001_z63/FeatureExampleOnRealSegments_testdata.arff" ) );

		//Clean up training data
//		final ReplaceMissingValues replace = new ReplaceMissingValues();
//		replace.setInputFormat( training_data );
//		final Instances training_data_filter1 = Filter.useFilter( training_data, replace );

		//Normalize training data
//		final Normalize norm = new Normalize();
//		norm.setInputFormat( training_data_filter1 );
//		final Instances processed_training_data = Filter.useFilter( training_data_filter1, norm );

		//Set class attribute for pre-processed training data
//		processed_training_data.setClassIndex( processed_training_data.numAttributes() - 1 );

		training_data.setClassIndex( 0 );

		//output to file
		Output = new FileOutputStream( "src/main/resources/synthetic/0001_z63/test.txt" );
		file = new PrintStream( Output );

		//build classifier
		final RandomForest tree = new RandomForest();
		tree.buildClassifier( training_data );

		//Clean up test data
//		replace.setInputFormat( test_data );
//		final Instances test_data_filter1 = Filter.useFilter( test_data, replace );

		//Normalize test data
//		norm.setInputFormat( training_data_filter1 );
//		final Instances processed_test_data = Filter.useFilter( test_data_filter1, norm );

		//Set class attribute for pre-processed training data
//		processed_test_data.setClassIndex( processed_test_data.numAttributes() - 1 );

		test_data.setClassIndex( 0 );

		int num_ones = 0;
		for ( int i = 0; i < test_data.numInstances(); i++ ) {
			final weka.core.Instance currentInst = test_data.instance( i );
			final int predictedClass = ( int ) tree.classifyInstance( currentInst );

			final double[] dist = tree.distributionForInstance( currentInst );
			System.out.println( String.format(
					">> distribution is (%.2f,%.2f)",
					dist[ 0 ],
					dist[ 1 ] ) );

			if ( predictedClass == 1 ) {
				num_ones++;
				System.out.println( String.format( "%d\t%d", predictedClass, num_ones ) );
				file.println( String.format( "%d\t%d", predictedClass, num_ones ) );
			}

		}
	}
}