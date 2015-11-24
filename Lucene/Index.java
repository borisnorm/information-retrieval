import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

/**
 * Class that is used for creating and maintaining index
 * 
 * @author Adib
 */
public class Index {

	// Policy for extracting index terms from text
	private final Analyzer analyzer;

	// Create and Maintain Index
	private IndexWriter writer;

	// Constants
	public static final String CONTENT_FIELD = "content";
	public static final String PATH_FIELD = "path";
	public static final String FILE_NAME_FIELD = "fileName";

	/**
	 * Construct an index with the given file name
	 * 
	 * @param indexFileName the file name of the index
	 */
	public Index(String indexFileName) {
		analyzer = new SimpleAnalyzer(Version.LUCENE_47);
		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_47, analyzer);
		
		try {
			
			FSDirectory dir = FSDirectory.open(new File(indexFileName));
			writer = new IndexWriter(dir, config);
			
		} catch (IOException exception) {
			
			System.out.println("Unable to create an index file");
			exception.printStackTrace();
			
		}
	}
	
	/**
	 * Indexes a file or directory
	 * 
	 * @param fileName the name of a text file or a folder to add to index
	 */
	public void indexFileOrDirectory(String fileName) {
		List<File> files = new ArrayList<File>();

		// gets the list of files in a folder (if user has submitted
		// the name of a folder) or gets a single file name (is user
		// has submitted only the file name)
		addFiles(new File(fileName), files);

		int originalNumDocs = writer.numDocs();
		for (File file : files) {

			try (
				FileReader fileReader = new FileReader(file);
			) {

				Document doc = new Document();
				String contents = new String(Files.readAllBytes(Paths.get(file.getPath())), StandardCharsets.UTF_8);
				
				// add contents of file
				doc.add(new TextField(CONTENT_FIELD, contents, Field.Store.YES));
				doc.add(new StringField(PATH_FIELD, file.getPath(), Field.Store.YES));
				doc.add(new StringField(FILE_NAME_FIELD, file.getName(), Field.Store.YES));

				writer.addDocument(doc);
				System.out.println("Indexed: " + file);

			} catch (Exception e) {

				System.out.println("Could not add: " + file);

			}
		}

		int newNumDocs = writer.numDocs();
		System.out.println("");
		System.out.println("************************");
		System.out.println((newNumDocs - originalNumDocs) + " documents added.");
		System.out.println("************************");
	}

	/**
	 * Add the file to the given List of files if the file is of
	 * type HTML, XML, TXT. If the file is a directory search its 
	 * files and sub-directories
	 * 
	 * @param file The file or directory to add from
	 * @param files The list of files to add to
	 */
	private void addFiles(File file, List<File> files) {
		// File or Directory doesn't exist
		if (!file.exists()) {
			System.out.println(file + " does not exist.");
			return;
		}

		// Provided a directory
		if (file.isDirectory()) {

			for (File f : file.listFiles()) {
				addFiles(f, files);
			}

		} else {

			String fileName = file.getName().toLowerCase();

			// Only index text files
			if (fileName.endsWith(".htm") || fileName.endsWith(".html")
					|| fileName.endsWith(".xml") || fileName.endsWith(".txt")) {
				files.add(file);
			} else {
				System.out.println("Skipped " + fileName);
			}

		}
	}

	/**
	 * Commit changes and close the index file
	 */
	public void closeIndex() {
		try {
			
			writer.close();
			
		} catch (IOException exception) {
			
			System.out.println("Unable to commit changes");
			exception.printStackTrace();
			
		}
		
	}

	/**
	 * Remove HTML tags from the files in the given input Folder 
	 * and dump them to the output folder
	 * NOTE: Input folder must not contain any sub-directory
	 * 
	 * @param inputFolderName Input folder containing only files
	 * @param outputFolderName Output folder to dump the result
	 */
	public static void removeHTMLTags(String inputFolderName, String outputFolderName) {
		
		File inputFolderFile = new File(inputFolderName);
		File outputFolderFile = new File(outputFolderName);
		File[] inputFiles = inputFolderFile.listFiles();
		
		// Create Output Directory
		if (!outputFolderFile.exists()) {
			outputFolderFile.mkdirs();
		}
		
		// For each file in input folder
		for (File inputFile : inputFiles) {
			
			try (
				FileInputStream inputStream = new FileInputStream(inputFile);
				FileOutputStream outputStream = new FileOutputStream(new File(outputFolderName, inputFile.getName()));
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
				BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
			) {
				
				String currentLine;
				while ((currentLine = bufferedReader.readLine()) != null) {
					currentLine = currentLine.replaceAll("\\<[^>]+>", "");
					bufferedWriter.write(currentLine);
					bufferedWriter.newLine();
				}
				
				System.out.println("Removed HTML Tags: " + inputFile.getName());
				
			} catch (IOException exception) {
				
				System.out.println("Unable to remove HTML Tags for " + inputFile.getName());
				exception.printStackTrace();
				
			}
			
		}
	}
}
