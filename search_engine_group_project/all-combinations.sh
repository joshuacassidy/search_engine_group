mvn package && java -jar target/search_engine_group_project-1.0-SNAPSHOT.jar -analyzer standard -similarity classic -output_location output/output-standard-classic.txt
mvn package && java -jar target/search_engine_group_project-1.0-SNAPSHOT.jar -analyzer standard -similarity boolean -output_location output/output-standard-boolean.txt
mvn package && java -jar target/search_engine_group_project-1.0-SNAPSHOT.jar -analyzer standard -similarity lmd -output_location output/output-standard-lmd.txt
mvn package && java -jar target/search_engine_group_project-1.0-SNAPSHOT.jar -analyzer standard -similarity dfis -output_location output/output-standard-dfis.txt
mvn package && java -jar target/search_engine_group_project-1.0-SNAPSHOT.jar -analyzer standard -similarity bm25 -output_location output/output-standard-bm25.txt

mvn package && java -jar target/search_engine_group_project-1.0-SNAPSHOT.jar -analyzer simple -similarity classic -output_location output/output-simple-classic.txt
mvn package && java -jar target/search_engine_group_project-1.0-SNAPSHOT.jar -analyzer simple -similarity boolean -output_location output/output-simple-boolean.txt
mvn package && java -jar target/search_engine_group_project-1.0-SNAPSHOT.jar -analyzer simple -similarity lmd -output_location output/output-simple-lmd.txt
mvn package && java -jar target/search_engine_group_project-1.0-SNAPSHOT.jar -analyzer simple -similarity dfis -output_location output/output-simple-dfis.txt
mvn package && java -jar target/search_engine_group_project-1.0-SNAPSHOT.jar -analyzer simple -similarity bm25 -output_location output/output-simple-bm25.txt

mvn package && java -jar target/search_engine_group_project-1.0-SNAPSHOT.jar -analyzer english -similarity classic -output_location output/output-english-classic.txt
mvn package && java -jar target/search_engine_group_project-1.0-SNAPSHOT.jar -analyzer english -similarity boolean -output_location output/output-english-boolean.txt
mvn package && java -jar target/search_engine_group_project-1.0-SNAPSHOT.jar -analyzer english -similarity lmd -output_location output/output-english-lmd.txt
mvn package && java -jar target/search_engine_group_project-1.0-SNAPSHOT.jar -analyzer english -similarity dfis -output_location output/output-english-dfis.txt
mvn package && java -jar target/search_engine_group_project-1.0-SNAPSHOT.jar -analyzer english -similarity bm25 -output_location output/output-english-bm25.txt

mvn package && java -jar target/search_engine_group_project-1.0-SNAPSHOT.jar -analyzer white-space -similarity classic -output_location output/output-white-space-classic.txt
mvn package && java -jar target/search_engine_group_project-1.0-SNAPSHOT.jar -analyzer white-space -similarity boolean -output_location output/output-white-space-boolean.txt
mvn package && java -jar target/search_engine_group_project-1.0-SNAPSHOT.jar -analyzer white-space -similarity lmd -output_location output/output-white-space-lmd.txt
mvn package && java -jar target/search_engine_group_project-1.0-SNAPSHOT.jar -analyzer white-space -similarity dfis -output_location output/output-white-space-dfis.txt
mvn package && java -jar target/search_engine_group_project-1.0-SNAPSHOT.jar -analyzer white-space -similarity bm25 -output_location output/output-white-space-bm25.txt

mvn package && java -jar target/search_engine_group_project-1.0-SNAPSHOT.jar -analyzer stop -similarity classic -output_location output/output-stop-classic.txt
mvn package && java -jar target/search_engine_group_project-1.0-SNAPSHOT.jar -analyzer stop -similarity boolean -output_location output/output-stop-boolean.txt
mvn package && java -jar target/search_engine_group_project-1.0-SNAPSHOT.jar -analyzer stop -similarity lmd -output_location output/output-stop-lmd.txt
mvn package && java -jar target/search_engine_group_project-1.0-SNAPSHOT.jar -analyzer stop -similarity dfis -output_location output/output-stop-dfis.txt
mvn package && java -jar target/search_engine_group_project-1.0-SNAPSHOT.jar -analyzer stop -similarity bm25 -output_location output/output-stop-bm25.txt

mvn package && java -jar target/search_engine_group_project-1.0-SNAPSHOT.jar -analyzer custom -similarity classic -output_location output/output-custom-classic.txt
mvn package && java -jar target/search_engine_group_project-1.0-SNAPSHOT.jar -analyzer custom -similarity boolean -output_location output/output-custom-boolean.txt
mvn package && java -jar target/search_engine_group_project-1.0-SNAPSHOT.jar -analyzer custom -similarity lmd -output_location output/output-custom-lmd.txt
mvn package && java -jar target/search_engine_group_project-1.0-SNAPSHOT.jar -analyzer custom -similarity dfis -output_location output/output-custom-dfis.txt
mvn package && java -jar target/search_engine_group_project-1.0-SNAPSHOT.jar -analyzer custom -similarity bm25 -output_location output/output-custom-bm25.txt