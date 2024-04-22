import json
import multiprocessing
import os
import subprocess
import time
import psutil
from threading import Thread


def monitor_process(process_pid, metrics):
    """Monitor process resource usage."""
    try:
        ps_process = psutil.Process(process_pid)
        while ps_process.is_running():
            mem_info = ps_process.memory_info()
            cpu_times = ps_process.cpu_times()
            metrics["memory_used_kb"] = max(
                metrics.get("memory_used_kb", 0), mem_info.rss / 1024
            )
            metrics["cpu_time_used"] = max(
                metrics.get("cpu_time_used", 0), cpu_times.user + cpu_times.system
            )
            time.sleep(0.1)  # Polling interval
    except psutil.NoSuchProcess:
        pass  # Process has finished, exit monitoring


def compile_java(java_file):
    directory, _ = os.path.split(java_file)
    try:
        subprocess.check_output(
            ["javac", java_file], stderr=subprocess.STDOUT, cwd=directory
        )
        return True
    except subprocess.CalledProcessError as e:
        print(f"Compilation failed for {java_file}: {e.output.decode()}")
        return False


def is_output_sorted(process_stdout):
    """Check if the output from the process is sorted."""
    prev_value = float("-inf")
    for line in process_stdout:
        try:
            value = float(line.strip())
            if value < prev_value:
                return False
            prev_value = value
        except ValueError:
            return False
    return True


def run_java(submission_info):
    class_name, directory, test_input_file, dataset_id, results_dir = submission_info
    start_time = time.time()
    metrics = {}

    try:
        with subprocess.Popen(
            ["java", class_name, test_input_file],
            stdout=subprocess.PIPE,
            stderr=subprocess.PIPE,
            cwd=directory,
            text=True,
            bufsize=1,
        ) as process:
            monitor_thread = Thread(target=monitor_process, args=(process.pid, metrics))
            monitor_thread.start()

            sorted_output = is_output_sorted(process.stdout)
            process.communicate(timeout=1000)  # Ensure process termination
            monitor_thread.join()

            result = {
                "success": process.returncode == 0 and sorted_output,
                "execution_time": time.time() - start_time,
                "memory_used_kb": metrics.get("memory_used_kb"),
                "cpu_time_used": metrics.get("cpu_time_used"),
                "is_sorted": sorted_output,
            }

    except subprocess.TimeoutExpired:
        process.kill()
        result = {"success": False, "reason": "Timeout"}

    save_result(result, dataset_id, results_dir)
    return result


def compile_java(java_file):
    directory, _ = os.path.split(java_file)
    try:
        subprocess.check_output(
            ["javac", java_file], stderr=subprocess.STDOUT, cwd=directory
        )
        return True
    except subprocess.CalledProcessError as e:
        print(f"Compilation failed for {java_file}: {e.output.decode()}")
        return False


def evaluate_submission(java_file, test_input_file, dataset_id, results_dir):
    class_name = os.path.splitext(os.path.basename(java_file))[0]
    directory = os.path.dirname(java_file)
    if compile_java(java_file):
        return run_java(
            (class_name, directory, test_input_file, dataset_id, results_dir)
        )
    return None


def save_result(result, dataset_id, results_dir):
    results_file = os.path.join(results_dir, f"results_{dataset_id}.json")
    os.makedirs(results_dir, exist_ok=True)
    if os.path.exists(results_file):
        with open(results_file, "r") as file:
            existing_results = json.load(file)
    else:
        existing_results = []
    existing_results.append(result)
    with open(results_file, "w") as file:
        json.dump(existing_results, file, indent=4)


def worker_init():
    import signal

    signal.signal(signal.SIGINT, signal.SIG_IGN)


def main(submissions_dir, results_dir, test_input_file):
    dataset_id = os.path.basename(test_input_file).split(".")[0]
    args = [
        (os.path.join(root, file), test_input_file, dataset_id, results_dir)
        for root, _, files in os.walk(submissions_dir)
        for file in files
        if file.endswith(".java")
    ]
    with multiprocessing.Pool(
        processes=multiprocessing.cpu_count(), initializer=worker_init
    ) as pool:
        pool.starmap(evaluate_submission, args)


if __name__ == "__main__":
    script_path = os.path.dirname(os.path.abspath(__file__))
    competition_root = os.path.join(script_path, "..")
    submissions_dir = os.path.join(competition_root, "submissions")
    results_dir = os.path.join(competition_root, "results")
    datasets = ["dataset_900000.txt", "dataset_9000.txt"]
    for dataset in datasets:
        test_input_file = os.path.join(competition_root, "datasets", dataset)
        main(submissions_dir, results_dir, test_input_file)
