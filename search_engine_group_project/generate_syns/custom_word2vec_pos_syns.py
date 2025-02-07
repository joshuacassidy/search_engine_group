import spacy

import random
random.seed(11)
syns = open("generate_syns/words.txt","r") 
file_content = syns.readlines()
token_list = []
count = 0


model = spacy.load("en_core_web_md")

words = {}

for i in file_content:
	token_list.append(i.split(" "))
	for word in model(i[:5000]):
		if word.tag_ == "NN" or word.tag_ == "NNS" or word.tag_ == "NNP" or word.tag_ == "NNPS":
			words["%s" % word] = "%s" % word
	count += 1

from gensim.models.word2vec import Word2Vec

model = Word2Vec(
    token_list,
    workers=3,
    size=300,
    min_count=3,
    window=6,
    sample=1e-3)

syns.close()

custom_syns = open("generate_syns/pos_custom_syns_py.txt","w+") 

stopwords = []
with open("resources/stop_words.txt", "r", encoding="utf-8") as f:
	stopwords = f.read().split("\n")

print(words)

for i in model.wv.vocab.keys():
	sims = str(i)
	if i not in stopwords and not i.isnumeric() and i.strip().rstrip() != "" and i in words:
		similar_words = model.most_similar(positive=[i], topn=3)
		has_syns = False
		for sim in similar_words:
			if sim[0] not in stopwords and not sim[0].isnumeric() and sim[0] in words:
				sims += ":" + str(sim[0]).strip().rstrip()
				has_syns = True
		if has_syns:
			custom_syns.write(sims+"\n")
custom_syns.close()