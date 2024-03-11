# Purpose: Script to automate the setup of a competition for evaluating Java sorting algorithms

import os
import subprocess
import json
import time
import random


class CompetitionSetup:
    def __init__(self, submissions_dir, results_dir, test_input_file):
        """
        Initialize the competition setup with directory paths and test input file.
        """
        self.submissions_dir = submissions_dir
        self.results_dir = results_dir
        self.test_input_file = test_input_file
        self.setup_directories()

    def setup_directories(self):
        """
        Ensure that the submissions and results directories exist.
        """
        os.makedirs(self.submissions_dir, exist_ok=True)
        os.makedirs(self.results_dir, exist_ok=True)

    def compile_java(self, java_file):
        """
        Compiles a Java file and returns True if successful, False otherwise.
        """
        directory, java_file_name = os.path.split(java_file)
        try:
            subprocess.check_output(
                ["javac", java_file], stderr=subprocess.STDOUT, cwd=directory
            )
            return True
        except subprocess.CalledProcessError as e:
            print(f"Compilation failed for {java_file}: {e.output.decode()}")
            return False

    def run_java(self, class_name, directory):
        """
        Runs a compiled Java class and captures execution time.
        """
        start_time = time.time()
        try:
            process = subprocess.run(
                ["java", class_name, self.test_input_file],
                text=True,
                capture_output=True,
                timeout=1000,
                cwd=directory,
            )
            execution_time = time.time() - start_time
            if process.returncode == 0:
                return (True, execution_time, process.stdout)
            else:
                print(f"Error running {class_name}: {process.stderr}")
                return (False, execution_time, process.stderr)
        except subprocess.TimeoutExpired as e:
            print(f"Execution timed out for {class_name}")
            return (False, time.time() - start_time, "Timeout")

    def evaluate_submissions(self):
        """
        Evaluates all submissions in the submissions directory, compiling and running each.
        """
        results = []
        for root, dirs, files in os.walk(self.submissions_dir):
            for file in files:
                if file.endswith(".java"):
                    java_file = os.path.join(root, file)
                    class_name = os.path.splitext(file)[0]
                    student_id = os.path.basename(root)
                    directory = os.path.dirname(java_file)
                    if self.compile_java(java_file):
                        success, execution_time, output = self.run_java(
                            class_name, directory
                        )

                        test_input_file_name = os.path.basename(self.test_input_file)
                        if success:
                            # convert from a string list to a list of numbers
                            list_output = list(map(int, output.split(",")))
                            is_sorted = list_output == sorted(list_output)
                            results.append(
                                {
                                    "student_id": student_id,
                                    "algorithm": class_name,
                                    "dataset": test_input_file_name,
                                    "execution_time": execution_time,
                                    "is_sorted": is_sorted,
                                }
                            )
                        else:
                            results.append(
                                {
                                    "student_id": student_id,
                                    "algorithm": class_name,
                                    "dataset": test_input_file_name,
                                    "error": output,
                                }
                            )
        self.save_results(results)

    def save_results(self, results):
        """
        Saves the evaluation results to a JSON file in the results directory.
        """
        results_file = os.path.join(self.results_dir, "results.json")
        # Load existing results
        if os.path.exists(results_file):
            with open(results_file, "r") as f:
                existing_results = json.load(f)
        else:
            existing_results = []

        # Append new results
        existing_results.extend(results)

        # Write all results back to the file
        with open(results_file, "w") as f:
            json.dump(existing_results, f, indent=4)

    @staticmethod
    def run(submissions_dir, results_dir, test_input_file):
        """
        Static method to create a CompetitionSetup instance and run the evaluation.
        """
        setup = CompetitionSetup(submissions_dir, results_dir, test_input_file)
        setup.evaluate_submissions()


# Example usage (Uncomment and modify the paths and input as necessary)


def random_array(size=1000):
    random.seed(0)
    # generate an array seperated by ,
    return ",".join(str(random.randint(0, 1000)) for _ in range(size))


if __name__ == "__main__":
    script_path = os.path.dirname(__file__)
    competition_root = os.path.join(script_path, "..")
    submission_dir = os.path.join(competition_root, "submissions")
    results_dir = os.path.join(competition_root, "results")
    dataset_900000 = os.path.join(competition_root, "datasets", "dataset_900000.txt")
    dataset_9000 = os.path.join(competition_root, "datasets", "dataset_9000.txt")
    print(os.path.join(competition_root, "datasets", "dataset_900000.txt"))
    CompetitionSetup.run(submission_dir, results_dir, dataset_900000)
    CompetitionSetup.run(submission_dir, results_dir, dataset_9000)
