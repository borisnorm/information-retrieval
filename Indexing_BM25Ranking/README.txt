------------------------------------------------------------------------
------------------------- Compile and Run ------------------------------
------------------------------------------------------------------------
Go to the directory containing these extracted files:
Indexer.java Index.java BM25Algorithm.java BM25.java Printer.java

------------------------- Build inverted index -------------------------
Compile:
javac Indexer.java Index.java Printer.java

Run:
java Indexer <corpus_file> <index_file>
example:
java Indexer tccorpus.txt index.out

------------------------- Rank by BM25 ---------------------------------
Compile:
javac BM25.java BM25Algorithm.java Index.java Printer.java

Run:
java BM25 <index_file> <query_file> <documents_to_retrieve>
example:
java BM25 index.out queries.txt 100

Output:
results.eval --> File containing top documents for each query in 
the given format: query_id Q0 doc_id rank BM25_score system_name 

------------------------------------------------------------------------
----------------------------- Report -----------------------------------
------------------------------------------------------------------------
index_tccorpus.out:
An index file for the given corpus(tccorpus.txt)

results_queries.eval:
Result of running BM25 for given queries(queries.txt) on the index (index_tccorpus.out)
java BM25 index.out queries.txt 100

Index.java:
An index builder/loader class

Indexer.java:
Create and build index for the given input corpus

BM25.java:
Load index and rank documents for given queries

BM25Algorithm.java:
Class used for calculating the BM25 for a given query on the given inverted index

Printer.java:
Helper class to print to file
Use methods in this class to print the desired results

Description:
For building index, Index class has buildIndex method that reads input corpus from the file. 
This method loads the index which is a Map from Token to a map from document id to 
term frequency i.e. each key-value pair has key as token name (word) and value is 
a map to a key-value pair that has key as document id and value as term frequency in 
that document. This method reads each line and parses its tokens depending whether it 
indicates a start of document or tokens of an already started document and saves in the 
index accordingly. To write the index to a file we use printIndex method of the Printer 
class that writes this index to a file.

For running BM25, Index class has loadIndex method that reads index from the file.
This method loads the index which is a Map from Token to a map from document id to 
term frequency i.e. each key-value pair has key as token name (word) and value is 
a map to a key-value pair that has key as document id and value as term frequency in 
that document. Also, it creates a map from document id to number of tokens in that 
document. Now, BM25Algorithm is run for each query and its result is printed to a file 
"results.eval" using printSortedScore method of Printer class. Total size of corpus 
and average document length is calculated at BM25Algorithm class initialization time.