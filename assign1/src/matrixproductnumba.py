from time import clock_gettime, CLOCK_PROCESS_CPUTIME_ID
from numba import njit


@njit(fastmath=False)
def mult(size_a: int, size_b: int):
    a = [1.0] * size_a * size_a
    b = [i + 1 for i in range(size_b) for j in range(size_b)]
    c = [0] * size_a * size_b

    for i in range(size_a):
        for j in range(size_b):
            for k in range(size_a):
                c[i * size_a + j] += a[i * size_a + k] * b[k * size_b + j]


@njit(fastmath=False)
def mult_line(size_a: int, size_b: int):
    a = [1.0] * size_a * size_a
    b = [i + 1 for i in range(size_b) for j in range(size_b)]
    c = [0] * size_a * size_b


    for i in range(size_a):
        for k in range(size_a):
            for j in range(size_b):
                c[i * size_a + j] += a[i * size_a + k] * b[k * size_b + j]

    


def main():
    op = 1

    while op:
        print("1. Multiplication")
        print("2. Line Multiplication")
        # print("3. Block Multiplication")
        op = int(input("Selection?: "))

        if op == 0:
            break

        lines = cols = int(input("Dimensions: lins=cols ? "))

        if op == 1:

            start = clock_gettime(CLOCK_PROCESS_CPUTIME_ID)

            mult(lines, cols)

            end = clock_gettime(CLOCK_PROCESS_CPUTIME_ID)
            print(f"Time: {end - start:.8f} seconds\n")
        if op == 2:
            start = clock_gettime(CLOCK_PROCESS_CPUTIME_ID)

            mult_line(lines, cols)

            end = clock_gettime(CLOCK_PROCESS_CPUTIME_ID)

            print(f"Time: {end - start:.8f} seconds\n")

        # if op == 3:
        #     size_block = int(input("Block Size? "))
        #     mult_block(lines, cols, size_block);


if __name__ == "__main__":
    main()
