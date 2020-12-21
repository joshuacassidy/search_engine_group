
# syns = open("words.txt","r") 
# file_content = syns.readlines()
# token_list = []
# count = 0

# for i in file_content:
# 	token_list.append(i.split(" "))
# 	if count > 30000:
# 		break
# 	count += 1

# from gensim.models.word2vec import Word2Vec

# model = Word2Vec(
#     token_list,
#     workers=3,
#     size=300,
#     min_count=3,
#     window=6,
#     sample=1e-3)

# syns.close()

from gensim.models import KeyedVectors
model = KeyedVectors.load_word2vec_format('wordnet/word2vec.bin', binary=True, limit=10000000)

google_syns = open("wordnet/google_syns.txt","w+", encoding="utf-8") 

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
				# if has_syns:
				sims += ":"
				sims += str(sim[0]).strip().rstrip()
				has_syns = True
		if has_syns:
			google_syns.write(sims.lower()+"\n")
google_syns.close()














# from gensim.models import KeyedVectors


# model = KeyedVectors.load_word2vec_format("search_engine_group_project/word2vec/word2vec.txt", binary=False, limit=1000000)
# syns = open("syns.txt","w+") 

# for i in model.wv.vocab.keys():
#   sims = str(i)
#   for sim in model.most_similar(i):
#     sims += "," + str(sim[0])
#   syns.write(sims+"\n")
# syns.close()
