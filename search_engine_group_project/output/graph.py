import matplotlib
matplotlib.use("TkAgg")
import matplotlib.pyplot as plt
import pandas as pd
import os

def percision_recall_curve(plot_name, include_files, visualise_scoring_approach):

    plt.rc('font', size=14)
    plt.rcParams['figure.constrained_layout.use'] = True
    fig = plt.gcf()
    fig.canvas.set_window_title(plot_name)
    fig.set_size_inches(12, 5.5)
    plt.xlabel('Recall')
    plt.ylabel('Precision')
    plt.title('Recall-Precision Curve')


    for filename in os.listdir("trec_eval_outputs/"):
        if include_files in filename:
            f = open("trec_eval_outputs/%s" % filename, "r")
            data = f.read().strip()
            f.close()
            if not visualise_scoring_approach:
                analyzer = filename.split("_")[-1].split(".")[0]
            else: 
                analyzer = filename.split("_")[0]
            
            recall_keys = [
                "iprec_at_recall_0.00",
                "iprec_at_recall_0.10",
                "iprec_at_recall_0.20",
                "iprec_at_recall_0.30",
                "iprec_at_recall_0.40",
                "iprec_at_recall_0.50",
                "iprec_at_recall_0.60",
                "iprec_at_recall_0.70",
                "iprec_at_recall_0.80",
                "iprec_at_recall_0.90",
                "iprec_at_recall_1.00"
            ]

            recall = []
            percision = []
            for i in data.split("\n"):
                record = i.split("\t")
                if i.split(" ")[0].strip() in recall_keys:
                    percision.append(float(record[-1]))
                    recall.append(float(i.split(" ")[0].split("iprec_at_recall_")[1].strip()))

            plt.plot(percision, recall, label = analyzer)
            
    plt.legend(bbox_to_anchor=(1.02,1), loc="upper left")
    plt.savefig('%s.png' % plot_name)
    plt.clf()

percision_recall_curve('Recall-Precision Curve for Different Analyzers', "bm25", True)
percision_recall_curve('Recall-Precision Curve for Different Scoring Approaches', "Custom", False)
