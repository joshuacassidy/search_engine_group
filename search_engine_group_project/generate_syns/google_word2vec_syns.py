from gensim.models import KeyedVectors
model = KeyedVectors.load_word2vec_format('generate_syns/word2vec.bin', binary=True, limit=10000000)

google_syns = open("generate_syns/google_syns.txt","w+", encoding="utf-8") 

stopwords = []
with open("resources/stop_words.txt", "r", encoding="utf-8") as f:
	stopwords = f.read().split("\n")

for i in model.wv.vocab.keys():
	sims = str(i)
	if i not in stopwords and not i.isnumeric() and i.strip().rstrip() != "":
		similar_words = model.most_similar(positive=[i], topn=3, restrict_vocab=300)
		has_syns = False
		for sim in similar_words:
			if sim[0] not in stopwords and not sim[0].isnumeric():
				sims += ":"
				sims += str(sim[0]).strip().rstrip()
				has_syns = True
		if has_syns:
			google_syns.write(sims.lower()+"\n")
google_syns.close()

