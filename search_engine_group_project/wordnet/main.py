
syns = open("words.txt","r") 
file_content = syns.readlines()
token_list = []
count = 0

for i in file_content:
	token_list.append(i.split(" "))
	if count > 1000:
		break
	count += 1

from gensim.models.word2vec import Word2Vec

model = Word2Vec(
    token_list,
    workers=3,
    size=300,
    min_count=3,
    window=6,
    sample=1e-3)

# model_name = "custom_model.wn"
# model.save(model_name)

syns.close()

custom_syns = open("custom_syns.txt","w+") 

stopwords = []
with open("/Users/owner/Desktop/search_engine_group/search_engine_group_project/resources/stop_words.txt", "r") as f:
	stopwords = f.read().split("\n")


for i in model.wv.vocab.keys():
	sims = str(i)
	if i not in stopwords and not i.isnumeric() and i.strip().rstrip() != "":
		similar_words = model.most_similar(positive=[i], topn=3, restrict_vocab=300)
		has_syns = False
		for sim in similar_words:
			if sim[0] not in stopwords and not sim[0].isnumeric():
				sims += "," + str(sim[0]).strip().rstrip()
				has_syns = True
		if has_syns:
			custom_syns.write(sims+"\n")
custom_syns.close()







