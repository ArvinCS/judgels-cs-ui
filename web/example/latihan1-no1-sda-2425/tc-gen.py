import os
import random
import subprocess

slug = "latihan1-no1-sda-2425"
random_seed = 42  # Set a random seed for reproducibility

def random_even_number(start, end):
    bound_start = (start-1)//2 + 1
    bound_end = end//2

    return random.randint(bound_start, bound_end) * 2

def random_odd_number(start, end):
    bound_start = (start-1)//2 + 1
    bound_end = (end+1)//2

    return random.randint(bound_start, bound_end) * 2 - 1

def random_number(start, end):
    return random.randint(start, end)

def generate_sample_tc(number, input):
    input_filename = f"{slug}_sample_{number}.in"
    output_filename = f"{slug}_sample_{number}.out"

    os.makedirs("testcases", exist_ok=True)

    with open("testcases/" + input_filename, "w") as file:
        file.write(str(input))
    
    subprocess.run(["javac", "Solution.java"])
    with open("testcases/" + output_filename, "w") as file:
        result = subprocess.run(
            ["java", "Solution"],
            input=str(input).encode(),
            capture_output=True
        )
        file.write(result.stdout.decode())

def generate_tc(number, input):
    input_filename = f"{slug}_{number}.in"
    output_filename = f"{slug}_{number}.out"

    os.makedirs("testcases", exist_ok=True)

    with open("testcases/" + input_filename, "w") as file:
        file.write(str(input))
    
    subprocess.run(["javac", "Solution.java"])
    with open("testcases/" + output_filename, "w") as file:
        result = subprocess.run(
            ["java", "Solution"],
            input=str(input).encode(),
            capture_output=True
        )
        file.write(result.stdout.decode())

MAX_N = 1_000_000

if __name__ == "__main__":
    random.seed(random_seed)
    generate_sample_tc(1, str(2))
    generate_sample_tc(2, str(1001))
    generate_tc(1, random_odd_number(1, 1))
    generate_tc(2, random_number(1, MAX_N))
    generate_tc(3, random_even_number(1, MAX_N))
    generate_tc(4, random_odd_number(1, MAX_N))
    generate_tc(5, random_odd_number(1, MAX_N//2))
    generate_tc(6, random_odd_number(MAX_N//2, MAX_N))
    generate_tc(7, random_even_number(MAX_N//2, MAX_N))
    generate_tc(8, str(998244353))


