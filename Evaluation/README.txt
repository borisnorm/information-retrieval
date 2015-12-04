------------------------------------------------------------------------
------------------------- Compile and Run ------------------------------
------------------------------------------------------------------------
Go to the directory containing these extracted files:
Evaluate.java Measure.java Printer.java

------------------------------ Compile ---------------------------------

javac -cp "lib/*" Evaluate.java Measure.java Printer.java

------------------------------ Run -------------------------------------

Windows:
java -cp "lib/*;." Evaluate

Linux:
java -cp ".:lib/*" Evaluate

------------------------------ Output ---------------------------------

P@20 for Query: portable operating systems(12) is 0.2
P@20 for Query: code optimization for space efficiency(13) is 0.2
P@20 for Query: parallel algorithms(19) is 0.35
Mean Average Precision (MAP): 0.4332617122206565

All tables contains following columns and values:
1) Rank 
2) Document ID
3) Document Score 
4) Relevance Level 
5) Precision 
6) Recall 
7) NDCG

portable_operating_system_table.txt:
Table of retrieval effectiveness for portable operating systems(12)

code_optimization_for_space_efficiency_table.txt:
Table of retrieval effectiveness for code optimization for space efficiency(13)

parallel_algorithms_table.txt:
Table of retrieval effectiveness for parallel algorithms(19)

------------------------------------------------------------------------
----------------------------- Report -----------------------------------
------------------------------------------------------------------------

Evaluate.java:
Main class to run HW5

Measure.java:
Class used to compute Effectiveness Metrics

Printer.java:
Helper class to print to file
Use methods in this class to print the desired results

Evaluate class is the starting point for this HomeWork. All the required libraries 
are present in the lib/ folder. CACM relevance judgement file is read to get 
the document relevance for the given queries using getRelevantJudgement method. 

Each search result for the given queries i.e. 12, 13 and 19 are read using 
getDocument method to get the document rank, fileName and ranking score. Also, 
the boolean relevance for each document is set based on relevance judgement. 

Measure class is used to compute Effectiveness Metrics. The ratio of number of 
relevant documents that the system succeeded to retrieve for a given query, to 
the number of retrieved documents i.e. precision is obtained using getPrecision 
method. The ratio of number of relevant documents that the system succeeded to 
retrieve for a given query, to the number of the relevant documents for that query 
is obtained using getRecall method. The average of precision values where a 
relevant document was retrieved is calculated using getAveragePrecision method. 
The total accumulated for each document rank (normalized) by comparing it to 
perfect ranking is calculated usign getNormalizedDiscountedCumulativeGain method.