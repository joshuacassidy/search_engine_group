# Search Engine
In this project we have a search engine that uses Lucene.
## Building and Executing

You can compile and run the java code from the 'search_engine_group_project' folder using the command:
```sh
mvn package && java -jar target/search_engine_group_project-1.0-SNAPSHOT.jar
```

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
