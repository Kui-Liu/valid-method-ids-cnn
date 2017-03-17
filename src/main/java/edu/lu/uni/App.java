package edu.lu.uni;

import java.io.File;
import java.io.IOException;
import java.util.List;

import edu.lu.uni.data.preparing.Standardization;
import edu.lu.uni.data.preparing.ZeroAppender;
import edu.lu.uni.deeplearning.extractor.FeatureExtractor;
import edu.lu.uni.util.FileHelper;

public class App {

	public static void main(String[] args) {
		App example = new App();
		try {
			// data preprocessing
			example.appendZero();
			example.standardizeData();
			// feature extracting: deep learning with the CNN algorithm
			example.extractFeatureWithCNN();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Append zero to the integer vectors of which sizes are less than the max size,
	 * to make all the lengths of vectors consistent and equal to the max size.
	 * @throws IOException
	 */
	public void appendZero() throws IOException {
		String inputFilePath = Configuration.ENCODED_METHOD_BODY_FILE_PATH;
		String inputFileExtension = Configuration.STRING_DATA_FILE_EXTENSION;
		List<File> integerVectorsFiles = FileHelper.getAllFiles(inputFilePath, inputFileExtension);

		String outputFileExtension = Configuration.DIGITAL_DATA_FILE_EXTENSION;
		String outputFilePath = Configuration.DATA_APPZENDED_ZERO;
		// Clear existing output data generated at the last time.
		FileHelper.deleteDirectory(outputFilePath);
		
		ZeroAppender appender = new ZeroAppender();
		for (File file : integerVectorsFiles) {
			appender.appendZeroForVectors(file, inputFilePath, outputFilePath, inputFileExtension, outputFileExtension);
		}
	}

	/**
	 * Standardize the data of integer vectors.
	 */
	public void standardizeData() {
		String inputFilePath = Configuration.ENCODED_METHOD_BODY_FILE_PATH;
		String fileExtension = Configuration.STRING_DATA_FILE_EXTENSION;
		List<File> integerVectorsFiles = FileHelper.getAllFiles(inputFilePath, fileExtension);

		String outputFileExtension = Configuration.DIGITAL_DATA_FILE_EXTENSION;
		String outputFilePath = Configuration.DATA_STANDARDIZED;
		// Clear existing output data generated at the last time.
		FileHelper.deleteDirectory(outputFilePath);
		
		for (File file : integerVectorsFiles) {
			Standardization s = new Standardization(file, outputFilePath, fileExtension, outputFileExtension);
			s.standardize();
		}
	}

	/**
	 * Extract features of method bodies by deep learning with the CNN algorithm.
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void extractFeatureWithCNN() throws IOException, InterruptedException {
		String fileExtension = Configuration.DIGITAL_DATA_FILE_EXTENSION;
		List<File> inputFiles = FileHelper.getAllFiles(Configuration.DATA_APPZENDED_ZERO, fileExtension); // normal data.
		inputFiles.addAll(FileHelper.getAllFiles(Configuration.DATA_STANDARDIZED, fileExtension));        // standardized data.
		
		String outputPath = Configuration.DATA_EXTRACTED_FEATURE;
		// Clear existing output data generated at the last time.
		FileHelper.deleteDirectory(outputPath);
		
		for (File inputFile : inputFiles) {
			String fileName = inputFile.getName();
			int sizeOfVector = Integer.parseInt(fileName.substring(fileName.lastIndexOf("=") + 1, fileName.lastIndexOf(fileExtension)));
			int batchSize = 1000;
			int sizeOfFeatureVector = 100;
			
			FeatureExtractor extractor = new FeatureExtractor(inputFile, sizeOfVector, batchSize, sizeOfFeatureVector);
			extractor.setOutputPath(outputPath);
			
//			extractor.setNumberOfEpochs(1);
//			extractor.setSeed(123);
//			extractor.setNumOfOutOfLayer1(20);
//			extractor.setNumOfOutOfLayer2(50);
			
			extractor.extracteFeaturesWithCNN(); 
		}
	}

}