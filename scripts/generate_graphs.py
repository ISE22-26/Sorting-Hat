import json
import os
import matplotlib.pyplot as plt

script_path = os.path.dirname(__file__)
competition_root = os.path.join(script_path, "..")
submission_dir = os.path.join(competition_root, "submissions")
results_dir = os.path.join(competition_root, "results")


def load_results(results_file):
    """
    Load results from the results.json file, excluding any with errors.
    """
    with open(results_file, "r") as file:
        data = json.load(file)
    clean_data = [entry for entry in data if "error" not in entry]
    return clean_data


def prepare_plot_data(results):
    """
    Organize results by dataset for plotting.
    """
    plot_data = {}
    for entry in results:
        dataset = entry["dataset"]
        if dataset not in plot_data:
            plot_data[dataset] = {}
        if entry["algorithm"] not in plot_data[dataset]:
            plot_data[dataset][entry["algorithm"]] = []
        plot_data[dataset][entry["algorithm"]].append(entry["execution_time"])
    return plot_data


def plot_results(plot_data):
    """
    Create a bar plot for each dataset with execution times of each algorithm.
    """
    for (dataset,) in plot_data.items():
        plt.figure(figsize=(10, 6))
        labels = list(algorithms.keys())
        times = [
            sum(algorithms[alg]) / len(algorithms[alg]) for alg in labels
        ]  # Average times

        plt.bar(labels, times, color="skyblue")
        plt.xlabel("Algorithm")
        plt.ylabel("Average Execution Time (seconds)")
        plt.title(f"Average Execution Times by Algorithm for {dataset}")
        plt.xticks(rotation=45)
        plt.tight_layout()
        plt.show()


if __name__ == "__main__":
    results_file = os.path.join(results_dir, "results.json")

    results = load_results(results_file)
    plot_data = prepare_plot_data(results)
    plot_results(plot_data)
