# Search Engine
In this project we have a search engine that uses Lucene.
## Building and Executing

Before building and executing, be sure to download the datasets here: https://drive.google.com/file/d/17KpMCaE34eLvdiTINqj1lmxSBSu8BtDP/view and extract it.
Put the "fbis", "ft", "fr94", and "latimes" folders inside the datasets folder in the search_engine_group_project folder.

You can compile and run (for the best results) the java code from the 'search_engine_group_project' folder using the command:
```sh
mvn package && java -jar target/search_engine_group_project-1.0-SNAPSHOT.jar -use_word_frequencies -create_index -doc_2_vec
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
 and can have the following values: ```princeton```, ```google```, ```document```, ```document_POS```, ```custom```.
The default values is ```custom```.
- ```-output_location``` this parameter let you choose the output directory and file name of the result of the scoring, the default is ```"output/output.txt"```.

We also have 3 flags:
- ```-create_index``` this flag will tell the program to create the index, it will not create it by default.
- ```-use_word_frequencies``` this flag will tell the program to also use the word frequencies when scoring.
- ```-doc_2_vec``` this flag will tell the program to also use the doc2vec approach when scoring.

### Using Document and Google Wordnets

To use the ```google``` or ```document``` wordnet, you will need to run some commands:

The first one is needed to extract the words from all the documents. From the search_engine_group_project folder, run:
```python generate_syns/store_words.py```

Once that is done, you will be able to get the document POS wordnet in this way (same folder as before):
```python generate_syns/custom_word2vec_pos_syns.py```

Once that is done, you will be able to get the document wordnet in this way (same folder as before):
```python generate_syns/custom_word2vec_syns.py```

And the google wordnet in this way (same folder as before):
```python generate_syns/google_word2vec_syns.py```
Also, to run the google wordnet, you will need to download the pretrained word2vec model here: https://code.google.com/archive/p/word2vec/
Once donwloaded, extract the content into the generate_syns folder, that you can find in the search_engine_group_project folder.

### Using word frequencies and doc2vec

To use word frequencies and doc2vec to score, you will need to run the python server in your machine.

### Python Server

To enable the python server, you will need to use the command ```python3 flask-server/app.py```.
In order to run this command though, you will need to download the pretrained doc2vec model here: https://ibm.ent.box.com/s/3f160t4xpuya9an935k84ig465gvymm2
Once downloaded, put the 3 doc2vec.bin files into the doc2vec folder.

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
