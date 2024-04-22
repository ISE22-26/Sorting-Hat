import os
import numpy as np
from multiprocessing import Pool


def generate_and_write_chunk(args):
    dataset_path, start, end = args
    chunk = np.random.randint(0, 100, size=(end - start))
    with open(dataset_path, "ab") as f:
        np.savetxt(f, chunk, delimiter=",", fmt="%d")


def generate_dataset_parallel(
    dataset_path, total_size, chunk_size=10000, num_processes=4
):
    os.makedirs(os.path.dirname(dataset_path), exist_ok=True)
    # Clear file content before writing
    with open(dataset_path, "wb") as f:
        pass
    # Calculate ranges for each process
    step_size = max(1, total_size // num_processes)
    ranges = [
        (dataset_path, i, min(i + step_size, total_size))
        for i in range(0, total_size, step_size)
    ]
    with Pool(processes=num_processes) as pool:
        pool.map(generate_and_write_chunk, ranges)


if __name__ == "__main__":
    script_path = os.path.dirname(__file__)
    competition_root = os.path.join(script_path, "..")
    datasets_path = os.path.join(competition_root, "datasets")

    # Generate dataset with 9000 items
    generate_dataset_parallel(os.path.join(datasets_path, "dataset_9000.txt"), 9000)

    # Generate dataset with 900000 items
    generate_dataset_parallel(os.path.join(datasets_path, "dataset_900000.txt"), 900000)

    # Generate dataset with 500000000 items
    generate_dataset_parallel(
        os.path.join(datasets_path, "dataset_500000000.txt"), 500000000
    )
