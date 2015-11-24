------------------------------------------------------------------------
------------------------- Compile and Run ------------------------------
------------------------------------------------------------------------
Go to the directory containing these extracted files:
Run.java Index.java Retrieve.java PlotGraph.java Printer.java

------------------------------ Compile ---------------------------------

javac -cp "lib/*" Index.java Run.java Printer.java PlotGraph.java Retrieve.java

------------------------------ Run -------------------------------------

Windows:
java -cp "lib/*;." Run

Linux:
java -cp ".:lib/*" Run

------------------------------ Output ----------------------------------

cacm_no_tags Folder : Same as cacm corpus files but with all HTML tags removed from the files

index Folder : Folder to maintain index files

term_frequency.txt : Sorted (by frequency) list of (term, term_freq) pairs

ZipfianGraph.png : The graph of Rank -> Probability for the given corpus

results.txt : Result of running below queries on the index to retreive top 100 results
Queries:
portable operating systems
code optimization for space efficiency
parallel algorithms
parallel processor in information retrieval

compare.html/compare.txt : Comparison of total number of matches in HW4 to that of queries in HW3

------------------------------------------------------------------------
----------------------------- Report -----------------------------------
------------------------------------------------------------------------

results.txt : File containing top documents for each query in 
the given format: Rank FileName Score TextSnippet(Max 200 chars)

Run.java :
Main Class to run HW4

Index.java :
Class that is used for creating and maintaining index

Retrieve.java :
Class used to retrieve/search information from an index

PlotGraph.java :
Helper class to plot graph in a JFrame

Printer.java :
Helper class to print to file
Use methods in this class to print the desired results

1) Run class is the starting point for this HomeWork. All the required 
libraries are present in the lucene folder. Indexing the raw CACM corpus 
begins with removing all the HTML tags from the corpus. removeHTMLTags() 
method of Index class is used for this purpose. It takes an input folder 
as input and generates an ouutput folder with the given name having files 
with removed HTML tags. Next, step is to index this file(s) into index 
using Lucene. indexFileOrDirectory() method of Index class is used to 
achieve this. Here, the file content, path and the file name are indexed. 

2) A list of unique_term and frequency_pair is built using getUniqueTermFrequency() 
method of Retrieve class(sort by frequency). Printer class is used to print this 
data to a file using printTermFrequencyPair() method.

3) Zifian curve is plotted using PlotGraph's method plotGraph(). The data for this 
rank and probability is obtained using getRankProbability() method of Retreive class. 
This takes in a term-frequency pair which is obtained from step - 2

4) Query searching is done using searchQuery() method of Retrieve class. Top documents 
along with the text snippet's and score are saved to file using printQueryResult() 
method of Printer class.

5) Table comparing results obtained by HW3 and HW4 is given in compare.html file.
