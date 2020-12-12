from flask import Flask, Response, request,render_template
import json
from gensim.models import KeyedVectors

app = Flask(__name__)
import math
model = KeyedVectors.load_word2vec_format('word2vec/word2vec.txt', binary=False)

@app.route('/', methods=['GET'])
def get_word():
    try:
        word = request.args.get('word')
        return Response(json.dumps(model.most_similar(positive=[word])), mimetype='application/json', status='200')
    except:
        return Response(json.dumps([]), mimetype='application/json', status='400')

if __name__ == '__main__':
    app.run(debug=True)
