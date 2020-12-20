from flask import Flask, Response, request,render_template
from wordfreq import word_frequency
import json
import math
import numpy as np
from gensim.models import KeyedVectors
from gensim.models.doc2vec import Doc2Vec

from scipy import spatial

doc2vec_model = Doc2Vec.load('doc2vec/doc2vec.bin')

app = Flask(__name__)

def build_doc2vec_vectors(query_text, document_texts):
    query_vector = doc2vec_model.infer_vector(query_text.split())
    document_vectors = []
    
    for i in range(len(document_texts)):
        document_vectors.append(doc2vec_model.infer_vector(document_texts[i]['doc_text'].split()))

    return query_vector, document_vectors

def get_doc2vec_similarity(query_vector, document_vector):
    return 1 - spatial.distance.cosine(query_vector, document_vector)

@app.route('/doc2vec', methods=['POST'])
def get_doc2vec_vectors_similarities():
    data = request.json
    
    query_text = data['query_text']
    document_texts = data['document_texts']
    
    query_vector, document_vectors = build_doc2vec_vectors(query_text, document_texts)
    
    values = {'values': []}
    for i in range(len(document_vectors)):
        similarity = get_doc2vec_similarity(query_vector, document_vectors[i])
        values['values'].append(similarity)
    
    result = json.dumps(values)
    
    return Response(result, mimetype='application/json', status='200')

def get_zipf_similarity(query_text, document_text):
    dictionary = dict()
    result = 0
    
    for word in document_text.split():
        if word not in dictionary:
            dictionary[word] = 0
        dictionary[word] += 1
        
    for word in query_text.split():
        if word_frequency(word, 'en') == 0:
            continue
        word_freq_score = 1/word_frequency(word, 'en')
        
        if word in dictionary:
            result += word_freq_score * dictionary[word]
    
    return result
    
    
@app.route('/zipf', methods=['POST'])
def get_zipf_similarities():
    data = request.json
    
    query_text = data['query_text']
    document_texts = data['document_texts']
    
    values = {'values': []}
    
    for i in range(len(document_texts)):
        similarity = get_zipf_similarity(query_text, document_texts[i]['doc_text'])
        values['values'].append(similarity)
    
    result = json.dumps(values)
    
    return Response(result, mimetype='application/json', status='200')

if __name__ == '__main__':
    app.run(debug=True)
