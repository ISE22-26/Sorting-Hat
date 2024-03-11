import os
import random


def generate_dataset(size):
    return [random.randint(0, 100) for _ in range(size)]


if __name__ == "__main__":

    script_path = os.path.dirname(__file__)
    competition_root = os.path.join(script_path, "..")
    datasets_path = os.path.join(competition_root, "datasets")

    # Ensure the datasets directory exists
    os.makedirs(datasets_path, exist_ok=True)

    # Generate dataset with 9000 items
    with open(os.path.join(datasets_path, "dataset_9000.txt"), "w") as f:
        f.write(",".join(map(str, generate_dataset(9000))))

    # Generate dataset with 900000 items
    with open(os.path.join(datasets_path, "dataset_900000.txt"), "w") as f:
        f.write(",".join(map(str, generate_dataset(900000))))
