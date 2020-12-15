import os
import re
from bs4 import BeautifulSoup

def run():
	syns = open("words.txt","w+") 
	for root, dirs, files in os.walk("./search_engine_group_project/datasets"):
		print(root)
		for file in files:
			if (
				file.endswith(".dtd") or 
				file.endswith(".txt") or 
				file == "readchg" or 
				file == "readmefr" or 
				file == "ftdtd" or 
				file == "fr94dtd" or
				file == ".DS_Store" or
				file == "group_member_dataset_info" or
				file == "readmeft" or
				file == "" or 
				file == "readfrcg"
			
			):
				continue

			if (
				"fbis" in root or
				"fr94" in root or
				"ft" in root or
				"latimes" in root
			):
				file_location = os.path.join(root, file)
				if os.path.isfile(file_location):
					with open(file_location, 'r', encoding = "ISO-8859-1") as f:
						contents = f.read()
						soup = BeautifulSoup(contents, 'html.parser')
						if "fbis" in root:
							bodys = soup.find_all("text")
						elif "fr94" in root:
							bodys = soup.find_all("text")
						elif "ft" in root:
							bodys = soup.find_all("text")
						else:
							bodys = soup.find_all("text")
						for i in range(len(bodys)):
							body = re.sub(' +', ' ', re.sub(r'[^A-Za-z0-9 ]+', '', bodys[i].get_text()).lower())
							syns.write(body + "\n")

	syns.close()

run()
