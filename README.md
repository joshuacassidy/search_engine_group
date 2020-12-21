# Search Engine
In this project we have a search engine that uses Lucene.
## Building and Executing

You can compile and run (for the best results) the java code from the 'search_engine_group_project' folder using the command:
```sh
mvn package && java -jar target/search_engine_group_project-1.0-SNAPSHOT.jar -use_word_frequencies -create_index
```

### CLI Parameters

We have 4 cli parameters that cam be used:
- ```-analizer``` this parameter let you choose which analyzer you want to use and can have the following values: 
```english```, ```standard```, ```simple```, ```white-space```, ```stop```, ```custom```.
The default values is ```custom```.
- ```-similarity``` this parameter let you choose which similarity you want to use and can have the following values: 
```axiomatic```, ```bm25```, ```boolean```, ```classic```, ```dfis```, ```lmd```, ```lmjm```.
The default values is ```bm25```.
- ```-wordnet``` this parameter let you choose which wordnet you want to use (only applicable to custom analyzers, otherwise this parameter is ignored)
 and can have the following values: ```princeton```, ```google```, ```document```, ```custom```.
The default values is ```custom```.
- ```-output_location``` this parameter let you choose the output directory and file name of the result of the scoring, the default is ```"output/output.txt"```.

We also have 3 flags:
- ```-create_index``` this flag will tell the program to create the index, it will not create it by default.
- ```-use_word_frequencies``` this flag will tell the program to also use the word frequencies when scoring.
- ```-doc_2_vec``` this flag will tell the program to also use the doc2vec approach when scoring.

### Using Document and Google Wordnets

To use the ```google``` or ```document``` wordnet, you will need to run some commands:

The first one is needed to extract the words from all the documents. From the search_engine_group_project folder, run:
```python wordnet/store_words.py```

Once that is done, you will be able to get the document wordnet in this way (same folder as before):
```python wordnet/main.py```

And the google wordnet in this way (same folder as before):
```python wordnet/google_wordnet.py```

## Evaluating

You will be able to evaluate the results using trec_eval.
from the main folder, type these commands to go into the right directory and compile trec_eval:
```sh
cd trec_eval-9.0.7
make
```

To evaluate the software using trec eval, use this code:
```sh
./trec_eval <QRel_file> ../search_engine_group_project/output/output.txt
```


## Project Dependencies:
1. Java (Version 8 or greater)
2. Maven (Version 3.6.0)
3. Bash (Version 3.2)

## Notes
1. The output.txt contains the output of the search engine
2. Remove the contents of the index folder before running as the hosted instance is limited on space
3. The output.txt uses the topic number in the first column, and the document number in the third column
