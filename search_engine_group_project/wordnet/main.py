token_list = [
  ['to', 'provide', 'early', 'intervention/early', 'childhood', 'special',
   'education', 'services', 'to', 'eligible', 'children', 'and', 'their',
   'families'],
  ['essential', 'job', 'functions'],
  ['participate', 'as', 'a', 'transdisciplinary', 'team', 'member', 'to',
   'complete', 'educational', 'assessments', 'for']
]

from gensim.models.word2vec import Word2Vec

model = Word2Vec(
    token_list,
    workers=3,
    size=300,
    min_count=3,
    window=6,
    sample=1e-3)

model.init_sims(replace=True)
model_name = "model"
model.save(model_name)

print(model.most_similar('job'))


from gensim.models.word2vec import Word2Vec
model_name = "model"
model = Word2Vec.load(model_name)
print(model.most_similar('a'))


# import os
# from bs4 import BeautifulSoup
# def run():
#     for root, dirs, files in os.walk("./datasets"):
#         print(root)
#         for file in files:
#             if (
#                 file.endswith(".dtd") or 
#                 file.endswith(".txt") or 
#                 file == "readchg" or 
#                 file == "readmefr" or 
#                 file == "ftdtd" or 
#                 file == "fr94dtd" or
#                 file == ".DS_Store" or
#                 file == "group_member_dataset_info" or
#                 file == "readmeft" or
#                 file == "" or 
#                 file == "readfrcg"
            
#             ):
#                 continue

#             if (
#                 "fbis" in root or
#                 "fr94" in root or
#                 "ft" in root or
#                 "latimes" in root
#             ):
#                 file_location = os.path.join(root, file)
#                 if os.path.isfile(file_location):
#                     with open(file_location, 'r') as f:
#                         contents = f.read()
#                         soup = BeautifulSoup(contents, 'html.parser')
#                     return

# run()