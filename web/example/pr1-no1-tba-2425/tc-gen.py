import random
import subprocess

domain_alphabet = ['a', 'b', 'c']
random_seed = 42  # Set a random seed for reproducibility

def pure_random_string(n):
    """Generate a random string of length n."""
    return ''.join(random.choices(domain_alphabet, k=n))

def pure_random_int(n):
    """Generate a random integer of n digits."""
    return str(random.randint(10**(n-1), 10**n - 1))

def random_half_string(n, c):
    """Generate a random string of length n with half fixed of same character of c and half randomized lowercase letters."""
    half_n = n // 2
    return c * half_n + ''.join(random.choices(domain_alphabet, k=n - half_n))

def random_even_parity_string(n, c):
    """Generate a random string of length n with each character at index even (0, 2, 4, ...) is randomized."""
    return ''.join(
        random.choice(domain_alphabet) if i % 2 == 0 else c
        for i in range(0, n)
    )
    

def generate_tc(subtask, number, input):
    input_filename = f"pr1-no1-tba-2425_{subtask}_{number}.in"
    output_filename = f"pr1-no1-tba-2425_{subtask}_{number}.out"

    with open("testcases/" + input_filename, "w") as file:
        file.write(input)
    
    with open("testcases/" + output_filename, "w") as file:
        result = subprocess.run(
            ["java", "-jar", "jflap-core.jar", "runonce", "solution.jff"],
            input=input.encode(),
            capture_output=True
        )
        file.write(result.stdout.decode())

if __name__ == "__main__":
    random.seed(random_seed)
    generate_tc(1, 1, pure_random_string(10))
    generate_tc(1, 2, pure_random_string(10))
    generate_tc(1, 3, random_half_string(10, 'a'))
    generate_tc(1, 4, random_half_string(10, 'b'))
    generate_tc(1, 5, random_half_string(10, 'c'))

    generate_tc(2, 1, pure_random_string(1000))
    generate_tc(2, 2, pure_random_string(1000))
    generate_tc(2, 3, random_half_string(1000, 'a'))
    generate_tc(2, 4, random_half_string(1000, 'b'))
    generate_tc(2, 5, random_half_string(1000, 'c'))
    generate_tc(2, 6, random_even_parity_string(1000, 'a'))
    generate_tc(2, 7, random_even_parity_string(1000, 'b'))
    generate_tc(2, 8, random_even_parity_string(1000, 'c'))

